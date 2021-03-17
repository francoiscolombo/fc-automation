package com.fc.tools.automation.actions;

import com.fc.tools.automation.parser.AbstractAction;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class CopyFile extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        getParameter("from").ifPresent(source -> getParameter("to").ifPresent(target -> {
            Path sourceLocation = Paths.get(source);
            Path targetLocation = Paths.get(target);
            CopyFileVisitor fileVisitor = new CopyFileVisitor(sourceLocation, targetLocation);
            try {
                Files.walkFileTree(sourceLocation, fileVisitor);
                LOGGER.info(String.format("<%s> successfully copied to destination <%s>", source, target));
                this.exitCode = 0;
            } catch (IOException ioex) {
                LOGGER.warning(String.format("I/O Exception while trying to copy <%s> to destination <%s> => %s", source, target, ioex.getMessage()));
                this.exitCode = 2;
            }
        }));
    }

    private static class CopyFileVisitor extends SimpleFileVisitor<Path> {

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
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
            Path newFile = this.target.resolve(this.source.relativize(file));
            try {
                Files.copy(file, newFile);
            } catch (IOException ioex) {
                LOGGER.warning(String.format("I/O Exception while trying to copy <%s> to destination <%s> => %s", file.toString(), this.target.toString(), ioex.getMessage()));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException ioex) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException ioex) {
            LOGGER.warning(String.format("I/O Exception while trying to copy <%s> to destination <%s> => %s", file.toString(), this.target.toString(), ioex.getMessage()));
            return CONTINUE;
        }

    }

}
