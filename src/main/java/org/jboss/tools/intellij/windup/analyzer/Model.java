package org.jboss.tools.intellij.windup.analyzer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class Model {
    public static class Ruleset {
        public String name;
        public String description;
        public Map<String, Violation> violations;
    }

    public static class Violation {
        public String description;
        public String category;
        public List<String> labels = Lists.newArrayList();
        public List<Incident> incidents = Lists.newArrayList();
    }

    public static class Incident {
        public String uri;
        public String message;
        public String codeSnip;
        public int lineNumber;
        public Map<String, String> variables = Maps.newHashMap();
    }

    public static class AnalyzerResultsModel {
        public String id;
        public String name;
        public List<Ruleset> rulesets = Lists.newArrayList();
    }
}