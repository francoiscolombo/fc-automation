package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.PrintStream;

public class Compress extends AbstractArchiveStatement {

    public Compress(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        if(check(0, values) && check(1, values)) {
            Value folderPath = values[0];
            Value archivePath = values[1];
            return new Value(createTarGzFromFolder(folderPath.internalString(), archivePath.internalString()));
        }
        return new Value(1);
    }

}
