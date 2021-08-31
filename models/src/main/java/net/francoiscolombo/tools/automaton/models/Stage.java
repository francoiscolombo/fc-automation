package net.francoiscolombo.tools.automaton.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Stage {

    private String action;

    private String display;

    private String condition;

    private Loop loop;

    private Foreach foreach;

    private Map<String, String> parameters;

    public final String getParameter(final String key) {
        String value = null;
        if (this.parameters.containsKey(key)) {
            value = this.parameters.getOrDefault(key, "");
        }
        return value;
    }

}
