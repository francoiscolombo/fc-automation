package net.francoiscolombo.tools.automaton.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Nodes {

    private int port;

    private List<String> hostnames;

}
