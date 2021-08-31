package net.francoiscolombo.tools.automaton.master;

import net.francoiscolombo.tools.automaton.agent.client.AgentClient;
import net.francoiscolombo.tools.automaton.agent.client.AgentResult;
import net.francoiscolombo.tools.automaton.models.Playbook;
import picocli.CommandLine;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

@CommandLine.Command(
        name = "automaton",
        mixinStandardHelpOptions = true,
        version = "automaton 1.0",
        description = "manage the automaton master process"
)
public class Start implements Callable<Integer> {

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

    @CommandLine.Option(names = {"-p", "--playbook"}, required = true, description = "playbook to execute, full path")
    private String playbookPath;

    @Override
    public Integer call() throws Exception {
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
        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Start()).execute(args);
        System.exit(exitCode);
    }

}
