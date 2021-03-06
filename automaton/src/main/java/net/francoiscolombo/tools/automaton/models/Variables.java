package net.francoiscolombo.tools.automaton.models;

import java.util.*;
import java.util.logging.Logger;

public class Variables {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final Variables instance = new Variables();

    private final Set<Variable> variables = new TreeSet<>();

    public static Variables global() {
        return instance;
    }

    public List<Variable> all() {
        List<Variable> allVariables = new LinkedList<>();
        allVariables.addAll(this.variables);
        return allVariables;
    }

    public void register(final List<Variable> vars) {
        // we force update the contents of the variables, so if a value
        // evolve it will be register properly
        boolean removed = this.variables.removeAll(vars);
        boolean added = this.variables.addAll(vars);
        if (removed || added) {
            LOGGER.info("");
        }
    }

    public Optional<Variable> get(final String name) {
        Optional<Variable> v = Optional.empty();
        for (Variable var : this.variables) {
            if (var.getName().equalsIgnoreCase(name)) {
                return Optional.of(var);
            }
        }
        return v;
    }

    public void set(final Variable var) {
        this.variables.remove(var);
        this.variables.add(var);
    }

    public void set(final String name, final String value) {
        Variable var = new Variable();
        var.setName(name);
        var.setValue(value);
        this.variables.add(var);
    }

    public void remove(final Variable var) {
        this.variables.remove(var);
    }

}