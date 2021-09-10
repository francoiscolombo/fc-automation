package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.eval.EvalNumExpr;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class Eval extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        try {
            String exp = getMandatoryParameter("expression");
            String varName = getMandatoryParameter("result");
            BigDecimal result = EvalNumExpr.build(replaceVariables(exp)).parse();
            setVariable(varName, result);
            this.exitCode = 0;
        } catch (ParameterNotFound parameterNotFound) {
            LOGGER.warning(parameterNotFound.getMessage());
        }
    }

}
