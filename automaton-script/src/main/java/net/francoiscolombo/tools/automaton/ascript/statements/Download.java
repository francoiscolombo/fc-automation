package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Download extends AbstractStatement {

    public Download(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String url = values[0].internalString();
            String path = values[1].internalString();
            try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                printStream.printf("<%s> successfully copied to destination <%s>\n", url, path);
                exitCode = 0;
            } catch (FileNotFoundException e) {
                errorStream.printf("Source URL <%s> is not found: %s\n", url, e.getMessage());
                exitCode = 2;
            } catch (IOException e) {
                errorStream.printf("I/O Exception while trying to copy <%s> to destination <%s> => %s\n", url, path, e.getMessage());
                exitCode = 3;
            }
        }
        return new Value(exitCode);
    }

}
