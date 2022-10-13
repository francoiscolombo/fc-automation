package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List extends AbstractStatement {

    public List(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            String pattern = values[0].internalString();
            String listDirectory = values[1].internalString();
            try {
                Set<String> directories = new HashSet<>();
                Set<String> files = new HashSet<>();
                Files.walkFileTree(Paths.get(listDirectory), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        String name = file.getFileName().toString();
                        if(name.matches(pattern)) {
                            String f = String.format(
                                    "%s %s (C:%s,M:%s,A:%s) %d",
                                    name,
                                    attrs.isSymbolicLink() ? "L" : "",
                                    attrs.creationTime().toString(),
                                    attrs.lastModifiedTime().toString(),
                                    attrs.lastAccessTime().toString(),
                                    attrs.size());
                            if (Files.isDirectory(file)) {
                                directories.add(f);
                            } else {
                                files.add(f);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
                directories.forEach(printStream::println);
                files.forEach(printStream::println);
                exitCode = 0;
            } catch (IOException ioException) {
                errorStream.printf("Can't list pattern %s from directory %s, error is '%s'\n", pattern, listDirectory, ioException.getMessage());
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
