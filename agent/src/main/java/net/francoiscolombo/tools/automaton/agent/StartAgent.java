package net.francoiscolombo.tools.automaton.agent;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.francoiscolombo.tools.automaton.agent.service.AgentService;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.logging.*;

@CommandLine.Command(
        name = "automaton-agent",
        mixinStandardHelpOptions = true,
        version = "automaton 1.0",
        description = "automaton runtime agent, which is in charge of executing the playbook on the node"
)
public class StartAgent implements Callable<Integer> {

    // setup global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static {
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler chandler = new ConsoleHandler();
        chandler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(final LogRecord lr) {
                return String.format(format, new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getMessage());
            }
        });
        LOGGER.addHandler(chandler);
        LOGGER.setLevel(Level.ALL);
    }

    @CommandLine.Option(names = {"-p", "--port"}, required = true, defaultValue = "8071", description = "port on which the agent will listening")
    private String agentPort;

    @Override
    public Integer call() throws Exception {
        try {
            int port = Integer.parseInt(agentPort);
            Server server = ServerBuilder.forPort(port).addService(new AgentService()).build();
            LOGGER.info(String.format("Starting server for listening on port %d", port));
            try {
                server.start();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    LOGGER.info("Shutdown server now...");
                    server.shutdownNow();
                    LOGGER.info(String.format("Server is shutdown and no more listening on port %d now.", port));
                }));
                LOGGER.info(String.format("Server started and ready to receive connections on port %d", port));
                server.awaitTermination();
                return 0;
            } catch (IOException | InterruptedException exception) {
                LOGGER.severe(exception.getMessage());
                return -2;
            }
        } catch(NumberFormatException exception) {
            LOGGER.severe(String.format("Port %s is not numeric ! please provide a numeric port, like 8071 for example.",agentPort));
            return -1;
        }
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new StartAgent()).execute(args);
        System.exit(exitCode);
    }

}
