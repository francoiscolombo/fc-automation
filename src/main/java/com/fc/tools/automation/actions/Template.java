package com.fc.tools.automation.actions;

import com.fc.tools.automation.parser.AbstractAction;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Template extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Stream<String> lines = null;

    @Override
    protected void execute() {
        this.exitCode = 1;
        getParameter("template").ifPresent(template -> {
            Path templatePath = Paths.get(template);
            try {
                this.lines = Files.lines(templatePath);
            } catch (Exception ioex) {
                LOGGER.warning(String.format("Exception <%s> while trying to load template from <%s>", ioex.getMessage(), template));
                this.exitCode = 1;
                this.lines = null;
            }
        });
        getParameter("body").ifPresent(contents -> {
            String separator = System.getProperty("line.separator");
            this.lines = Arrays.asList(contents.split(separator)).stream();
        });
        if (this.lines != null) {
            getParameter("path").ifPresent(destination -> {
                File path = new File(destination);
                path.getParentFile().mkdirs();
                try (PrintWriter pw = new PrintWriter(path, "UTF-8")) {
                    this.lines.forEachOrdered(line -> {
                        String l = replaceVariables(line);
                        LOGGER.info(String.format("> %s", l));
                        pw.println(l);
                    });
                    this.lines.close();
                    LOGGER.info(String.format(">>> template written in file <%s>", destination));
                    this.exitCode = 0;
                } catch (IOException ioex) {
                    LOGGER.warning(String.format("Exception <%s> while trying to copy template to <%s>", ioex.getMessage(), destination));
                    this.exitCode = 2;
                }
            });
        }
    }

}
