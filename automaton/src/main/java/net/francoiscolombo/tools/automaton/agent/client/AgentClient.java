package net.francoiscolombo.tools.automaton.agent.client;

import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import net.francoiscolombo.tools.automaton.agent.service.ModelConverter;
import net.francoiscolombo.tools.automaton.grpc.agent.AgentServiceGrpc;
import net.francoiscolombo.tools.automaton.grpc.agent.MPing;
import net.francoiscolombo.tools.automaton.grpc.agent.MPong;
import net.francoiscolombo.tools.automaton.grpc.agent.MResponse;
import net.francoiscolombo.tools.automaton.models.Playbook;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class AgentClient implements Callable<AgentResult> {

    // setup global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final AgentServiceGrpc.AgentServiceBlockingStub blockingStub;

    private final Playbook playbook;

    private final boolean pingOnly;

    public AgentClient(String hostname, int port, Playbook playbook, boolean pingOnly) {
        this.blockingStub = AgentServiceGrpc.newBlockingStub(
                ManagedChannelBuilder
                        .forAddress(hostname, port)
                        .usePlaintext()
                        .build()
        );
        this.playbook = playbook;
        this.pingOnly = pingOnly;
    }

    @Override
    public AgentResult call() throws Exception {
        try {
            // is this agent running, at least?
            MPong pong = blockingStub.ping(MPing.newBuilder().setPing("PING").build());
            if(pong.getPong().equalsIgnoreCase("PONG")) {
                LOGGER.info("Agent is ready for running playbook.");
                AgentResult result = new AgentResult(100, "PING", "Agent answer with a proper PONG");
                if(!pingOnly) {
                    MResponse response = blockingStub.play(ModelConverter.createRequest(this.playbook));
                    result = new AgentResult(response.getCode(), response.getReason(), response.getMessage());
                    if(result.getStatusCode() < 400) {
                        LOGGER.info(result.toString());
                    } else {
                        LOGGER.warning(result.toString());
                    }
                }
                return result;
            } else {
                LOGGER.severe("Cannot contact agent...");
                return new AgentResult(504, "BAD GATEWAY", "Agent is not responding to the test call");
            }
        } catch(StatusRuntimeException sre) {
            LOGGER.severe("Runtime exception happened during call of gRpc service, message is "+sre.getMessage());
            return new AgentResult(500, "ERROR", "Runtime exception happened during call of gRpc service, message is "+sre.getMessage());
        }
    }

}
