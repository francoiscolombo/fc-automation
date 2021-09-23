package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private void zip(final String sourceDirectoryPath, final String zipFilePath) throws IOException {
        Path zipFile = Files.createFile(Paths.get(zipFilePath));
        Path sourceDirPath = Paths.get(sourceDirectoryPath);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile)); Stream<Path> paths = Files.walk(sourceDirPath)) {
            paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                System.out.println("> " + path);
                try {
                    zipOutputStream.putNextEntry(zipEntry);
                    Files.copy(path, zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (IOException ioex) {
                    LOGGER.warning(String.format("Cannot add entry <%s> to archive file <%s> - %s", path, zipFilePath, ioex.getMessage()));
                }
            });
        }
    }

    @Override
    protected void execute() {
        this.exitCode = 1;
        try {
            String folderPath = getMandatoryParameter("path");
            String archivePath = getMandatoryParameter("archive");
            try {
                zip(folderPath, archivePath);
                this.exitCode = 0;
            } catch (IOException ioex) {
                LOGGER.warning(String.format("Cannot create archive file <%s> from folder <%s> - %s", archivePath, folderPath, ioex.getMessage()));
                this.exitCode = 2;
            }
        } catch (ParameterNotFound parameterNotFound) {
            LOGGER.warning(parameterNotFound.getMessage());
        }
    }

}
