package com.fc.tools.automation.actions;

import com.fc.tools.automation.parser.AbstractAction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

public class DownloadFile extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        getParameter("url").ifPresent(url -> {
            getParameter("path").ifPresent(path -> {
                try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
                    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
                    FileChannel fileChannel = fileOutputStream.getChannel();
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    LOGGER.info(String.format("<%s> successfully copied to destination <%s>", url, path));
                    this.exitCode = 0;
                } catch (FileNotFoundException e) {
                    LOGGER.warning(String.format("Source URL <%s> is not found: %s", url, e.getMessage()));
                    this.exitCode = 2;
                } catch (IOException e) {
                    LOGGER.warning(String.format("I/O Exception while trying to copy <%s> to destination <%s> => %s", url, path, e.getMessage()));
                    this.exitCode = 3;
                }
            });
        });
    }

}
