package net.francoiscolombo.tools.automaton.agent.service;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import net.francoiscolombo.tools.automaton.actions.Action;
import net.francoiscolombo.tools.automaton.grpc.agent.*;
import net.francoiscolombo.tools.automaton.models.Playbook;
import net.francoiscolombo.tools.automaton.models.Variable;
import net.francoiscolombo.tools.automaton.models.Variables;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

public class AgentService extends AgentServiceGrpc.AgentServiceImplBase {

    // setup global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // in which package should we search for actions?
    private final static String ACTIONS_PACKAGE = "net.francoiscolombo.tools.automaton.actions.%s";

    private int status = -1;
    private String reason = "";
    private String message = "";

    private MResponse response() {
        return MResponse.newBuilder().setCode(status).setReason(reason).setMessage(message).build();
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
        String title = String.format("Running now playbook [%s]", playbook.getName());
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
