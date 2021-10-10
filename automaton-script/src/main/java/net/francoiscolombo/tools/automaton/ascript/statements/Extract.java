package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.PrintStream;

public class Extract extends AbstractArchiveStatement {

    public Extract(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        if(check(0, values) && check(1, values)) {
            Value archivePath = values[0];
            Value folderPath = values[1];
            if (archivePath.internalString().endsWith(".tar.gz")) {
                return new Value(unTarGzFile(archivePath.internalString(), folderPath.internalString()));
            } else if (archivePath.internalString().endsWith(".tar")) {
                return new Value(unTarFile(archivePath.internalString(), folderPath.internalString()));
            } else {
                errorStream.printf("We can uncompress only .tar.gz or .tar archive, and you ask to process archive %s. Sorry, we can't do it.\n", archivePath.internalString());
                return new Value(4);
            }
        }
        return new Value(1);
    }

}
