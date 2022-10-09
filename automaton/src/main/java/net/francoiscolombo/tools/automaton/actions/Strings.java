package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;
import net.francoiscolombo.tools.automaton.models.Variable;

import java.util.logging.Logger;

public class Strings extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        try {
            String varVarName = getMandatoryParameter("var");
            Variable var = this.variables.getOrDefault(varVarName.trim().toUpperCase(), null);
            String resultVarName = getParameter("result");
            if(this.variables.getOrDefault(varVarName.trim().toUpperCase(), null) == null) {
                resultVarName = varVarName;
            }
            String filter = getMandatoryParameter("filter");
            if(var != null) {
                String str = var.getValue().trim();
                if("lower".equalsIgnoreCase(filter)) {
                    var.setValue(str.toLowerCase());
                } else if("upper".equalsIgnoreCase(filter)) {
                    var.setValue(str.toUpperCase());
                } else if("concat".equalsIgnoreCase(filter)) {
                    String value = getMandatoryParameter("string");
                    var.setValue(str.concat(value));
                } else if("left".equalsIgnoreCase(filter)) {
                    Integer length = Integer.getInteger(getMandatoryParameter("length"));
                    var.setValue(str.substring(0, length));
                } else if("right".equalsIgnoreCase(filter)) {
                    Integer length = Integer.getInteger(getMandatoryParameter("length"));
                    var.setValue(str.substring(str.length()-length,length));
                } else if("substring".equalsIgnoreCase(filter)) {
                    Integer starts = Integer.getInteger(getMandatoryParameter("starts"));
                    Integer length = Integer.getInteger(getMandatoryParameter("length"));
                    var.setValue(str.substring(starts,length));
                } else if("replace".equalsIgnoreCase(filter)) {
                    String regex = getMandatoryParameter("regex");
                    String value = getMandatoryParameter("string");
                    var.setValue(str.replaceAll(regex, value));
                } else if("startsWith".equalsIgnoreCase(filter)) {
                    String value = getMandatoryParameter("string");
                    var.setValue(String.valueOf(str.startsWith(value)));
                } else if("endsWith".equalsIgnoreCase(filter)) {
                    String value = getMandatoryParameter("string");
                    var.setValue(String.valueOf(str.endsWith(value)));
                } else if("contains".equalsIgnoreCase(filter)) {
                    String value = getMandatoryParameter("string");
                    var.setValue(String.valueOf(str.contains(value)));
                } else if("matches".equalsIgnoreCase(filter)) {
                    String regex = getMandatoryParameter("regex");
                    var.setValue(String.valueOf(str.matches(regex)));
                }
                setVariable(resultVarName, var);
                this.exitCode = 0;
            }
        } catch (NumberFormatException | ParameterNotFound numberFormat) {
            LOGGER.warning(numberFormat.getMessage());
        }
    }

}
