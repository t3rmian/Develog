package io.github.t3r1jj.develog.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlantUmlMatcherTest {
    @Test
    void findPlantUMLSubstrings() {
        String input = "@startuml\n" +
                "Class01 <|-- Class02\n" +
                "Class03 *-- Class04\n" +
                "Class05 o-- Class06\n" +
                "Class07 .. Class08\n" +
                "Class09 -- Class10\n" +
                "@enduml";
        List<String> groups = PlantUmlMatcher.findPlantUMLSubstrings(input);
        assertEquals(1, groups.size(), "Found one group");
        assertEquals(groups.get(0), input, "Found the group");
    }

    @Test
    @DisplayName("findPlantUMLSubstrings within text")
    void findPlantUMLSubstrings_withinText() {
        String input = "@startuml\n" +
                "Class01 <|-- Class02\n" +
                "Class03 *-- Class04\n" +
                "Class05 o-- Class06\n" +
                "Class07 .. Class08\n" +
                "Class09 -- Class10\n" +
                "@enduml";
        List<String> groups = PlantUmlMatcher.findPlantUMLSubstrings("abc" + input + " bcf");
        assertEquals(1, groups.size(), "Found one group");
        assertEquals(groups.get(0), input, "Found the group");
    }

    @Test
    @DisplayName("findPlantUMLSubstrings within text, trailing ending")
    void findPlantUMLSubstrings_withinText_trailingEnding() {
        String input = "@startuml\n" +
                "Class01 <|-- Class02\n" +
                "Class03 *-- Class04\n" +
                "Class05 o-- Class06\n" +
                "Class07 .. Class08\n" +
                "Class09 -- Class10\n" +
                "@enduml";
        List<String> groups = PlantUmlMatcher.findPlantUMLSubstrings("abc" + input + "bcf");
        assertEquals(1, groups.size(), "Found one group");
        assertEquals(groups.get(0), input + "bcf", "Found the group");
    }

    @Test
    @DisplayName("findPlantUMLSubstrings, multipleGroups")
    void findPlantUMLSubstrings_multipleGroups() {
        String input = "@startuml\n" +
                "Class01 <|-- Class02\n" +
                "Class03 *-- Class04\n" +
                "Class05 o-- Class06\n" +
                "Class07 .. Class08\n" +
                "Class09 -- Class10\n" +
                "@enduml";
        List<String> groups = PlantUmlMatcher.findPlantUMLSubstrings(input + " " + input + " " + input);
        assertEquals(3, groups.size(), "Found 3 groups");
    }

    @Test
    @DisplayName("findPlantUMLSubstrings, multipleGroups, singleLine")
    void findPlantUMLSubstrings_multipleGroups_singleLine() {
        String input = "@start " +
                "Class01 <|-- Class02 " +
                "Class03 *-- Class04 " +
                "Class05 o-- Class06 " +
                "Class07 .. Class08 " +
                "Class09 -- Class10 " +
                "@end";
        System.out.println(input);
        List<String> groups = PlantUmlMatcher.findPlantUMLSubstrings(input + " " + input + " " + input);
        assertEquals(3, groups.size(), "Found 3 groups");
    }

}