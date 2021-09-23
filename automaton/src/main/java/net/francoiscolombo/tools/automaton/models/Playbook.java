package net.francoiscolombo.tools.automaton.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Playbook {

    private String name;

    private List<Hostname> nodes;

    private List<Variable> variables;

    private List<Vault> vault;

    private List<Stage> stages;

}
