package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Archive extends AbstractAction {

    // global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private void addToArchive(final String filePath, final String parent, final TarArchiveOutputStream tarArchive) throws IOException {
        File file = new File(filePath);
        // Create entry name relative to parent file path for the archived file
        String entryName = String.format("%s%s", parent, file.getName());
        LOGGER.info(String.format("entryName > %s", entryName));
        // add tar ArchiveEntry
        tarArchive.putArchiveEntry(new TarArchiveEntry(file, entryName));
        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
                // Write file content to archive
                IOUtils.copy(bis, tarArchive);
                tarArchive.closeArchiveEntry();
            }
        } else if (file.isDirectory()) {
            // no content to copy so close archive entry
            tarArchive.closeArchiveEntry();
            // if this directory contains more directories and files
            // traverse and archive them
            for (File f : Objects.requireNonNull(file.listFiles())) {
                // recursive call
                addToArchive(f.getAbsolutePath(), entryName + File.separator, tarArchive);
            }
        }
    }

    private void createTarGzFromFolder(final String folderPath, final String archivePath) {
        try (FileOutputStream fos = new FileOutputStream(archivePath);
             GZIPOutputStream gzipOS = new GZIPOutputStream(new BufferedOutputStream(fos));
             TarArchiveOutputStream tarArchive = new TarArchiveOutputStream(gzipOS)) {
            addToArchive(folderPath, "", tarArchive);
            LOGGER.info(String.format("Archive %s is now created from folder %s.", archivePath, folderPath));
            this.exitCode = 0;
        } catch (IOException ioex) {
            LOGGER.warning(String.format("An I/O exception happened during the creation of %s from %s, the message is '%s'", archivePath, folderPath, ioex.getMessage()));
            this.exitCode = 2;
        }
    }

    private void unTar(String archivePath, String folderPath, File destFile, TarArchiveInputStream tis) throws IOException {
        TarArchiveEntry tarEntry;
        while ((tarEntry = tis.getNextTarEntry()) != null) {
            LOGGER.info(String.format("> tar entry > %s", tarEntry.getName()));
            if (!tarEntry.isDirectory()) {
                File outputFile = new File(destFile + File.separator + tarEntry.getName());
                if(outputFile.getParentFile().mkdirs()) {
                    LOGGER.info(String.format("> parent directories of %s was created", outputFile.getAbsolutePath()));
                }
                IOUtils.copy(tis, new FileOutputStream(outputFile));
            }
        }
        LOGGER.info(String.format("%s is now uncompress under folder %s.", archivePath, folderPath));
    }

    private void unTarGzFile(final String archivePath, final String folderPath) {
        File destFile = Paths.get(folderPath).toFile();
        try (FileInputStream fis = new FileInputStream(archivePath);
             GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fis));
             TarArchiveInputStream tis = new TarArchiveInputStream(gzipInputStream)) {
            unTar(archivePath, folderPath, destFile, tis);
        } catch (IOException ioex) {
            LOGGER.warning(String.format("An I/O exception happened during the creation of %s from %s, the message is '%s'", archivePath, folderPath, ioex.getMessage()));
            this.exitCode = 2;
        }
    }

    private void unTarFile(final String archivePath, final String folderPath) {
        File destFile = Paths.get(folderPath).toFile();
        try (FileInputStream fis = new FileInputStream(archivePath); TarArchiveInputStream tis = new TarArchiveInputStream(fis)) {
            unTar(archivePath, folderPath, destFile, tis);
        } catch (IOException ioex) {
            LOGGER.warning(String.format("An I/O exception happened during the creation of %s from %s, the message is '%s'", archivePath, folderPath, ioex.getMessage()));
            this.exitCode = 2;
        }
    }

    @Override
    protected void execute() {
        this.exitCode = 1;
        try {
            String folderPath = getMandatoryParameter("directory");
            String archivePath = getMandatoryParameter("path");
            String action = getMandatoryParameter("action");
            if ("compress".equalsIgnoreCase(action)) {
                this.exitCode = 0;
                createTarGzFromFolder(folderPath, archivePath);
            } else if ("uncompress".equalsIgnoreCase(action)) {
                this.exitCode = 0;
                if (archivePath.endsWith(".tar.gz")) {
                    unTarGzFile(archivePath, folderPath);
                } else if (archivePath.endsWith(".tar")) {
                    unTarFile(archivePath, folderPath);
                } else {
                    LOGGER.warning(String.format("We can uncompress only .tar.gz or .tar archive, and you ask to process archive %s. Sorry, we can't do it.",
                            archivePath));
                    this.exitCode = 4;
                }
            } else {
                LOGGER.warning(String.format("Action <%s> is not allowed, only 'compress' and 'uncompress'.", action));
                this.exitCode = 5;
            }
        } catch (ParameterNotFound parameterNotFound) {
            LOGGER.warning(parameterNotFound.getMessage());
            this.exitCode = 3;
        }
    }

}