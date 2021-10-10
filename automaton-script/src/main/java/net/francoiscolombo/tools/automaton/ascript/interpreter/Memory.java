package net.francoiscolombo.tools.automaton.ascript.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A very simple memory with only a global scope.
 */
public class Memory {

    private Map<String, Value> memory = new HashMap<>();

    public Value get(String name) {
        return memory.get(name.trim().toUpperCase());
    }

    public void assign(String name, Value value) {
        memory.put(name.trim().toUpperCase(), value);
    }

    public void free() {
        memory.clear();
    }

    public Set<String> getVariables() {
        return memory.keySet();
    }

}
