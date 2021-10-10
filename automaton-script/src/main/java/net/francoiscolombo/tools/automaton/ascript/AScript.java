package net.francoiscolombo.tools.automaton.ascript;

import net.francoiscolombo.tools.automaton.ascript.exceptions.AScriptException;
import picocli.CommandLine;

import java.nio.file.Paths;
import java.util.concurrent.*;

@CommandLine.Command(
        name = "ascript",
        mixinStandardHelpOptions = true,
        version = "AScript interpreter 1.0.0",
        description = "this is the scripting language of automaton, which can also be executed by the 'script' action"
)
public class AScript implements Callable<Integer> {

    @CommandLine.Option(names = {"--script"}, description = "script file to execute, full path")
    private String scriptPath;

    @Override
    public Integer call() throws AScriptException {
        if((scriptPath != null) && Paths.get(scriptPath).toFile().exists()) {
            AScriptInterpreterTask scriptInterpreterTask = new AScriptInterpreterTask(scriptPath, System.in, System.out, System.err);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Integer> exitCode = executorService.submit(scriptInterpreterTask);
            try {
                return exitCode.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new AScriptException(e.getMessage());
            }
        } else {
            CommandLine.usage(this, System.out);
        }
        return -1;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new AScript()).execute(args);
        System.exit(exitCode);
    }

}
