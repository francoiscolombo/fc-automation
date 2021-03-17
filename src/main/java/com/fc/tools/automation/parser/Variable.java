package com.fc.tools.automation.parser;

import java.util.Optional;

public class Variable implements Comparable<Variable> {

    private String name;

    private Optional<String> value;

    public final String getName() {
        return this.name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final Optional<String> getValue() {
        return this.value;
    }

    public final void setValue(final Optional<String> value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    /**
     * please note that the variable name is not case sensitive for testing equality
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Variable other = (Variable) obj;
        if (this.name == null) {
            return other.name == null;
        } else return this.name.equalsIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return "Variable [name=" + this.name + ", value=" + this.value + "]";
    }

    @Override
    public int compareTo(final Variable v) {
        return this.name.compareTo(v.name);
    }

}
