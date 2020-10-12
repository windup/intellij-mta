package org.jboss.tools.intellij.mta.services;

import com.google.common.collect.Lists;
import com.intellij.openapi.Disposable;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jboss.tools.intellij.mta.model.MtaModelParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ModelService implements Disposable {

    private MtaModel mtaModel;

    private static final String STATE_LOCATION = ModelService.getStateLocation();

    public MtaModel getModel() {
        return this.mtaModel;
    }

    public void forceReload() {
        this.mtaModel = MtaModelParser.parseModel(STATE_LOCATION);
    }

    public MtaModel loadModel() {
        if (this.mtaModel != null) {
            return this.mtaModel;
        }
        this.mtaModel = MtaModelParser.parseModel(STATE_LOCATION);
        return this.mtaModel;
    }

    public void saveModel() {
        JSONObject model = new JSONObject();
        JSONArray configurations = new JSONArray();
        model.put("configurations", configurations);
        for (MtaConfiguration configuration : this.mtaModel.getConfigurations()) {
            JSONObject configObject = new JSONObject();
            configurations.add(configObject);
            configObject.put("id", configuration.getId());
            configObject.put("name", configuration.getName());
            JSONObject options = new JSONObject();
            configObject.put("options", options);
            for (Map.Entry entry : configuration.getOptions().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof List) {
                    List<String> values = (List<String>)value;
                    JSONArray valueArray = new JSONArray();
                    valueArray.addAll(values);
                    options.put(entry.getKey(), valueArray);
                }
                else {
                    options.put(entry.getKey(), entry.getValue());
                }
            }
            AnalysisResultsSummary resultsSummary = configuration.getSummary();
            if (resultsSummary != null) {
                JSONObject summary = new JSONObject();
                configObject.put("summary", summary);
                String skippedReports = (String) configuration.getOptions().get("skipReports");
                boolean skipReports = skippedReports != null ? Boolean.valueOf(skippedReports) : false;
                summary.put("skipReports", skipReports);
                summary.put("outputLocation", resultsSummary.outputLocation);
                summary.put("executedTimestamp", resultsSummary.executedTimestamp);
                summary.put("executable", resultsSummary.executable);
                summary.put("hintCount", resultsSummary.hints.size());
                summary.put("classificationCount", resultsSummary.classifications.size());
                List<Issue> issues = Lists.newArrayList(resultsSummary.hints);
                issues.addAll(resultsSummary.classifications);
                for (Issue issue : issues) {
                    if (!issue.quickfixes.isEmpty()) {
                        JSONObject quickfixes = (JSONObject) summary.get("quickfixes");
                        if (quickfixes == null) {
                            quickfixes = new JSONObject();
                            summary.put("quickfixes", quickfixes);
                        }
                        JSONObject quickfixObj = new JSONObject();
                        quickfixes.put(issue.id, quickfixObj);
                        quickfixObj.put("originalLineSource", issue.originalLineSource);

                        JSONObject quickfixedLines = new JSONObject();
                        quickfixObj.put("quickfixedLines", quickfixedLines);
                        for (QuickFix quickfix : issue.quickfixes) {
                            String fix = issue.quickfixedLines.get(quickfix.id);
                            if (fix != null) {
                                quickfixedLines.put(quickfix.id, fix);
                            }
                        }
                    }
                }
            }
        }
        boolean canWrite = true;
        try {
            if (!new File(STATE_LOCATION).exists()) {
                File out = new File(STATE_LOCATION);
                out.getParentFile().mkdirs();
                out.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            canWrite = false;
        }
        if (canWrite) {
            try (FileWriter file = new FileWriter(STATE_LOCATION)) {
                String content = model.toJSONString();
                file.write(content);
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {
        this.saveModel();
    }

    private static String getStateLocation() {
        return System.getProperty("user.home")
            + File.separator + ".mta"
            + File.separator +  "tooling"
            + File.separator + "intellij"
            + File.separator + "model.json";
    }
}
