package com.fc.tools.automation.parser;

import java.util.List;

public class Playbook {

    private String name;

    private List<Variable> variables;

    private List<Stage> stages;

    public List<Stage> getStages() {
        return this.stages;
    }

    public void setStages(final List<Stage> stages) {
        this.stages = stages;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public final List<Variable> getVariables() {
        return this.variables;
    }

    public final void setVariables(final List<Variable> variables) {
        this.variables = variables;
    }

}
