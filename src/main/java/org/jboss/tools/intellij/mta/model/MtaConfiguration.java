package org.jboss.tools.intellij.mta.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MtaConfiguration {

    private ListMultimap<String, String> options = ArrayListMultimap.create();
    private String name;
    private String id;
    private AnalysisResults analysisResults;

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
}
