package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Show extends AbstractStatement {

    public Show(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String parameter = values[0].internalString();
            Path path = Paths.get(parameter);
            if(path.toFile().exists() && path.toFile().isFile()) {
                try {
                    Files.lines(path).forEach(printStream::println);
                    exitCode = 0;
                } catch (IOException ioex) {
                    errorStream.printf("An exception happened while trying to show content of file %s, the message is '%s'\n", path, ioex.getMessage());
                    exitCode = 2;
                }
            } else {
                printStream.println(parameter);
                exitCode = 0;
            }
        }
        return new Value(exitCode);
    }

}
