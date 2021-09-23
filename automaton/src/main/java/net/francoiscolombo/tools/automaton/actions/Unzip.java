package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static int BUFFER = 1024;

    private void unzip(final Path sourceZip, final Path targetDirectory) throws IOException {
        Path root = targetDirectory.normalize();
        try (InputStream is = Files.newInputStream(sourceZip); ZipInputStream zis = new ZipInputStream(is)) {
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

    @Override
    protected void execute() {
        this.exitCode = 1;
        try {
            String archivePath = getMandatoryParameter("archive");
            String folderPath = getMandatoryParameter("destination");
            try {
                Path src = Paths.get(archivePath);
                Path dst = Paths.get(folderPath);
                unzip(src, dst);
                this.exitCode = 0;
            } catch (Exception ex) {
                LOGGER.warning(String.format("Something went wrong while trying to unzip <%s> to <%s>: %s", archivePath, folderPath, ex.getMessage()));
                this.exitCode = 2;
            }
        } catch (ParameterNotFound parameterNotFound) {
            LOGGER.warning(parameterNotFound.getMessage());
        }
    }

}