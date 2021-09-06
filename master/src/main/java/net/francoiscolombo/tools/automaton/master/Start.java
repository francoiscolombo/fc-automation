package net.francoiscolombo.tools.automaton.master;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.francoiscolombo.tools.automaton.agent.client.AgentClient;
import net.francoiscolombo.tools.automaton.agent.client.AgentResult;
import net.francoiscolombo.tools.automaton.agent.service.AgentService;
import net.francoiscolombo.tools.automaton.models.Playbook;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.logging.Formatter;

@CommandLine.Command(
        name = "automaton",
        mixinStandardHelpOptions = true,
        version = "automaton 1.0",
        description = "automaton is a tool for helping you to manage efficiently your servers configuration"
)
public class Start implements Callable<Integer> {

    // setup global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static Formatter FORMATTER = new SimpleFormatter() {
        private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

        @Override
        public synchronized String format(final LogRecord lr) {
            return String.format(format, new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getMessage());
        }
    };

    @CommandLine.Option(names = {"--playbook"}, defaultValue="./playbook.yaml", description = "playbook to execute, full path")
    private String playbookPath;

    @CommandLine.Option(names = {"--output"}, defaultValue = "", description = "redirect stdout to a file")
    private String logFilePath;

    @CommandLine.Option(names = {"--agent"}, description = "starts as an agent (you should set the port in this case, otherwise the default port value of 8071 is taken)")
    private boolean agentMode;

    @CommandLine.Option(names = {"--port"}, defaultValue = "8071", description = "port on which the agent will listening")
    private String agentPort;

    @Override
    public Integer call() throws Exception {
        // log management
        LOGGER.setUseParentHandlers(false);
        if(!"".equals(logFilePath)) {
            FileHandler fhandler = new FileHandler(logFilePath.concat(".%g.log"), 10 * 1024 * 1024, 10);
            fhandler.setFormatter(FORMATTER);
            LOGGER.addHandler(fhandler);
        } else {
            ConsoleHandler chandler = new ConsoleHandler();
            chandler.setFormatter(FORMATTER);
            LOGGER.addHandler(chandler);
        }
        LOGGER.setLevel(Level.ALL);
        // are we in agent mode or in master mode?
        if(agentMode) {
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
        } else {
            // master: run a playbook
            if(Paths.get(playbookPath).toFile().exists()) {
                if(Paths.get(playbookPath).toFile().canRead()) {
                    final Playbook playbook = PlaybookParser.build(playbookPath).checkImports().getPlaybook();
                    final ExecutorService executor = Executors.newFixedThreadPool(playbook.getNodes().getHostnames().size());
                    final int agentPort = playbook.getNodes().getPort();
                    final List<Callable<AgentResult>> agents = new LinkedList<>();
                    for(String hostname : playbook.getNodes().getHostnames()) {
                        agents.add(new AgentClient(hostname, agentPort, playbook));
                    }
                    final List<Future<AgentResult>> allTasks = executor.invokeAll(agents);
                    boolean allIsDone = true;
                    do {
                        TimeUnit.SECONDS.sleep(1);
                        for(Future<AgentResult> task : allTasks) {
                            if(!task.isDone()) {
                                allIsDone = false;
                            } else {
                                LOGGER.info("Task completed "+task.get());
                            }
                        }
                    } while(!allIsDone);
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                    }
                }
            }
        }
        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Start()).execute(args);
        System.exit(exitCode);
    }

}
