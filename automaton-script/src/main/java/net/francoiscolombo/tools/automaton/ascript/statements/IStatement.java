package net.francoiscolombo.tools.automaton.ascript.statements;

import ascript.AScriptParser;
import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

public interface IStatement {
    Value visitStatement(Value... values);
}
