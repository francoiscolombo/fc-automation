package net.francoiscolombo.tools.automaton.ascript.statements;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class AbstractZipUnzipStatement extends AbstractStatement {

    private final static int BUFFER = 1024;

    public AbstractZipUnzipStatement(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    protected void zip(final String sourceDirectoryPath, final String zipFilePath) throws IOException {
        final Path zipFile = Files.createFile(Paths.get(zipFilePath));
        final Path sourceDirPath = Paths.get(sourceDirectoryPath);
        try (final ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile));
             final Stream<Path> paths = Files.walk(sourceDirPath)) {
            paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                printStream.println("> " + path);
                try {
                    zipOutputStream.putNextEntry(zipEntry);
                    Files.copy(path, zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (IOException ioex) {
                    errorStream.printf("Cannot add entry <%s> to archive file <%s> - %s", path, zipFilePath, ioex.getMessage());
                }
            });
        }
    }

    protected void unzip(final Path sourceZip, final Path targetDirectory) throws IOException {
        final Path root = targetDirectory.normalize();
        try (final InputStream is = Files.newInputStream(sourceZip);
             final ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                Path path = root.resolve(entry.getName()).normalize();
                System.out.println("< " + path);
                if (!path.startsWith(root)) {
                    throw new IOException("Invalid ZIP");
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(path);
                } else {
                    Path parent = path.getParent();
                    Files.createDirectories(parent);
                    try (OutputStream os = Files.newOutputStream(path)) {
                        byte[] buffer = new byte[BUFFER];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
                entry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

}
