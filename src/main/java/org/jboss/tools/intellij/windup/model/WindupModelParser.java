/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.model;

import com.google.common.collect.Lists;
import org.jboss.tools.intellij.windup.cli.RulesetParser;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.jboss.tools.intellij.windup.model.WindupConfiguration.*;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class WindupModelParser {

    public static WindupModel parseModel(String fileName, ModelService modelService) {
        WindupModel windupModel = new WindupModel();
        JSONParser parser = new JSONParser();
        if (new File(fileName).exists()) {
            try {
                System.out.println("fILE NAME: " + fileName);
                Object obj = parser.parse(new FileReader(fileName));
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray configurations = (JSONArray) jsonObject.get("configurations");
                Iterator iterator = configurations.iterator();
                while (iterator.hasNext()) {
                    JSONObject config = (JSONObject) iterator.next();
                    WindupConfiguration configuration = WindupModelParser.parseConfigurationObject(config, modelService);
                    windupModel.addConfiguration(configuration);
                   // WindupResultsParser.parseResults(configuration);
                 //   System.out.println("This is in Parse Model ........ calls parseRulesetForKantraConfig");
                    RulesetParser.parseRulesetForKantraConfig(configuration);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return windupModel;
    }

    private static WindupConfiguration parseConfigurationObject(JSONObject configurationObjects, ModelService modelService) {
       //System.out.println("This is parse configuration Object ...........");
        WindupConfiguration windupConfiguration = new WindupConfiguration();
        windupConfiguration.setId((String) configurationObjects.get("id"));
        windupConfiguration.setName((String) configurationObjects.get("name"));
        WindupModelParser.parseConfigurationOptionsObject(
                (Map) configurationObjects.get("options"),
                windupConfiguration);
        Map summary = (Map) configurationObjects.get("summary");
      //  System.out.println("Checking for Summery  ..........................");
        if (summary != null) {
       //     System.out.println("Summery does not come null..........................");
            WindupModelParser.parseSummary(summary, windupConfiguration, modelService);
        }
        String windupCli = (String)windupConfiguration.getOptions().get("cli");
        if (windupCli == null || "".equals(windupCli)) {
            windupConfiguration.getOptions().put("cli", modelService.computeWindupCliLocation());
        }
        String output = (String)windupConfiguration.getOptions().get("output");
        if (output == null || "".equals(output)) {
            windupConfiguration.getOptions().put("output", ModelService.getConfigurationOutputLocation(windupConfiguration));
        }
        return windupConfiguration;
    }

    public static void parseConfigurationOptionsObject(Map optionsObject, WindupConfiguration configuration) {
        Iterator<Map.Entry> iterator = optionsObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = iterator.next();
            Object key = pair.getKey();
            Object value = pair.getValue();
            if (value instanceof JSONArray) {
                JSONArray optionValues = (JSONArray)value;
                ArrayList<String> values = Lists.newArrayList();
                for (Object v : optionValues) {
                    values.add((String)v);
                }
                configuration.addOption((String)key, values);
            }
            else {
                configuration.addOption((String)key, String.valueOf(value));
            }
        }
    }

    private static void parseSummary(Map optionsObject, WindupConfiguration configuration, ModelService modelService) {
        AnalysisResultsSummary summary = new AnalysisResultsSummary(modelService);
        configuration.setSummary(summary);
        Iterator<Map.Entry> iterator = optionsObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = iterator.next();
            String key = (String)pair.getKey();
            Object value = pair.getValue();
            if ("executedTimestamp".equals(key)) {
                summary.executedTimestamp = (String)value;
            }
            else if ("executionDuration".equals(key)) {
                summary.executionDuration = (String)value;
            }
            else if ("outputLocation".equals(key)) {
                summary.outputLocation = (String)value;
            }
            else if ("executable".equals(key)) {
                summary.executable = (String)value;
            }
            else if ("hintCount".equals(key)) {
                summary.hintCount = new Integer(String.valueOf(value));
            }
            else if ("classificationCount".equals(key)) {
                summary.classificationCount = new Integer(String.valueOf(value));
            }
            else if ("completeIssues".equals(key)) {
                if (value instanceof JSONArray) {
                    JSONArray optionValues = (JSONArray) value;
                    for (Object v : optionValues) {
                        summary.completeIssues.add(String.valueOf(v));
                    }
                }
            }
            else if ("deletedIssues".equals(key)) {
                if (value instanceof JSONArray) {
                    JSONArray optionValues = (JSONArray) value;
                    for (Object v : optionValues) {
                        summary.deletedIssues.add(String.valueOf(v));
                    }
                }
            }
        }
    }
}
