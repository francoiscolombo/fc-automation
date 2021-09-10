package net.francoiscolombo.tools.automaton.actions;

import java.util.logging.Logger;

public class Package extends AbstractAction {

    // global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        LOGGER.warning("NOT IMPLEMENTED YET");
    }

}
