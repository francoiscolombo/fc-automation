package com.fc.tools.automation.actions;

import com.fc.tools.automation.parser.AbstractAction;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.logging.Logger;

public class Eval extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static ScriptEngine JS_ENGINE = (new ScriptEngineManager()).getEngineByName("JavaScript");

    @Override
    protected void execute() {
        this.exitCode = 1;
        getParameter("expression").ifPresent(exp -> {
            getParameter("result").ifPresent(varName -> {
                String expression = replaceVariables(exp);
                try {
                    Object result = JS_ENGINE.eval(expression);
                    setVariable(varName, result);
                    this.exitCode = 0;
                } catch (ScriptException e) {
                    LOGGER.warning(String.format("An exception happened while evaluating expression <%s>: %s", exp, e.getMessage()));
                    this.exitCode = 2;
                }
            });
        });
    }

}
