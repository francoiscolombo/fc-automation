package net.francoiscolombo.tools.automaton.agent.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentResult {
    private int statusCode;
    private String reason;
    private String message;

    @Override
    public String toString() {
        return String.format("[%d] (%s) %s", statusCode, reason, message);
    }

}
