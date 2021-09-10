package net.francoiscolombo.tools.automaton.agent.service;

import io.grpc.stub.StreamObserver;
import net.francoiscolombo.tools.automaton.actions.Action;
import net.francoiscolombo.tools.automaton.grpc.agent.*;
import net.francoiscolombo.tools.automaton.models.Playbook;
import net.francoiscolombo.tools.automaton.models.Variable;
import net.francoiscolombo.tools.automaton.models.Variables;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class AgentService extends AgentServiceGrpc.AgentServiceImplBase {

    // setup global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // in which package should we search for actions?
    private final static String ACTIONS_PACKAGE = "net.francoiscolombo.tools.automaton.actions.%s";

    private int status = -1;
    private String reason = "";
    private String message = "";
    private BufferedOutputStream mBufferedOutputStream = null;

    public static String getHostname() {
        String hostname = null;
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            hostname = localAddress.getHostName();
        } catch (UnknownHostException e) {
            hostname = "localhost";
        }
        return hostname;
    }

    private MResponse response() {
        return MResponse.newBuilder().setCode(status).setReason(reason).setMessage(message).build();
    }

    @Override
    public StreamObserver<MSendFileRequest> sendfile(final StreamObserver<MSendFileResponse> responseObserver) {

        status = 200;
        message = "File transfered.";

        return new StreamObserver<MSendFileRequest>() {

            int mmCount = 0;

            @Override
            public void onNext(MSendFileRequest mSendFileRequest) {
                // Print count
                LOGGER.info("SendFile: onNext count: " + mmCount);
                mmCount++;

                byte[] data = mSendFileRequest.getData().toByteArray();
                long offset = mSendFileRequest.getOffset();
                String name = mSendFileRequest.getName();
                try {
                    if (mBufferedOutputStream == null) {
                        String filePath = mSendFileRequest.getPath().concat(File.pathSeparator).concat(mSendFileRequest.getName());
                        mBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                    }
                    mBufferedOutputStream.write(data);
                    mBufferedOutputStream.flush();
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                    status = 500;
                    message = String.format("IOException while trying to send file: %s",e.getMessage());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.severe(throwable.getMessage());
                status = 500;
                message = String.format("Exception while trying to send file: %s",throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(MSendFileResponse.newBuilder().setStatus(status).setMessage(message).build());
                responseObserver.onCompleted();
                if (mBufferedOutputStream != null) {
                    try {
                        mBufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mBufferedOutputStream = null;
                    }
                }
            }

        };
    }

    @Override
    public void ping(MPing request, StreamObserver<MPong> responseObserver) {
        if(request.isInitialized() && request.getPing().equalsIgnoreCase("PING")) {
            LOGGER.info("PONG");
            responseObserver.onNext(MPong.newBuilder().setPong("PONG").build());
        } else {
            LOGGER.warning("POUIK");
            responseObserver.onNext(MPong.newBuilder().setPong("POUIK").build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void play(MPlaybook mPlaybook, StreamObserver<MResponse> responseObserver) {
        /*Context ctx = Context.current().fork();
        ctx.run(() -> {
            // Can start asynchronous work here that will not
            // be cancelled when myRpcMethod returns
        });*/
        Playbook playbook = ModelConverter.convertPlaybook(mPlaybook);
        String title = String.format("Running now playbook [%s] on <%s>", playbook.getName(), getHostname());
        String dash = new String(new char[120 - title.length()]).replace("\0", "=");
        LOGGER.info(title.concat(dash));
        List<Variable> variables = new LinkedList<>();
        playbook.getVariables().forEach(v -> {
            LOGGER.info(String.format("> Variable <%s> defined.", v.getName()));
            variables.add(new Variable(v.getName(), v.getValue()));
        });
        title = "Running tasks now ";
        dash = new String(new char[120 - title.length()]).replace("\0", "=");
        LOGGER.info(title.concat(dash));
        Variables.global().register(variables);
        playbook.getStages().forEach(stage -> {
            try {
                Class<?> actionClass = Class.forName(String.format(ACTIONS_PACKAGE, stage.getAction()));
                try {
                    Action action = (Action) actionClass.getDeclaredConstructor().newInstance();
                    //LOGGER.info(String.format("Action <%s> loaded.", actionClass.getName()));
                    action.registerStage(stage);
                    Variables.global().register(action.runTask(Variables.global().all()));
                    status = 0;
                    reason = "OK";
                    message = "Playbook executed.";
                } catch (InstantiationException iee) {
                    status = 500;
                    reason = "SERVER ERROR";
                    message = String.format("Cannot instantiate class <%s> : %s", actionClass.getCanonicalName(), iee.getMessage());
                    LOGGER.severe(message);
                } catch (IllegalAccessException iae) {
                    status = 500;
                    reason = "SERVER ERROR";
                    message = String.format("Illegal access for instanciation of class <%s> : %s", actionClass.getCanonicalName(), iae.getMessage());
                    LOGGER.severe(message);
                } catch (InvocationTargetException ite) {
                    status = 500;
                    reason = "SERVER ERROR";
                    message = String.format("Exception when invocating target class <%s> : %s", actionClass.getCanonicalName(), ite.getMessage());
                    LOGGER.severe(message);
                } catch (NoSuchMethodException nsme) {
                    status = 500;
                    reason = "SERVER ERROR";
                    message = String.format("No such method to invoke for target class <%s> : %s", actionClass.getCanonicalName(), nsme.getMessage());
                    LOGGER.severe(message);
                }
            } catch (ClassNotFoundException cnfe) {
                status = 404;
                reason = "NOT FOUND";
                message = String.format("### ERROR ### action <%s> is not defined (yet), so it can't be processed. we skip it. (%s)", stage.getAction(), cnfe.getMessage());
                LOGGER.warning(message);
            }
        });
        responseObserver.onNext(response());
        responseObserver.onCompleted();
    }

}
