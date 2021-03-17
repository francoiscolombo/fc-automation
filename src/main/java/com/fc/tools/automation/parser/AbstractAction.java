package com.fc.tools.automation.parser;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the ancestor class for all the actions class.<br>
 *
 * @author BI8XQ - François Colombo
 */
public abstract class AbstractAction implements Action {

    // global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static Pattern VAR_PATTERN = Pattern.compile("\\{\\{\\s*(\\w+)\\s*\\}\\}");

    private final static ScriptEngine JS_ENGINE = (new ScriptEngineManager()).getEngineByName("JavaScript");
    protected int exitCode = 0;
    protected Map<String, Variable> variables = new HashMap<>();
    protected boolean canExecute = true;
    private Stage stage;
    private final Map<String, String> parameters = new HashMap<>();

    private void registerVariables(final List<Variable> vars) {
        vars.forEach(v -> this.variables.put(v.getName().trim().toUpperCase(), v));
    }

    private List<Variable> getVariables() {
        return new ArrayList<>(this.variables.values());
    }

    private void registerParameters() {
        for (String key : this.stage.getParameters().keySet()) {
            String value = this.stage.getParameters().get(key);
            if (value != null) {
                this.parameters.put(key.trim().toUpperCase(), replaceVariables(value));
            }
        }
    }

    private boolean checkCondition(final String expression) {
        try {
            Object eval = JS_ENGINE.eval(replaceVariables(expression));
            if (eval instanceof Boolean) {
                return (Boolean) eval;
            } else {
                LOGGER.warning(String.format("Expression <%s> does not return a boolean... fails condition.", expression));
            }
        } catch (ScriptException e) {
            LOGGER.warning(String.format("Expression <%s> cannot be evaluated: message is '%s'", expression, e.getMessage()));
        }
        return false;
    }

    private void title() {
        String title = String.format("[Action:%s] ", this.stage.getAction());
        if (title.length() < 120) {
            String dash = new String(new char[120 - title.length()]).replace("\0", "-");
            LOGGER.info(title.concat(dash));
        } else {
            LOGGER.info(title);
        }
        LOGGER.info(replaceVariables(this.stage.getDisplay()));
    }

    private void footer(final Instant start) {
        Instant end = Instant.now();
        Duration ellapsed = Duration.between(start, end);
        LocalTime ellapsedTime = LocalTime.ofNanoOfDay(ellapsed.toNanos());
        if (this.exitCode != 0) {
            String message = String.format(">>> Return code is %d. An error might have happened.", this.exitCode);
            String dash = new String(new char[120 - message.length()]).replace("\0", "-");
            LOGGER.info(message.concat(dash));
        } else {
            String message = String.format(">>> Execute action completed in %s", ellapsedTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
            String dash = new String(new char[120 - message.length()]).replace("\0", "-");
            LOGGER.info(message.concat(dash));

        }
    }

    protected String replaceVariables(final String c) {
        String result = c;
        Matcher m = VAR_PATTERN.matcher(c);
        while (m.find()) {
            String found = m.group();
            String var = m.group(1);
            Variable variable = this.variables.getOrDefault(var.trim().toUpperCase(), null);
            if (variable == null) {
                result = result.replace(found, String.format("{{%s}}", var));
            } else {
                if (variable.getValue().isPresent()) {
                    result = result.replace(found, variable.getValue().get());
                }
            }
        }
        return result;
    }

    protected Optional<String> getParameter(final String key) {
        String value = this.parameters.getOrDefault(key.trim().toUpperCase(), "");
        if ("".equals(value)) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    protected void setVariable(final String varName, final Object value) {
        Variable v = this.variables.getOrDefault(varName.trim().toUpperCase(), null);
        if (v == null) {
            LOGGER.warning(String.format("ERROR: variable <%s> is undefined...", varName));
        } else {
            v.setValue(Optional.of(String.valueOf(value)));
            this.variables.put(varName.trim().toUpperCase(), v);
            LOGGER.info(String.format("Set value <%s> to variable <%s>", value, varName));
        }
    }

    @Override
    public void registerStage(final Stage stage) {
        this.stage = stage;
    }

    @Override
    public List<Variable> runTask(final List<Variable> variables) {
        Instant start = Instant.now();
        this.canExecute = true;
        this.exitCode = 0;
        title();
        registerVariables(variables);
        registerParameters();
        // only execute if the condition is true
        if (this.stage.getCondition().isPresent()) {
            this.stage.getCondition().ifPresent(c -> this.canExecute = checkCondition(c));
        }
        if (this.canExecute) {
            execute();
        }
        footer(start);
        return getVariables();
    }

    protected abstract void execute();

}
