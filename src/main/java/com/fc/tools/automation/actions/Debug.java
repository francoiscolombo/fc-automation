package com.fc.tools.automation.actions;

import com.fc.tools.automation.parser.AbstractAction;

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
        getParameter("message").ifPresent(msg -> {
            LOGGER.info(String.format("DBG: %s", msg));
            this.exitCode = 0;
        });
        getParameter("file").ifPresent(path -> {
            try {
                Files.lines(Paths.get(path)).forEach(line -> LOGGER.info(String.format("DBG: %s", line)));
                this.exitCode = 0;
            } catch (IOException ioex) {
                LOGGER.warning(String.format("An exception happened while trying to show content of file %s, the message is '%s'", path, ioex.getMessage()));
            }
        });
    }

}
