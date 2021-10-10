package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;

public class Ping extends AbstractStatement {

    public Ping(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values)) {
            String host = values[0].internalString();
            try {
                if (InetAddress.getByName(host).isReachable(100)) {
                    printStream.printf("%s is reachable\n", host);
                }
            } catch (IOException e) {
                printStream.printf("%s is not reachable\n", host);
            }
        }
        return new Value(exitCode);
    }

}
