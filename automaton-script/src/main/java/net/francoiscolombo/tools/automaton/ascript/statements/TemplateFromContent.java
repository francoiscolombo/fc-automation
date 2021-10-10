package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Memory;
import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Stream;

public class TemplateFromContent extends AbstractTemplateStatement {

    public TemplateFromContent(PrintStream printStream, PrintStream errorStream, Memory memory) {
        super(printStream, errorStream, memory);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String template = values[0].internalString();
            String destination = values[1].internalString();
            try {
                final Stream<String> lines = Arrays.stream(template.split(System.getProperty("line.separator")));
                exitCode = writeTemplate(lines, destination);
            } catch (Exception ioex) {
                errorStream.printf("Exception <%s> while trying to load template from <%s>\n", ioex.getMessage(), template);
                exitCode = 2;
            }
        }
        return new Value(exitCode);
    }

}
