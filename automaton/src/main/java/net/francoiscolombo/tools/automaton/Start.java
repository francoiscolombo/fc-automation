package net.francoiscolombo.tools.automaton;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.francoiscolombo.tools.automaton.agent.client.AgentClient;
import net.francoiscolombo.tools.automaton.agent.client.AgentResult;
import net.francoiscolombo.tools.automaton.agent.service.AgentService;
import net.francoiscolombo.tools.automaton.cypher.VaultManager;
import net.francoiscolombo.tools.automaton.models.Hostname;
import net.francoiscolombo.tools.automaton.models.Playbook;
import net.francoiscolombo.tools.automaton.parser.PlaybookParser;
import picocli.CommandLine;
import picocli.jansi.graalvm.AnsiConsole;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.*;

@CommandLine.Command(
        name = "automaton",
        mixinStandardHelpOptions = true,
        version = "automaton 1.1.0",
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

    @CommandLine.Option(names = {"--playbook"}, defaultValue="", description = "playbook to execute, full path")
    protected String playbookPath;

    @CommandLine.Option(names = {"--ping-only"}, description = "ping all the nodes of a playbook to check if agents are started")
    protected boolean pingAgents;

    @CommandLine.Option(names = {"--output"}, defaultValue = "", description = "redirect stdout to a file")
    protected String logFilePath;

    @CommandLine.Option(names = {"--agent"}, description = "starts as an agent (you should set the port in this case, otherwise the default port value of 8071 is taken)")
    protected boolean isAgentMode;

    @CommandLine.Option(names = {"--port"}, defaultValue = "8071", description = "port on which the agent will listening")
    protected String agentPort;

    @CommandLine.Option(names = {"--vault"}, description = "manage the local vault")
    protected boolean isVault;

    @CommandLine.Option(names = {"--create"}, defaultValue = "", description = "create a key in the vault and set the initial value")
    protected String createKey;

    @CommandLine.Option(names = {"--update"}, defaultValue = "", description = "update the value of an existing key in the vault with a new value")
    protected String updateKey;

    @CommandLine.Option(names = {"--delete"}, defaultValue = "", description = "delete an existing key in the vault")
    protected String deleteKey;

    @CommandLine.Option(names = {"--retrieve"}, defaultValue = "", description = "retrieve the value of an existing key in the vault")
    protected String vaultKey;

    @CommandLine.Option(names = {"--list"}, description = "list all the keys currently stored inside the vault")
    protected boolean listVault;

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
        if(isAgentMode) {
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
            } catch (NumberFormatException exception) {
                LOGGER.severe(String.format("Port %s is not numeric ! please provide a numeric port, like 8071 for example.", agentPort));
                return -1;
            }
        } else if(isVault) {
            // master: manage the local vault
            if(listVault) {
                VaultManager.getIntance().listKeys();
            } else {
                if(!"".equals(createKey)) {
                    // create a new key
                    VaultManager.getIntance().createKey(createKey);
                } else if(!"".equals(updateKey)) {
                    // update an existing key
                    VaultManager.getIntance().updateKey(updateKey);
                } else if(!"".equals(deleteKey)) {
                    // delete an existing key
                    VaultManager.getIntance().deleteKey(deleteKey);
                } else if(!"".equals(vaultKey)) {
                    // retrieve existing key
                    String value = VaultManager.getIntance().retrieve(vaultKey);
                    System.out.printf("Value for key <%s> is '%s'\n", vaultKey, value);
                }
            }
        } else if (!"".equals(playbookPath)) {
            // master: run a playbook
            if(Paths.get(playbookPath).toFile().exists()) {
                if(Paths.get(playbookPath).toFile().canRead()) {
                    final Playbook playbook = PlaybookParser.build(playbookPath).checkImports().getPlaybook();
                    final ExecutorService executor = Executors.newFixedThreadPool(playbook.getNodes().size());
                    final List<Callable<AgentResult>> agents = new LinkedList<>();
                    for(Hostname hostname : playbook.getNodes()) {
                        agents.add(new AgentClient(hostname.getHostname(), hostname.getPort(), playbook, pingAgents));
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
            } else {
                CommandLine.usage(this, System.out);
            }
        } else {
            CommandLine.usage(this, System.out);
        }
        return 0;
    }

    public static void main(String... args) {
        int exitCode;
        try (AnsiConsole ignored = AnsiConsole.windowsInstall()) {
            exitCode = new CommandLine(new Start()).execute(args);
        }
        System.exit(exitCode);
    }

}
