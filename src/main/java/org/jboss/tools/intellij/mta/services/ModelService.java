/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.services;

import com.google.common.collect.Lists;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tree.StructureTreeModel;
import org.apache.commons.io.FileUtils;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jboss.tools.intellij.mta.model.MtaModelParser;
import org.jboss.tools.intellij.mta.model.NameUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ModelService implements Disposable {

    private MtaModel mtaModel;
    private Project project;
    private StructureTreeModel treeModel;

    private static final String STATE_LOCATION = ModelService.getStateLocation();

    public ModelService(Project project) {
        this.project = project;
    }

    public MtaModel getModel() {
        return this.mtaModel;
    }

    public Project getProject() {
        return this.project;
    }

    public void setTreeModel(StructureTreeModel treeModel) {
        this.treeModel = treeModel;
    }

    public StructureTreeModel getTreeModel() {
        return this.treeModel;
    }

    public void forceReload() {
        this.mtaModel = MtaModelParser.parseModel(STATE_LOCATION, this);
    }

    public MtaModel loadModel() {
        if (this.mtaModel != null) {
            return this.mtaModel;
        }
        this.mtaModel = MtaModelParser.parseModel(STATE_LOCATION, this);
        if (this.mtaModel.getConfigurations().isEmpty()) {
            // Create default configuration
            this.createConfiguration();
        }
        return this.mtaModel;
    }

    public boolean deleteConfiguration(MtaConfiguration configuration) {
        return this.mtaModel.deleteConfiguration(configuration);
    }

    public MtaConfiguration createConfiguration() {
        MtaModel model = this.getModel();
        MtaConfiguration configuration = new MtaConfiguration();
        configuration.setId(MtaConfiguration.generateUniqueId());
        configuration.setName(NameUtil.generateUniqueConfigurationName(model));
        configuration.getOptions().put("mtr-cli", this.computeMtaCliLocation());
        configuration.getOptions().put("output", ModelService.getConfigurationOutputLocation(configuration));
        configuration.getOptions().put("sourceMode", "true");
        List<String> target = (List<String>)configuration.getOptions().get("target");
        if (target == null || target.isEmpty()) {
            target = Lists.newArrayList();
            target.add("eap7");
        }
        configuration.getOptions().put("target", target);
        model.addConfiguration(configuration);
        return configuration;
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
                JSONArray completeIssues = new JSONArray();
                summary.put("completeIssues", completeIssues);
                JSONArray deletedIssues = new JSONArray();
                summary.put("deletedIssues", deletedIssues);
                for (Issue issue : issues) {
                    if (issue.complete) {
                        completeIssues.add(issue.id);
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

    public static void deleteOutput(MtaConfiguration configuration) {
        String output = (String)configuration.getOptions().get("output");
        if (output != null && !"".equals(output)) {
            try {
                FileUtils.deleteDirectory(new File(output));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Unable to delete output location for configuration.");
        }
    }

    @Override
    public void dispose() {
        this.saveModel();
    }

    private static String getStateLocation() {
        return ModelService.getDefaultOutputLocation()
                + File.separator + "model.json";
    }

    public String computeMtaCliLocation() {
        if (this.getModel().getConfigurations().isEmpty()) {
            return "";
        }
        MtaConfiguration configuration = Lists.reverse(this.getModel().getConfigurations()).stream().filter(config -> {
            String cli = (String) config.getOptions().get("mtr-cli");
            return cli != null && !"".equals(cli);
        }).findFirst().orElse(null);
        if (configuration != null) {
            return (String) configuration.getOptions().get("mtr-cli");
        }
        return "";
    }

    public static String getDefaultOutputLocation() {
        return FileUtils.getUserDirectoryPath()
                + File.separator + ".mtr"
                + File.separator +  "tooling"
                + File.separator + "intellij";
    }

    public static String getConfigurationOutputLocation(MtaConfiguration configuration) {
        return ModelService.getDefaultOutputLocation()
                + File.separator + configuration.getId();
    }
}
