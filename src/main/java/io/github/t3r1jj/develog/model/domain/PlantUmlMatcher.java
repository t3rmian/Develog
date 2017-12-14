package io.github.t3r1jj.develog.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PlantUmlMatcher {
    static List<String> findPlantUMLSubstrings(String input) {
        Pattern pattern = Pattern.compile("(@start\\S*.*?(?=@end)\\S*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        List<String> groups = new ArrayList<>();
        while (matcher.find()) {
            groups.add(matcher.group(1));
        }
        return groups;
    }
}
