package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.ascript.AScriptInterpreterTask;
import net.francoiscolombo.tools.automaton.ascript.exceptions.AScriptException;
import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Script extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        try {
            String scriptPath = getMandatoryParameter("script");
            AScriptInterpreterTask scriptInterpreterTask = new AScriptInterpreterTask(scriptPath, System.in, System.out, System.err);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Integer> exitCode = executorService.submit(scriptInterpreterTask);
            try {
                this.exitCode = exitCode.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.warning(String.format("Exception happened while trying to execute script, the message is '%s'", e.getMessage()));
            }
        } catch (ParameterNotFound parameterNotFound) {
            LOGGER.warning(parameterNotFound.getMessage());
        }
    }

}
