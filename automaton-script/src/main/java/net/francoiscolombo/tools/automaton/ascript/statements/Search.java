package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;
import org.grep4j.core.Grep4j;
import org.grep4j.core.model.Profile;
import org.grep4j.core.model.ProfileBuilder;
import org.grep4j.core.result.GrepResults;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Search extends AbstractStatement {

    public Search(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String fileName = values[0].internalString();
            String searchDirectory = values[1].internalString();
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:.*" + fileName);
            try {
                Collection<Path> searchResults = search(searchDirectory, matcher);
                searchResults.forEach(printStream::println);
                exitCode = 0;
            } catch (IOException ioException) {
                errorStream.printf("Can't search file %s from directory %s, error is '%s'\n", fileName, searchDirectory, ioException.getMessage());
                exitCode = 2;
            }
        }
        return new Value(exitCode);
    }

    private static Collection<Path> search(String searchDirectory, PathMatcher matcher) throws IOException {
        try (Stream<Path> files = Files.walk(Paths.get(searchDirectory))) {
            return files
                    .filter(matcher::matches)
                    .collect(Collectors.toList());
        }
    }
}
