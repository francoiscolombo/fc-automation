package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.IOException;
import java.io.PrintStream;

public class Zip extends AbstractZipUnzipStatement {

    public Zip(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String folderPath = values[0].internalString();
            String archivePath = values[1].internalString();
            try {
                zip(folderPath, archivePath);
                exitCode = 0;
            } catch (IOException ioex) {
                errorStream.printf("Cannot create archive file <%s> from folder <%s> - %s", archivePath, folderPath, ioex.getMessage());
                exitCode = 2;
            }
        }
        return new Value(exitCode);
    }

}
