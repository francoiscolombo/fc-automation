package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LinesBefore extends AbstractStatement {

    public LinesBefore(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values) && check(2, values)) {
            final String file = values[0].internalString();
            final String regexp = values[1].internalString();
            final String before = values[2].internalString();
            final Path path = Paths.get(file);
            final List<String> content = new LinkedList<>();
            try {
                final Stream<String> lines = Files.lines(path);
                try {
                    final Pattern pattern = Pattern.compile(regexp);
                    lines.forEach(line -> {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            content.add(before);
                            content.add(line);
                        } else {
                            content.add(line);
                        }
                    });
                    if (!content.isEmpty()) {
                        // rewrite the file
                        try (PrintWriter pw = new PrintWriter(path.toFile().getAbsolutePath(), StandardCharsets.UTF_8)) {
                            content.forEach(pw::println);
                            exitCode = 0;
                        } catch (IOException ioex) {
                            errorStream.printf("Exception <%s> while trying to update file <%s>\n", ioex.getMessage(), path.toFile().getName());
                            exitCode = 4;
                        }
                    }
                } catch (Exception pex) {
                    errorStream.printf("Exception <%s> while trying to compile regexp <%s>\n", pex.getMessage(), regexp);
                    exitCode = 3;
                }
            } catch (Exception ioex) {
                errorStream.printf("Exception <%s> while trying to load file <%s>\n", ioex.getMessage(), file);
                exitCode = 2;
            }
        }
        return new Value(exitCode);
    }

}
