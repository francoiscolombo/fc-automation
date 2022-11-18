package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.models.Variable;
import net.francoiscolombo.tools.automaton.models.Variables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Debug extends AbstractAction {

    // global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        String var = getParameter("var");
        if(!var.equals("")) {
            boolean found = false;
            for(Variable v : Variables.global().all()) {
                if(v.getName().matches(var)) {
                    LOGGER.info(String.format("DBG: %s = '%s'", v.getName(), v.getValue()));
                    found = true;
                }
            }
            if(!found) {
                LOGGER.warning(String.format("DBG: no variable name matches '%s' to display", var));
            }
            this.exitCode = 0;
        } else {
            String msg = getParameter("message");
            if(!msg.equals("")) {
                LOGGER.info(String.format("DBG: %s", msg));
                this.exitCode = 0;
            } else {
                String path = getParameter("file");
                if(!path.equals("")) {
                    try {
                        Files.lines(Paths.get(path)).forEach(line -> {
                            LOGGER.info(String.format("DBG: %s", line));
                        });
                        this.exitCode = 0;
                    } catch (IOException ioex) {
                        LOGGER.warning(String.format("An exception happened while trying to show content of file %s, the message is '%s'", path, ioex.getMessage()));
                    }
                } else {
                    LOGGER.warning("Action debug need parameter <message> or <file>, none provided, no action done.");
                }
            }
        }
    }

}
