package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.PrintStream;

public class Packages extends AbstractStatement {

    public Packages(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        printStream.println("--- NOT IMPLEMENTED YET ---");
        for(Value v : values) {
            printStream.printf("> %s\n", v);
        }
        printStream.println("---------------------------");
        return new Value(exitCode);
    }

}
