package net.francoiscolombo.tools.automaton.master;

import net.francoiscolombo.tools.automaton.models.Playbook;
import net.francoiscolombo.tools.automaton.models.Stage;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaybookParser {

    // the global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // this is the list of actions that are read from the YAML file
    private Playbook playbook;

    /**
     * we are implementing a builder, so the constructors must be private.
     */
    private PlaybookParser() {
        this("playbook.yaml");
    }

    private PlaybookParser(final String filePath) {
        boolean error = false;
        Instant start = Instant.now();
        Path path = Paths.get(filePath);
        if (path.toFile().exists() && path.toFile().canRead()) {
            try (BufferedReader fileReader = Files.newBufferedReader(path)) {
                Yaml yaml = new Yaml(new Constructor(Playbook.class));
                this.playbook = yaml.load(fileReader);
            } catch (IOException ioex) {
                LOGGER.log(Level.SEVERE, "I/O Exception occured while trying to load playbook file: " + ioex.getMessage(), ioex);
                error = true;
            } catch (ScannerException scex) {
                LOGGER.severe("Playbook definition file " + path + " malformed: " + scex.getMessage());
                error = true;
            } catch (ConstructorException cnex) {
                LOGGER.severe("Playbook cannot be constructed from file " + path + ": " + cnex.getMessage());
                error = true;
            }
        } else {
            LOGGER.severe("Playbook file does not exists or in not readable. Operation aborted.");
            error = true;
        }
        if (!error) {
            Instant end = Instant.now();
            Duration ellapsed = Duration.between(start, end);
            LocalTime ellapsedTime = LocalTime.ofNanoOfDay(ellapsed.toNanos());
            LOGGER.info(String.format("PlaybookParser: playbook loaded from <%s> in %s", filePath, ellapsedTime.format(DateTimeFormatter.ISO_LOCAL_TIME)));
        } else {
            LOGGER.severe(String.format("PlaybookParser: can't load playbook <%s>, process aborted", filePath));
            System.exit(-1);
        }
    }

    /**
     * and this is the method that will give the single instance of this class.
     *
     * @return the instance of the ActionsParser class
     */
    public synchronized static PlaybookParser build(final String filePath) {
        return new PlaybookParser(filePath);
    }

    /**
     * allows to return the playbook loaded and parsed
     *
     * @return a playbook instance
     */
    public Playbook getPlaybook() {
        return this.playbook;
    }

    /**
     * iterate stages list and search for special "import" action, which is the only one runnning on the master side<br>
     */
    public PlaybookParser checkImports() {
        final LinkedList<Stage> stages = new LinkedList<>(this.playbook.getStages());
        for(Stage stage : this.playbook.getStages()) {
            if(stage.getAction().equalsIgnoreCase("import")) {
                for(String key : stage.getParameters().keySet()) {
                    if(key.equalsIgnoreCase("playbook")) {
                        String playbookPath = stage.getParameter(key);
                        if(!playbookPath.equals("")) {
                            // load playbook and merge variables part, and add all stages at the beginning of the current playbook
                            // please note that the nodes part is just skipped, since only the current nodes part is relevant
                            LOGGER.info("Import playbook from path "+playbookPath);
                            Playbook importedPlaybook = PlaybookParser.build(playbookPath).checkImports().getPlaybook();
                            playbook.getVariables().addAll(importedPlaybook.getVariables());
                            if(!importedPlaybook.getStages().isEmpty()) {
                                ListIterator<Stage> stageListIterator = importedPlaybook.getStages().listIterator(importedPlaybook.getStages().size());
                                while(stageListIterator.hasPrevious()) {
                                    stages.addFirst(stageListIterator.previous());
                                }
                            }
                        }
                    }
                }
            }
        }
        return this;
    }

}
