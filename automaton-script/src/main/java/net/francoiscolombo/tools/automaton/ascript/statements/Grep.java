package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;
import org.grep4j.core.Grep4j;
import org.grep4j.core.model.Profile;
import org.grep4j.core.model.ProfileBuilder;
import org.grep4j.core.result.GrepResults;

import java.io.PrintStream;
import java.nio.file.Path;

public class Grep extends AbstractStatement {

    public Grep(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String pattern = values[0].internalString();
            String filePath = values[1].internalString();
            Path path = Path.of(filePath);
            Profile localProfile = ProfileBuilder.newBuilder().
                    name(path.getFileName().toString()).filePath(path.getParent().toString()).
                    onLocalhost().build();
            GrepResults results = Grep4j.grep(Grep4j.regularExpression(pattern), localProfile);
            results.forEach(printStream::println);
            exitCode = 0;
        }
        return new Value(exitCode);
    }

}
