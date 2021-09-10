package net.francoiscolombo.tools.automaton.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Variable implements Comparable<Variable> {

    private String name;

    private String value;

    @Override
    public int compareTo(final Variable v) {
        return this.name.compareTo(v.name);
    }

}
