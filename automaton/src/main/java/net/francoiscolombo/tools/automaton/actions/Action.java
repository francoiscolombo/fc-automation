package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.models.Stage;
import net.francoiscolombo.tools.automaton.models.Variable;

import java.util.List;

/**
 * This is the interface that will be implemented by all the actions classes.<br>
 *
 * @author François Colombo
 */
public interface Action {

    void registerStage(Stage stage);

    List<Variable> runTask(List<Variable> variables);

}