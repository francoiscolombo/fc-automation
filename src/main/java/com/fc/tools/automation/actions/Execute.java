package com.fc.tools.automation.actions;

import com.fc.tools.automation.parser.AbstractAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Execute extends AbstractAction {

    // global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        getParameter("command").ifPresent(
                command -> {
                    LOGGER.info(String.format("Operating System: %s", System.getProperty("os.name")));
                    ProcessBuilder builder = new ProcessBuilder();
                    if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
                        builder.command("cmd.exe", "/c", command);
                    } else if (System.getProperty("os.name").toUpperCase().contains("LINUX") || System.getProperty("os.name").toUpperCase().contains("AIX")) {
                        builder.command("sh", "-c", command);
                    }
                    getParameter("dir").ifPresent(dir -> {
                        builder.directory(new File(dir));
                    });
                    try {
                        Process process = builder.start();
                        try (BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
                             BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                            StringBuffer sout = new StringBuffer();
                            String line;
                            while ((line = stdout.readLine()) != null) {
                                LOGGER.info("> " + line);
                                sout.append(line);
                            }
                            while ((line = stderr.readLine()) != null) {
                                LOGGER.warning("# " + line);
                            }
                            this.exitCode = process.waitFor();
                            getParameter("result").ifPresent(varName -> {
                                setVariable(varName, sout.toString());
                            });
                        }
                    } catch (IOException ex) {
                        LOGGER.severe("### ERROR ### - something went wrong while trying to execute command <" + command + "> - " + ex.getMessage());
                        this.exitCode = 2;
                    } catch (InterruptedException ex) {
                        LOGGER.severe("### ERROR ### - something went wrong while trying to execute command <" + command + "> - " + ex.getMessage());
                        this.exitCode = 3;
                    }
                });
    }

}
