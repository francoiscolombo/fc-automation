package net.francoiscolombo.tools.automaton.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Foreach {
    private List<String> items;
}
