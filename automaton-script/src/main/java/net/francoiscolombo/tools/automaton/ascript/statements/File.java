package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.IOException;
import java.io.PrintStream;
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

public class File extends AbstractStatement {

    public File(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values) && check(1, values)) {
            final String path = values[0].internalString();
            final Path filePath = Paths.get(path);
            final String state = values[1].internalString();
            if(!state.equals("")) {
                try {
                    if ("absent".equalsIgnoreCase(state)) {
                        Files.walk(filePath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(java.io.File::delete);
                        exitCode = 0;
                        if (Files.exists(filePath)) {
                            errorStream.printf("%s %s is not properly deleted, please check the rights...\n", filePath.toFile().isDirectory()
                                    ? "File"
                                    : "Directory", path);
                            exitCode = 2;
                        }
                    } else if ("directory".equalsIgnoreCase(state)) {
                        if (Files.exists(filePath)) {
                            if (filePath.toFile().isDirectory()) {
                                printStream.printf("Directory %s already exists.\n", path);
                                exitCode = 0;
                            } else {
                                errorStream.printf("%s already exists, but it's not a directory...\n", path);
                                exitCode = 4;
                            }
                        } else {
                            Files.createDirectories(filePath);
                            printStream.printf("New directory %s is created.\n", path);
                            exitCode = 0;
                        }
                    } else if ("file".equalsIgnoreCase(state)) {
                        if (Files.exists(filePath)) {
                            if (filePath.toFile().isFile()) {
                                printStream.printf("File %s already exists.\n", path);
                                exitCode = 0;
                            } else {
                                printStream.printf("%s already exists, but it's not a file...\n", path);
                                exitCode = 4;
                            }
                        } else {
                            Files.createFile(filePath);
                            printStream.printf("New file %s is created.\n", path);
                            exitCode = 0;
                        }
                    }
                } catch (IOException ioex) {
                    errorStream.printf("IOException happened during the process: %s\n", ioex.getMessage());
                    exitCode = 3;
                }
            }

            if(check(2, values)) {
                String newOwner = values[2].internalString();
                if(!newOwner.equals("")) {
                    try {
                        if (Files.exists(filePath)) {
                            printStream.println("-- owner before --");
                            UserPrincipal owner = Files.getOwner(filePath);
                            printStream.printf("Owner: %s\n", owner.toString());
                            printStream.println("-- lookup other user --");
                            FileSystem fileSystem = filePath.getFileSystem();
                            UserPrincipalLookupService service = fileSystem.getUserPrincipalLookupService();
                            UserPrincipal userPrincipal = service.lookupPrincipalByName(newOwner);
                            printStream.printf("Found UserPrincipal: %s\n", userPrincipal.toString());
                            Files.setOwner(filePath, userPrincipal);
                            printStream.println("-- owner after --");
                            owner = Files.getOwner(filePath);
                            printStream.printf("Owner: %s\n", owner.getName());
                            exitCode = 0;
                        } else {
                            errorStream.printf("%s does not exists, so we can't change its ownership...\n", path);
                            exitCode = 5;
                        }
                    } catch (IOException ioex) {
                        errorStream.printf("IOException happened during the process: %s\n", ioex.getMessage());
                        exitCode = 3;
                    }
                }
            }

            if(check(3, values)) {
                String mode = values[3].internalString();
                if(!mode.equals("")) {
                    printStream.printf("Operating System: %s\n", System.getProperty("os.name"));
                    if (System.getProperty("os.name").toUpperCase().contains("LINUX") || System.getProperty("os.name").toUpperCase().contains("AIX")) {
                        Set<PosixFilePermission> perms = convertPermsFromInt(Integer.parseInt(mode));
                        try {
                            Files.setPosixFilePermissions(filePath, perms);
                        } catch (IOException ioex) {
                            errorStream.printf("IOException happened during the process: %s\n", ioex.getMessage());
                            exitCode = 3;
                        }
                    } else {
                        errorStream.println("Operating system is not a linux one, so we can't change the file permissions. Sorry.");
                        exitCode = 6;
                    }
                }
            }

        }
        return new Value(exitCode);
    }

    private Set<PosixFilePermission> convertPermsFromInt(final int perms) {
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
        printStream.printf("Convert file permissions: %d -> %s\n", perms, sperms);
        return PosixFilePermissions.fromString(sperms);
    }

}
