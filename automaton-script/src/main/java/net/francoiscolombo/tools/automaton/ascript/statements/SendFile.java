package net.francoiscolombo.tools.automaton.ascript.statements;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;
import net.francoiscolombo.tools.automaton.grpc.agent.AgentServiceGrpc;
import net.francoiscolombo.tools.automaton.grpc.agent.MSendFileRequest;
import net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse;

import java.io.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendFile extends AbstractStatement {

    public SendFile(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        // SEND FILE expression TO string PORT number ON expression
        if(check(0, values) && check(1, values) && check(2, values) && check(3, values)) {
            final String sourcePath = values[0].internalString();
            final String targetHostname = values[1].internalString();
            final int targetPort = Integer.parseInt(values[2].internalString());
            final String targetPath = values[3].internalString();
            try {
                final AgentServiceGrpc.AgentServiceStub asyncStub = AgentServiceGrpc.newStub(
                        ManagedChannelBuilder
                                .forAddress(targetHostname, targetPort)
                                .usePlaintext()
                                .build()
                );
                StreamObserver<MSendFileRequest> requestObserver = asyncStub.sendfile(new StreamObserver<MSendFileResponse>() {
                    @Override
                    public void onNext(MSendFileResponse mSendFileResponse) {
                        printStream.println(String.valueOf(mSendFileResponse));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        errorStream.println(throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        printStream.println("Send file completed.");
                    }
                });
                try {
                    Path filepath = Paths.get(sourcePath);
                    File file = filepath.toFile();
                    if (!file.exists()) {
                        exitCode = 7;
                        errorStream.println("File does not exist");
                        return new Value(exitCode);
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
                        exitCode = 0;
                    } catch (FileNotFoundException fileNotFoundException) {
                        exitCode = 6;
                        errorStream.printf("File %s not found on this source hostname\n", sourcePath);
                    } catch (IOException ioException) {
                        exitCode = 5;
                        errorStream.printf("IOException while trying to read file %s from this source hostname\n", sourcePath);
                    }
                } catch (RuntimeException e) {
                    exitCode = 4;
                    requestObserver.onError(e);
                    throw e;
                }
                requestObserver.onCompleted();
            } catch (NumberFormatException numberFormatException) {
                errorStream.printf("Target port must be an integer: %s\n", numberFormatException.getMessage());
                exitCode = 2;
            }
        }
        return new Value(exitCode);
    }

}
