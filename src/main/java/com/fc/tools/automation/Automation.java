package com.fc.tools.automation;

import com.fc.tools.automation.parser.PlaybookParser;

import java.util.Date;
import java.util.logging.*;

/**
 * This is the main class of the application.<br>
 * <br>
 *
 * @author François Colombo
 */
public class Automation {

    private final static String VERSION = "0.1.0";

    /**
     * setup global logger
     */
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

    // parameters passed from the command line
    private String playbookPath = null;

    /**
     * the constructor is private, because we don't want another class to be able to create an instance of this main class.
     */
    private Automation(final String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                if ("--help".equalsIgnoreCase(args[i])) {
                    displayHelp();
                    System.exit(0);
                } else if ("--version".equalsIgnoreCase(args[i])) {
                    System.out.println(String.format("SiteParameters v%s", VERSION));
                    System.exit(0);
                } else if ("--playbook".equalsIgnoreCase(args[i])) {
                    this.playbookPath = args[i + 1];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            this.playbookPath = null;
        }
    }

    /**
     * main class. you have to pass arguments from the command line, to decide on which mode the automate have to run.<br>
     *
     * @param args well you know... the arguments. from the command line.
     */
    public static void main(final String[] args) {

        Automation automate = new Automation(args);
        automate.doProcess();

        System.exit(0);

    }

    /**
     * simple method to display the help with my favorite Linux logo
     */
    private void displayHelp() {
        System.out.println("    .---.");
        System.out.println("   /     \\     FC Automation Tools v" + VERSION);
        System.out.println("   \\.@-@./");
        System.out.println("   /`\\_/`\\     To run a playbook, use the following");
        System.out.println("  //  _ \\ \\    parameter:");
        System.out.println(" | \\     )|_      --playbook <playbook path>");
        System.out.println("/`\\_`>  <_/ \\");
        System.out.println("\\__/'---'\\__/  For now, cannot process you request.");
    }

    /**
     * this method allow to run the automate on the proper mode, depending the parameters passed on the command line.
     */
    private void doProcess() {
        // scan the path in search for "actions.xml" files to process
        if (this.playbookPath == null) {
            displayHelp();
            System.exit(0);
        } else {
            System.out.println("      ______ ______");
            System.out.println("    _/      Y      \\_");
            System.out.println("   // ~~ ~~ | ~~ ~  \\\\");
            System.out.println("  // ~ ~ ~~ | ~~~ ~~ \\\\      F.C Automation Tools v" + VERSION);
            System.out.println(" //________.|.________\\\\     -=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("`----------`-'----------'");
            System.out.println();

            PlaybookParser.build(this.playbookPath).runPipeline();

            System.out.println("   .----.");
            System.out.println("   |$>_ |");
            System.out.println(" __|____|__");
            System.out.println("|  ______--|   Operation completed successfully.");
            System.out.println("`-/.::::.\\-'");
            System.out.println(" `--------'    See you soon!");
        }
    }

}
