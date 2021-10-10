package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Unzip extends AbstractZipUnzipStatement {

    public Unzip(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String archivePath = values[0].internalString();
            String folderPath = values[1].internalString();
            try {
                Path src = Paths.get(archivePath);
                Path dst = Paths.get(folderPath);
                unzip(src, dst);
                exitCode = 0;
            } catch (Exception ex) {
                errorStream.printf("Something went wrong while trying to unzip <%s> to <%s>: %s", archivePath, folderPath, ex.getMessage());
                exitCode = 2;
            }

        }
        return new Value(exitCode);
    }

}
