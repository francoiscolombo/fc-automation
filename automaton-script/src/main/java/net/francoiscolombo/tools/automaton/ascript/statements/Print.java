package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.PrintStream;

public class Print extends AbstractStatement {

    public Print(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        Value value = values[0];
        if (value.isNumber()) {
            printStream.println(value.internalNumber());
        } else {
            printStream.println(value.internalString());
        }
        return value;
    }

}
