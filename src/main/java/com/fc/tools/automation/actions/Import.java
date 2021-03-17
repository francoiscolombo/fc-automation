package com.fc.tools.automation.actions;

import com.fc.tools.automation.parser.AbstractAction;
import com.fc.tools.automation.parser.PlaybookParser;

import java.util.logging.Logger;

public class Import extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        getParameter("playbook").ifPresent(playbookPath -> {
            LOGGER.info(String.format("-=[ Import of playbook <%s> ]=-", playbookPath));
            PlaybookParser.build(playbookPath).runPipeline();
            LOGGER.info(String.format("-=[ Execution of playbook <%s> done ]=-", playbookPath));
            this.exitCode = 0;
        });
    }

}
