package com.fc.tools.automation.parser;

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
     * iterate stages list and execute action<br>
     */
    public void runPipeline() {
        String title = String.format("Running now playbook [%s]", this.playbook.getName());
        String dash = new String(new char[120 - title.length()]).replace("\0", "=");
        LOGGER.info(title.concat(dash));
        this.playbook.getVariables().forEach(v -> LOGGER.info(String.format("> Variable <%s> defined.", v.getName())));
        title = "Running tasks now ";
        dash = new String(new char[120 - title.length()]).replace("\0", "=");
        LOGGER.info(title.concat(dash));
        Variables.global().register(this.playbook.getVariables());
        for (Stage stage : this.playbook.getStages()) {
            try {
                Class<?> actionClass = Class.forName(String.format("com.fc.tools.automation.actions.%s", stage.getAction()));
                try {
                    @SuppressWarnings("deprecation") Action action = (Action) actionClass.newInstance();
                    //LOGGER.info(String.format("Action <%s> loaded.", actionClass.getName()));
                    action.registerStage(stage);
                    Variables.global().register(action.runTask(Variables.global().all()));
                } catch (InstantiationException iee) {
                    LOGGER.severe(String.format("Cannot instantiate class <%s> : %s", actionClass.getCanonicalName(), iee.getMessage()));
                } catch (IllegalAccessException iae) {
                    LOGGER.severe(String.format("Illegal access for instanciation of class <%s> : %s", actionClass.getCanonicalName(), iae.getMessage()));
                }
            } catch (ClassNotFoundException cnfe) {
                LOGGER.warning(String.format("### ERROR ### action <%s> is not defined (yet), so it can't be processed. we skip it.", stage.getAction()));
            }
        }
    }

}
