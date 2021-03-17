package com.fc.tools.automation.parser;

import java.util.List;

/**
 * This is the interface that will be implemented by all the actions classes.<br>
 *
 * @author BI8XQ - François Colombo
 */
public interface Action {

    void registerStage(Stage stage);

    List<Variable> runTask(List<Variable> variables);

}
