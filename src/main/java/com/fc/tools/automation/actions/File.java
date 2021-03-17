package com.fc.tools.automation.actions;

import com.fc.tools.automation.parser.AbstractAction;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Comparator;
import java.util.Set;
import java.util.logging.Logger;

public class File extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Set<PosixFilePermission> convertPermsfromInt(final int perms) {
        final char[] ds = Integer.toString(perms).toCharArray();
        final char[] ss = {
                '-',
                '-',
                '-',
                '-',
                '-',
                '-',
                '-',
                '-',
                '-'
        };
        for (int i = ds.length - 1; i >= 0; i--) {
            int n = ds[i] - '0';
            if (i == (ds.length - 1)) {
                if ((n & 1) != 0) {
                    ss[8] = 'x';
                }
                if ((n & 2) != 0) {
                    ss[7] = 'w';
                }
                if ((n & 4) != 0) {
                    ss[6] = 'r';
                }
            } else if (i == (ds.length - 2)) {
                if ((n & 1) != 0) {
                    ss[5] = 'x';
                }
                if ((n & 2) != 0) {
                    ss[4] = 'w';
                }
                if ((n & 4) != 0) {
                    ss[3] = 'r';
                }
            } else if (i == (ds.length - 3)) {
                if ((n & 1) != 0) {
                    ss[2] = 'x';
                }
                if ((n & 2) != 0) {
                    ss[1] = 'w';
                }
                if ((n & 4) != 0) {
                    ss[0] = 'r';
                }
            }
        }
        String sperms = new String(ss);
        LOGGER.info(String.format("Convert file permissions: %d -> %s", perms, sperms));
        return PosixFilePermissions.fromString(sperms);
    }

    @Override
    protected void execute() {
        this.exitCode = 1;
        getParameter("path").ifPresent(
                path -> {
                    final Path filePath = Paths.get(path);
                    getParameter("state").ifPresent(
                            state -> {
                                try {
                                    if ("absent".equalsIgnoreCase(state)) {
                                        //noinspection ResultOfMethodCallIgnored
                                        Files.walk(filePath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(java.io.File::delete);
                                        this.exitCode = 0;
                                        if (Files.exists(filePath)) {
                                            LOGGER.warning(String.format("%s %s is not properly deleted, please check the rights...", filePath.toFile().isDirectory()
                                                    ? "File"
                                                    : "Directory", path));
                                            this.exitCode = 2;
                                        }
                                    } else if ("directory".equalsIgnoreCase(state)) {
                                        if (Files.exists(filePath)) {
                                            if (filePath.toFile().isDirectory()) {
                                                LOGGER.info(String.format("Directory %s already exists.", path));
                                                this.exitCode = 0;
                                            } else {
                                                LOGGER.warning(String.format("%s already exists, but it's not a directory...", path));
                                                this.exitCode = 4;
                                            }
                                        } else {
                                            Files.createDirectories(filePath);
                                            LOGGER.info(String.format("New directory %s is created.", path));
                                            this.exitCode = 0;
                                        }
                                    } else if ("file".equalsIgnoreCase(state)) {
                                        if (Files.exists(filePath)) {
                                            if (filePath.toFile().isFile()) {
                                                LOGGER.info(String.format("File %s already exists.", path));
                                                this.exitCode = 0;
                                            } else {
                                                LOGGER.warning(String.format("%s already exists, but it's not a file...", path));
                                                this.exitCode = 4;
                                            }
                                        } else {
                                            Files.createFile(filePath);
                                            LOGGER.info(String.format("New file %s is created.", path));
                                            this.exitCode = 0;
                                        }
                                    }
                                } catch (IOException ioex) {
                                    LOGGER.warning(String.format("IOException happened during the process: %s", ioex.getMessage()));
                                    this.exitCode = 3;
                                }
                            });
                    if (this.exitCode <= 1) {
                        getParameter("owner").ifPresent(newOwner -> {
                            try {
                                if (Files.exists(filePath)) {
                                    LOGGER.info("-- owner before --");
                                    UserPrincipal owner = Files.getOwner(filePath);
                                    LOGGER.info(String.format("Owner: %s", owner.toString()));
                                    LOGGER.info("-- lookup other user --");
                                    FileSystem fileSystem = filePath.getFileSystem();
                                    UserPrincipalLookupService service = fileSystem.getUserPrincipalLookupService();
                                    UserPrincipal userPrincipal = service.lookupPrincipalByName(newOwner);
                                    LOGGER.info(String.format("Found UserPrincipal: %s", userPrincipal.toString()));
                                    Files.setOwner(filePath, userPrincipal);
                                    LOGGER.info("-- owner after --");
                                    owner = Files.getOwner(filePath);
                                    LOGGER.info(String.format("Owner: %s", owner.getName()));
                                    this.exitCode = 0;
                                } else {
                                    LOGGER.warning(String.format("%s does not exists, so we can't change its ownership...", path));
                                    this.exitCode = 5;
                                }
                            } catch (IOException ioex) {
                                LOGGER.warning(String.format("IOException happened during the process: %s", ioex.getMessage()));
                                this.exitCode = 3;
                            }

                        });
                    }
                    if (this.exitCode <= 1) {
                        getParameter("mode").ifPresent(mode -> {
                            LOGGER.info(String.format("Operating System: %s", System.getProperty("os.name")));
                            if (System.getProperty("os.name").toUpperCase().contains("LINUX") || System.getProperty("os.name").toUpperCase().contains("AIX")) {
                                Set<PosixFilePermission> perms = convertPermsfromInt(Integer.parseInt(mode));
                                try {
                                    Files.setPosixFilePermissions(filePath, perms);
                                } catch (IOException ioex) {
                                    LOGGER.warning(String.format("IOException happened during the process: %s", ioex.getMessage()));
                                    this.exitCode = 3;
                                }
                            } else {
                                LOGGER.warning("Operating system is not a linux one, so we can't change the file permissions. Sorry.");
                                this.exitCode = 6;
                            }
                        });
                    }
                });
    }

}
