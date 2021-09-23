package net.francoiscolombo.tools.automaton.actions;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.francoiscolombo.tools.automaton.agent.service.AgentService;
import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;
import net.francoiscolombo.tools.automaton.grpc.agent.AgentServiceGrpc;
import net.francoiscolombo.tools.automaton.grpc.agent.MSendFileRequest;
import net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse;

import java.io.File;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class SendFile extends AbstractAction {

    // global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        try {
            String sourceHostname = getMandatoryParameter("source");
            String path = getMandatoryParameter("sourcepath");
            String targetHostname = getMandatoryParameter("target");
            String targetPath = getMandatoryParameter("targetpath");
            String strTargetPort = getMandatoryParameter("port");
            final int targetPort = Integer.parseInt(strTargetPort);
            if(sourceHostname.equalsIgnoreCase(AgentService.getHostname())) {
                final AgentServiceGrpc.AgentServiceStub asyncStub = AgentServiceGrpc.newStub(
                        ManagedChannelBuilder
                                .forAddress(targetHostname, targetPort)
                                .usePlaintext()
                                .build()
                );
                StreamObserver<MSendFileRequest> requestObserver = asyncStub.sendfile(new StreamObserver<MSendFileResponse>() {
                    @Override
                    public void onNext(MSendFileResponse mSendFileResponse) {
                        LOGGER.info(String.valueOf(mSendFileResponse));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LOGGER.severe(throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        LOGGER.info("Send file completed.");
                    }
                });
                try {
                    Path filepath = Paths.get(path);
                    File file = filepath.toFile();
                    if (!file.exists()) {
                        this.exitCode = 7;
                        LOGGER.warning("File does not exist");
                        return;
                    }
                    try {
                        BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(file));
                        int bufferSize = 512 * 1024; // 512k
                        byte[] buffer = new byte[bufferSize];
                        int size = 0;
                        while ((size = bInputStream.read(buffer)) > 0) {
                            ByteString byteString = ByteString.copyFrom(buffer, 0, size);
                            MSendFileRequest req = MSendFileRequest.newBuilder().setName(filepath.getFileName().toString()).setPath(targetPath).setData(byteString).setOffset(size).build();
                            requestObserver.onNext(req);
                        }
                        this.exitCode = 0;
                    } catch (FileNotFoundException fileNotFoundException) {
                        this.exitCode = 6;
                        LOGGER.warning(String.format("File %s not found on the source hostname %s", path, sourceHostname));
                    } catch (IOException ioException) {
                        this.exitCode = 5;
                        LOGGER.warning(String.format("IOException while trying to read file %s from the source hostname %s", path, sourceHostname));
                    }
                } catch (RuntimeException e) {
                    this.exitCode = 4;
                    requestObserver.onError(e);
                    throw e;
                }
                requestObserver.onCompleted();
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.severe("Target port must be an integer: "+numberFormatException.getMessage());
            this.exitCode = 2;
        } catch (ParameterNotFound parameterNotFound) {
            LOGGER.severe("Parameter not found: "+parameterNotFound.getMessage());
            this.exitCode = 3;
        }
    }

}
