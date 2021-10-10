package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Memory;
import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TemplateFromFile extends AbstractTemplateStatement {

    public TemplateFromFile(PrintStream printStream, PrintStream errorStream, Memory memory) {
        super(printStream, errorStream, memory);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String destination = values[1].internalString();
            Path templatePath = Paths.get(values[0].internalString());
            try {
                final Stream<String> lines = Files.lines(templatePath);
                exitCode = writeTemplate(lines, destination);
            } catch (Exception ioex) {
                errorStream.printf("Exception <%s> while trying to load template from <%s>\n", ioex.getMessage(), templatePath);
                exitCode = 2;
            }
        }
        return new Value(exitCode);
    }

}
