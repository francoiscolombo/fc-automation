package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.PrintStream;

public abstract class AbstractStatement implements IStatement {

    protected final PrintStream printStream;
    protected final PrintStream errorStream;

    public AbstractStatement(PrintStream printStream, PrintStream errorStream) {
        this.printStream = printStream;
        this.errorStream = errorStream;
    }

    protected boolean check(int index, Value... values) {
        return values.length > index;
    }

}
