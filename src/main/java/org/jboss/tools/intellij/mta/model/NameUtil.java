package org.jboss.tools.intellij.mta.model;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NameUtil {

    public static final String CONFIGURATION_ELEMENT_PREFIX = "mtaConfiguration";

    private static Pattern namePattern = Pattern.compile("([a-zA-Z_]*)([0-9]+)");

    public static String generateUniqueConfigurationName(MtaModel model) {
        Set<String> existingNames = getAllExistingNames(model);
        String nextName = generateName(CONFIGURATION_ELEMENT_PREFIX, existingNames);
        return nextName;
    }

    private static Set<String> getAllExistingNames(MtaModel model) {
        Set<String> nameSet = model.getConfigurations().stream().map(e -> e.getName()).collect(Collectors.toSet());
        return nameSet;
    }

    public static String generateName(String candidate, Set<String> names) {
        Matcher matcher = namePattern.matcher(candidate);
        int index = 0;
        if (matcher.matches()) {
            candidate = matcher.group(1);
            index = Integer.parseInt(matcher.group(2)) + 1;
        }
        return getNextUniqueName(names, index, candidate);
    }

    private static String getNextUniqueName(Set<String> names, int index, String prefixStr) {
        String newName = "";
        boolean done = false;
        while (!done) {
            newName = String.format("%s%d", prefixStr, index++);
            if (!names.contains(newName)) {
                done = true;
            }
        }
        return newName;
    }
}
