package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
        try {
            String destination = getMandatoryParameter("path");
            String template = getParameter("template");
            String contents = getParameter("body");
            if(!template.equals("")) {
                Path templatePath = Paths.get(template);
                try {
                    this.lines = Files.lines(templatePath);
                } catch (Exception ioex) {
                    LOGGER.warning(String.format("Exception <%s> while trying to load template from <%s>", ioex.getMessage(), template));
                    this.exitCode = 1;
                    this.lines = null;
                }
            } else if(!contents.equals("")) {
                String separator = System.getProperty("line.separator");
                this.lines = Arrays.stream(contents.split(separator));
            }
            if(this.lines != null) {
                File path = new File(destination);
                path.getParentFile().mkdirs();
                try (PrintWriter pw = new PrintWriter(path, StandardCharsets.UTF_8)) {
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
            }
        } catch (ParameterNotFound parameterNotFound) {
            LOGGER.warning(parameterNotFound.getMessage());
        }
    }

}
