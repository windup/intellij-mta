package org.jboss.tools.intellij.mta.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MtaConfiguration {

    private Map<String, Object> options = Maps.newHashMap();
    private String name;
    private String id;
    private AnalysisResultsSummary summary;

    public Map<String, Object> getOptions() {
        return this.options;
    }

    public void addOption(String option, Object value) {
        this.options.put(option, value);
    }

    public AnalysisResultsSummary getSummary() {
        return this.summary;
    }

    public void setSummary(AnalysisResultsSummary summary) {
        this.summary = summary;
    }
    public String getReportLocation() {
        String output = (String)this.options.get("output");
        if (output == null || output.isEmpty()) return null;
        Path location = Paths.get(output,"index.html");
        return location.toAbsolutePath().toString();
    }

    public String getResultsLocation() {
        String output = (String)this.options.get("output");
        if (output == null || output.isEmpty()) return null;
        Path location = Paths.get(output, "results.xml");
        return location.toAbsolutePath().toString();
    }

    public boolean skippedReports() {
        String skippedReports = (String) this.getOptions().get("skipReports");
        return skippedReports != null ? Boolean.valueOf(skippedReports) : false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class UniqueElement {
        public String id;
    }

    public static enum QuickFixType {
        REPLACE("REPLACE"),
        DELETE_LINE("DELETE_LINE"),
        INSERT_LINE("INSERT_LINE"),
        TRANSFORMATION("TRANSFORMATION");
        String type;
        QuickFixType(String type) {
            this.type = type;
        }
    }

    public static class Link extends UniqueElement {
        public String title;
        public String url;
    }

    public static class Issue extends UniqueElement {
        public String title;
        public ArrayList<QuickFix> quickfixes = Lists.newArrayList();
        public Map<String, String> quickfixedLines = Maps.newHashMap();
        public String originalLineSource;
        public String file;
        public String severity;
        public String ruleId;
        public String effort;
        public ArrayList<Link> links = Lists.newArrayList();
        public String report;
        public String category;
        public MtaConfiguration configuration;
        public Object dom;
        public boolean complete;
    }

    public static class QuickFix extends UniqueElement {
        public Issue issue;
        public QuickFixType type;
        public String searchString;
        public String replacementString;
        public String newLine;
        public String transformationId;
        public String name;
        public String file;
    }

    public static class AnalysisResultsSummary {
        public String executedTimestamp;
        public String executionDuration;
        public String outputLocation;
        public String executable;
        public int hintCount;
        public int classificationCount;
        public QuickfixData quickfixData;
        public Map<String, String> reports = Maps.newHashMap();
        public List<Hint> hints = Lists.newArrayList();
        public List<Classification> classifications = Lists.newArrayList();
    }

    public static interface ReportHolder {
        public String getReport();
    }

    public static interface IssueContainer {
        public Issue getIssue();
        public void setComplete();
    }

    public static class Hint extends Issue {
        public int lineNumber;
        public int column;
        public int length;
        public String sourceSnippet;
        public String hint;
    }

    public static class Classification extends Issue {
        public String description;
    }

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static class QuickfixData {
        public Map<String, QuickfixEntry> entries = Maps.newHashMap();
    }

    public static class QuickfixEntry {
        public String originalLineSource;
        public Map<String, String> quickfixes = Maps.newHashMap();
    }
}
