package net.francoiscolombo.tools.automaton.ascript.statements;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class AbstractArchiveStatement extends AbstractStatement {

    public AbstractArchiveStatement(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    private void addToArchive(final String filePath, final String parent, final TarArchiveOutputStream tarArchive) throws IOException {
        java.io.File file = new java.io.File(filePath);
        // Create entry name relative to parent file path for the archived file
        String entryName = String.format("%s%s", parent, file.getName());
        printStream.printf("entryName > %s\n", entryName);
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
            for (java.io.File f : Objects.requireNonNull(file.listFiles())) {
                // recursive call
                addToArchive(f.getAbsolutePath(), entryName + File.separator, tarArchive);
            }
        }
    }

    private void unTar(String archivePath, String folderPath, File destFile, TarArchiveInputStream tis) throws IOException {
        TarArchiveEntry tarEntry;
        while ((tarEntry = tis.getNextTarEntry()) != null) {
            printStream.printf("> tar entry > %s\n", tarEntry.getName());
            if (!tarEntry.isDirectory()) {
                File outputFile = new File(destFile + File.separator + tarEntry.getName());
                if(outputFile.getParentFile().mkdirs()) {
                    printStream.printf("> parent directories of %s was created\n", outputFile.getAbsolutePath());
                }
                IOUtils.copy(tis, new FileOutputStream(outputFile));
            }
        }
        printStream.printf("%s is now uncompress under folder %s.\n", archivePath, folderPath);
    }

    protected int createTarGzFromFolder(final String folderPath, final String archivePath) {
        try (final FileOutputStream fos = new FileOutputStream(archivePath);
             final GZIPOutputStream gzipOS = new GZIPOutputStream(new BufferedOutputStream(fos));
             final TarArchiveOutputStream tarArchive = new TarArchiveOutputStream(gzipOS)) {
            addToArchive(folderPath, "", tarArchive);
            printStream.printf("Archive %s is now created from folder %s.\n", archivePath, folderPath);
            return 0;
        } catch (IOException ioex) {
            errorStream.printf("An I/O exception happened during the creation of %s from %s, the message is '%s'\n", archivePath, folderPath, ioex.getMessage());
            return 2;
        }
    }

    protected int unTarGzFile(final String archivePath, final String folderPath) {
        File destFile = Paths.get(folderPath).toFile();
        try (final FileInputStream fis = new FileInputStream(archivePath);
             final GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fis));
             final TarArchiveInputStream tis = new TarArchiveInputStream(gzipInputStream)) {
            unTar(archivePath, folderPath, destFile, tis);
        } catch (IOException ioex) {
            errorStream.printf("An I/O exception happened during the creation of %s from %s, the message is '%s'", archivePath, folderPath, ioex.getMessage());
            return 2;
        }
        return 0;
    }

    protected int unTarFile(final String archivePath, final String folderPath) {
        File destFile = Paths.get(folderPath).toFile();
        try (final FileInputStream fis = new FileInputStream(archivePath);
             final TarArchiveInputStream tis = new TarArchiveInputStream(fis)) {
            unTar(archivePath, folderPath, destFile, tis);
        } catch (IOException ioex) {
            errorStream.printf("An I/O exception happened during the creation of %s from %s, the message is '%s'", archivePath, folderPath, ioex.getMessage());
            return 2;
        }
        return 0;
    }

}
