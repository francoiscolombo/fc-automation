package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class Copy extends AbstractStatement {

    public Copy(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0,values) && check(1, values)) {
            String source = values[0].internalString();
            String target = values[1].internalString();
            Path sourceLocation = Paths.get(source);
            Path targetLocation = Paths.get(target);
            CopyFileVisitor fileVisitor = new CopyFileVisitor(sourceLocation, targetLocation);
            try {
                Files.walkFileTree(sourceLocation, fileVisitor);
                printStream.printf("<%s> successfully copied to destination <%s>\n", source, target);
                exitCode = 0;
            } catch (IOException ioex) {
                errorStream.printf("I/O Exception while trying to copy <%s> to destination <%s> => %s\n", source, target, ioex.getMessage());
                exitCode = 2;
            }
        }
        return new Value(exitCode);
    }

    private class CopyFileVisitor extends SimpleFileVisitor<Path> {

        final Path source;

        final Path target;

        CopyFileVisitor(final Path source, final Path target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            Path newDirectory = this.target.resolve(this.source.relativize(dir));
            try {
                Files.copy(dir, newDirectory);
            } catch (FileAlreadyExistsException ioException) {
                return SKIP_SUBTREE; // skip processing
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Path newFile = this.target.resolve(this.source.relativize(file));
            Files.copy(file, newFile);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException ioex) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException ioex) {
            errorStream.printf("I/O Exception while trying to copy <%s> to destination <%s> => %s\n", file.toString(), this.target.toString(), ioex.getMessage());
            return CONTINUE;
        }

    }

}
