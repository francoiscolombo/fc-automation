package com.fc.tools.automation.parser;

import java.util.Map;
import java.util.Optional;

public class Stage {

    private String action;

    private String display;

    private Optional<String> condition;

    private Map<String, String> parameters;

    public final String getDisplay() {
        return this.display;
    }

    public final void setDisplay(final String display) {
        this.display = display;
    }

    public final String getAction() {
        return this.action;
    }

    public final void setAction(final String name) {
        this.action = name;
    }

    public final Map<String, String> getParameters() {
        return this.parameters;
    }

    public final void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public final Optional<String> getCondition() {
        return this.condition;
    }

    public final void setCondition(final Optional<String> condition) {
        this.condition = condition;
    }

    public final Optional<String> getParameter(final String key) {
        Optional<String> value = Optional.empty();
        if (this.parameters.containsKey(key)) {
            value = Optional.of(this.parameters.getOrDefault(key, ""));
        }
        return value;
    }

}
