package net.francoiscolombo.tools.automaton.agent.service;

import net.francoiscolombo.tools.automaton.grpc.agent.*;
import net.francoiscolombo.tools.automaton.models.*;

import java.util.*;

public class ModelConverter {

    public static Stage convertStage(MStage stage) {
        String action = stage.getAction();
        String condition = stage.getCondition();
        String display = stage.getDisplay();
        MLoop loop = stage.getLoop();
        MForEach foreach = stage.getForeach();
        // convert Loop
        Loop cLoop = null;
        if (stage.hasLoop()) {
            cLoop = new Loop();
            cLoop.setIndex(loop.getIndex());
            cLoop.setRange(loop.getRange());
        }
        // convert ForEach
        Foreach cForeach = null;
        if (stage.hasForeach()) {
            cForeach = new Foreach();
            List<String> items = new LinkedList<>();
            for (int i = 0; i < foreach.getItemsCount(); i++) {
                items.add(foreach.getItems(i));
            }
            cForeach.setItems(items);
        }
        // convert parameters
        Map<String, String> parameters = new HashMap<>(stage.getParametersCount() + 1);
        for (int i = 0; i < stage.getParametersCount(); i++) {
            MParameter p = stage.getParameters(i);
            parameters.put(p.getPname().toUpperCase(Locale.ROOT), p.getPvalue());
        }
        // create stage
        Stage s = new Stage();
        s.setAction(action);
        s.setDisplay(display);
        s.setCondition(condition);
        s.setForeach(cForeach);
        s.setLoop(cLoop);
        s.setParameters(parameters);
        return s;
    }

    public static Variable convertVariable(MVariable variable) {
        Variable v = new Variable();
        v.setName(variable.getName().toUpperCase(Locale.ROOT));
        v.setValue(variable.getValue());
        return v;
    }

    public static Playbook convertPlaybook(MPlaybook playbook) {
        Playbook p = new Playbook();
        p.setName(playbook.getName());
        p.setNodes(null);
        List<Stage> stages = new LinkedList<>();
        List<Variable> variables = new LinkedList<>();
        if(playbook.getVariablesCount() > 0) {
            for(int i=0; i<playbook.getVariablesCount(); i++) {
                variables.add(convertVariable(playbook.getVariables(i)));
            }
        }
        if(playbook.getStagesCount() > 0) {
            for(int i=0; i<playbook.getStagesCount(); i++) {
                stages.add(convertStage(playbook.getStages(i)));
            }
        }
        p.setVariables(variables);
        p.setStages(stages);
        return p;
    }

    public static MPlaybook createRequest(Playbook playbook) {
        List<MVariable> variables = new LinkedList<>();
        for(Variable v : playbook.getVariables()) {
            variables.add(MVariable.newBuilder()
                    .setName(v.getName())
                    .setValue(v.getValue() != null ? v.getValue() : "")
                    .build()
            );
        }

        List<MStage> stages = new LinkedList<>();
        for(Stage s : playbook.getStages()) {
            MLoop loop = MLoop.newBuilder().build();
            MForEach foreach = MForEach.newBuilder().build();
            if(s.getLoop() != null) {
                loop = MLoop.newBuilder()
                        .setIndex(s.getLoop().getIndex())
                        .setRange(s.getLoop().getRange())
                        .build();
            }
            if(s.getForeach() != null) {
                foreach = MForEach.newBuilder()
                        .addAllItems(s.getForeach().getItems())
                        .build();
            }
            List<MParameter> parameters = new LinkedList<>();
            for(String key: s.getParameters().keySet()) {
                parameters.add(MParameter.newBuilder().setPname(key).setPvalue(s.getParameter(key)).build());
            }
            stages.add(MStage.newBuilder()
                    .setAction(s.getAction())
                    .setCondition(s.getCondition() != null ? s.getCondition() : "")
                    .setDisplay(s.getDisplay())
                    .setLoop(loop)
                    .setForeach(foreach)
                    .addAllParameters(parameters)
                    .build()
            );
        }

        return MPlaybook.newBuilder()
                .setName(playbook.getName())
                .addAllVariables(variables)
                .addAllStages(stages)
                .build();
    }

}