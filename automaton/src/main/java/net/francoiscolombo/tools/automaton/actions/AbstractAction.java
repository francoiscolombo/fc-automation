package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.eval.EvalCondition;
import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;
import net.francoiscolombo.tools.automaton.models.Stage;
import net.francoiscolombo.tools.automaton.models.Variable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the ancestor class for all the actions class.<br>
 *
 * @author François Colombo
 */
public abstract class AbstractAction implements IAction {

    // global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static Pattern VAR_PATTERN = Pattern.compile("\\{\\{\\s*(\\w+)\\s*\\}\\}");

    protected int exitCode = 0;
    protected Map<String, Variable> variables = new HashMap<>();
    protected boolean canExecute = true;
    private Stage stage;
    private final Map<String, String> parameters = new HashMap<>();

    private void registerVariables(final List<Variable> vars) {
        for (Variable v : vars) {
            this.variables.put(v.getName().trim().toUpperCase(), v);
        }
    }

    private List<Variable> getVariables() {
        return new ArrayList<>(this.variables.values());
    }

    private void registerParameters() {
        for (String key : this.stage.getParameters().keySet()) {
            String value = this.stage.getParameters().get(key);
            if (value != null) {
                this.parameters.put(key.trim().toUpperCase(), value);
            }
        }
    }

    private boolean checkCondition(final String expression) {
        if(expression != null) {
            //LOGGER.info(String.format("Checking now if condition %s is true...", expression));
            try {
                boolean eval = EvalCondition.build(replaceVariables(expression)).eval();
                LOGGER.info(String.format("Condition %s is %b...", expression, eval));
                return eval;
            } catch(RuntimeException runtimeException) {
                LOGGER.warning(String.format("Expression <%s> does not return a boolean... fails condition with message '%s'", expression, runtimeException.getMessage()));
            }
        } else {
            LOGGER.warning("Expression is null, can't evaluate. Return false.");
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
                if (variable.getValue() != null) {
                    result = result.replace(found, variable.getValue());
                }
            }
        }
        return result;
    }

    protected String getMandatoryParameter(final String key) throws ParameterNotFound {
        String value = this.parameters.getOrDefault(key.trim().toUpperCase(), "");
        if ("".equals(value)) {
            throw new ParameterNotFound(String.format("Missing mandatory parameter %s", key));
        }
        return replaceVariables(value);
    }

    protected String getParameter(final String key) {
        return replaceVariables(this.parameters.getOrDefault(key.trim().toUpperCase(), ""));
    }

    protected void setVariable(final String varName, final Object value) {
        Variable v = this.variables.getOrDefault(varName.trim().toUpperCase(), null);
        if (v == null) {
            v = new Variable();
            v.setName(varName);
            //LOGGER.info(String.format("Define new variable <%s>...", varName));
        }
        v.setValue(String.valueOf(value));
        this.variables.put(varName.trim().toUpperCase(), v);
        //LOGGER.info(String.format("Set value <%s> to variable <%s>", value, varName));
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
        if (this.stage.getCondition() != null && !"".equals(this.stage.getCondition())) {
            //LOGGER.info("[X] Check condition "+this.stage.getCondition());
            this.canExecute = checkCondition(this.stage.getCondition());
        }
        if (this.canExecute) {
            if(this.stage.getLoop() != null && !"".equals(this.stage.getLoop().getIndex())) {
                String index = this.stage.getLoop().getIndex();
                int range = this.stage.getLoop().getRange();
                //LOGGER.info("[X] "+this.stage.getLoop());
                if(range > 0) {
                    for(int i=1; i<=range; i++) {
                        setVariable(index, i);
                        execute();
                    }
                }
            } else if (this.stage.getForeach() != null && !this.stage.getForeach().getItems().isEmpty()) {
                //LOGGER.info("[X] "+this.stage.getForeach());
                for(String item : this.stage.getForeach().getItems()) {
                    setVariable("item", replaceVariables(item));
                    execute();
                }
            } else {
                execute();
            }
        }
        footer(start);
        return getVariables();
    }

    protected abstract void execute();

}
