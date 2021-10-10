package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Memory;
import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class AbstractTemplateStatement extends AbstractStatement {

    private final static Pattern VAR_PATTERN = Pattern.compile("\\{\\{\\s*(\\w+)\\s*\\}\\}");

    final private Memory memoryReadOnly;

    public AbstractTemplateStatement(PrintStream printStream, PrintStream errorStream, Memory memory) {
        super(printStream, errorStream);
        this.memoryReadOnly = memory;
    }

    private String replaceVariables(String line) {
        String result = line;
        Matcher m = VAR_PATTERN.matcher(line);
        while (m.find()) {
            String found = m.group();
            String var = m.group(1);
            Value variable = memoryReadOnly.get(var);
            if (variable != null) {
                if(variable.isString()) {
                    result = result.replace(found, variable.internalString());
                } else if(variable.isNumber()) {
                    result = result.replace(found, String.valueOf(variable.internalNumber()));
                } else if(variable.isNaN()) {
                    result = result.replace(found, "NaN");
                } else if (variable.isFalse()) {
                    result = result.replace(found, "false");
                } else if (variable.isTrue()) {
                    result = result.replace(found, "true");
                }
            }
        }
        return result;
    }

    protected int writeTemplate(final Stream<String> lines, final String destination) {
        int exitCode = 1;
        java.io.File path = new File(destination);
        path.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(path, StandardCharsets.UTF_8)) {
            lines.forEachOrdered(line -> {
                String l = replaceVariables(line);
                printStream.printf("> %s", l);
                pw.println(l);
            });
            lines.close();
            printStream.printf(">>> template written in file <%s>\n", destination);
            exitCode = 0;
        } catch (IOException ioex) {
            errorStream.printf("Exception <%s> while trying to copy template to <%s>\n", ioex.getMessage(), destination);
            exitCode = 3;
        }
        return exitCode;
    }

}
