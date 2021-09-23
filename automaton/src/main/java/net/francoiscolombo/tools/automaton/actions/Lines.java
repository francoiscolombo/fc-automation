package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Lines extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Pattern pattern = null;

    private final List<String> content = new LinkedList<>();

    private String replace = null;

    private String after = null;

    private String before = null;

    /**
     * two mandatory parameters: file which is the file to update, and regexp which
     * allows to identify the lines to process in this file.<br>
     * then we have three different actions possibles:
     * <ul><li>replace - this will replace the lines by the value of this parameter</li>
     * <li>after - this will insert the line after every lines of the regexp</li>
     * <li>before - same but this time insert the line before</li></ul>
     * to delete the lines, simply use <em>replace</em> option and set an empty string,
     * this will just delete the lines identified by the regexp.
     */
    @Override
    protected void execute() {
        this.exitCode = 1;
        String value = getParameter("replace");
        if(!value.equals("")) {
            this.replace = value;
        } else {
            value = getParameter("after");
            if(!value.equals("")) {
                this.after = value;
            } else {
                value = getParameter("before");
                if(!value.equals("")) {
                    this.before = value;
                }
            }
        }
        try {
            final String file = getMandatoryParameter("file");
            final String regexp = getMandatoryParameter("regexp");
            final Path path = Paths.get(file);
            try {
                final Stream<String> lines = Files.lines(path);
                try {
                    this.pattern = Pattern.compile(regexp);
                    lines.forEach(line -> {
                        Matcher matcher = this.pattern.matcher(line);
                        if (matcher.find()) {
                            if (this.before != null) {
                                this.content.add(this.before);
                                this.content.add(line);
                            }
                            if (this.replace != null) {
                                if (!"".equals(this.replace)) {
                                    this.content.add(line.replaceAll(regexp, this.replace));
                                }
                            }
                            if (this.after != null) {
                                this.content.add(line);
                                this.content.add(this.after);
                            }
                        } else {
                            this.content.add(line);
                        }
                    });
                    if (!this.content.isEmpty()) {
                        // rewrite the file
                        try (PrintWriter pw = new PrintWriter(path.toFile().getAbsolutePath(), StandardCharsets.UTF_8)) {
                            this.content.forEach(pw::println);
                            this.exitCode = 0;
                        } catch (IOException ioex) {
                            LOGGER.warning(String.format("Exception <%s> while trying to update file <%s>", ioex.getMessage(), path.toFile().getName()));
                            this.exitCode = 4;
                        }
                    }
                } catch (Exception pex) {
                    LOGGER.warning(String.format("Exception <%s> while trying to compile regexp <%s>", pex.getMessage(), regexp));
                    this.exitCode = 3;
                }
            } catch (Exception ioex) {
                LOGGER.warning(String.format("Exception <%s> while trying to load file <%s>", ioex.getMessage(), file));
                this.exitCode = 2;
            }
        } catch (ParameterNotFound parameterNotFound) {
            LOGGER.warning(parameterNotFound.getMessage());
        }
    }

}