package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.*;

public class Execute extends AbstractStatement {

    final private StringBuilder output = new StringBuilder();

    public Execute(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String command = values[0].internalString();
            String dir = values[1].internalString();
            printStream.printf("Operating System: %s\n", System.getProperty("os.name"));
            ProcessBuilder builder = new ProcessBuilder();
            if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
                builder.command("cmd.exe", "/c", command);
            } else if (System.getProperty("os.name").toUpperCase().contains("LINUX") || System.getProperty("os.name").toUpperCase().contains("AIX")) {
                builder.command("sh", "-c", command);
            }
            if(!dir.equals("")) {
                builder.directory(new java.io.File(dir));
            }
            try {
                Process process = builder.start();
                try (BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
                     BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = stdout.readLine()) != null) {
                        printStream.println("> " + line);
                        output.append(line);
                    }
                    while ((line = stderr.readLine()) != null) {
                        errorStream.println("# " + line);
                    }
                    exitCode = process.waitFor();
                }
            } catch (IOException ex) {
                errorStream.printf("### ERROR ### - something went wrong while trying to execute command <%s> - %s\n", command, ex.getMessage());
                exitCode = 2;
            } catch (InterruptedException ex) {
                errorStream.printf("### ERROR ### - something interrupted execution of command <%s> - %s\n", command, ex.getMessage());
                exitCode = 3;
            }
        }
        return new Value(exitCode);
    }

    public String getOutput() {
        return output.toString();
    }

}
