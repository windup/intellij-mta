package org.jboss.tools.intellij.mta.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MtaConfiguration {

    private ListMultimap<String, String> options = ArrayListMultimap.create();
    private String name;
    private String id;
    private AnalysisResults analysisResults;
    private AnalysisResultsSummary summary;

    public ListMultimap<String, String> getOptions() {
        return this.options;
    }

    public void addOption(String option, String value) {
        this.options.put(option, value);
    }

    public AnalysisResults getAnalysisRestuls() {
        return this.analysisResults;
    }

    public void setAnalysisResults(AnalysisResults analysisResults) {
        this.analysisResults = analysisResults;
    }

    public AnalysisResultsSummary getSummary() {
        return this.summary;
    }

    public void setSummary(AnalysisResultsSummary summary) {
        this.summary = summary;
    }
    public String getReportLocation() {
        List<String> output = this.options.get("output");
        if (output.isEmpty()) return null;
        Path location = Paths.get(output.get(0), "index.html");
        return location.toAbsolutePath().toString();
    }

    public String getResultsLocation() {
        List<String> output = this.options.get("output");
        if (output.isEmpty()) return null;
        Path location = Paths.get(output.get(0), "results.xml");
        return location.toAbsolutePath().toString();
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
        String id;
    }

    public static enum IssueType {
        Hint,
        Classification
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
        String title;
        String url;
    }

    public static class Issue extends UniqueElement {
        IssueType type;
        String title;
        ArrayList<QuickFix> quickfixes = Lists.newArrayList();
        Map<String, String> quickfixedLines = Maps.newHashMap();
        String originalLineSource;
        String file;
        String severity;
        String ruleId;
        String effort;
        ArrayList<Link> links = Lists.newArrayList();
        String report;
        String category;
        MtaConfiguration configuration;
        Object dom;
        boolean complete;
    }

    public static class QuickFix extends UniqueElement {
        Issue issue;
        QuickFixType type;
        String searchString;
        String replacementString;
        String newLine;
        String transformationId;
        String name;
        String file;
    }

    public static class AnalysisResultsSummary {
        Boolean skippedReports;
        String executedTimestamp;
        String executionDuration;
        String outputLocation;
        String executable;
        List<QuickFix> quickfixes = Lists.newArrayList();
        ParsedQuickfixData parsedQuickfixData;
    }

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static class ParsedQuickfixData {
        Map<String, ParsedQuickfixEntry> entries = Maps.newHashMap();
    }

    public static class ParsedQuickfixEntry {
        String originalLineSource;
        Map<String, String> quickfixes = Maps.newHashMap();
    }
}
