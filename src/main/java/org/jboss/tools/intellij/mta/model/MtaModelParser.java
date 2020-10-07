package org.jboss.tools.intellij.mta.model;

import org.jboss.tools.intellij.mta.cli.MtaResultsParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;

public class MtaModelParser {

    public static MtaModel parseModel(String fileName) {
        MtaModel mtaModel = new MtaModel();
        JSONParser parser = new JSONParser();
        if (new File(fileName).exists()) {
            try {
                Object obj = parser.parse(new FileReader(fileName));
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray configurations = (JSONArray) jsonObject.get("configurations");
                Iterator iterator = configurations.iterator();
                while (iterator.hasNext()) {
                    JSONObject config = (JSONObject) iterator.next();
                    MtaConfiguration configuration = MtaModelParser.parseConfigurationObject(config);
                    mtaModel.addConfiguration(configuration);
                    MtaResultsParser.parseResults(configuration, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mtaModel;
    }

    private static MtaConfiguration parseConfigurationObject(JSONObject configurationObjects) {
        MtaConfiguration mtaConfiguration = new MtaConfiguration();
        mtaConfiguration.setId((String) configurationObjects.get("id"));
        mtaConfiguration.setName((String) configurationObjects.get("name"));
        MtaModelParser.parseConfigurationOptionsObject(
                (Map) configurationObjects.get("options"),
                mtaConfiguration);
        Map summary = (Map) configurationObjects.get("summary");
        if (summary != null) {
            MtaModelParser.parseSummary(summary, mtaConfiguration);
        }
        return mtaConfiguration;
    }

    private static void parseConfigurationOptionsObject(Map optionsObject, MtaConfiguration configuration) {
        Iterator<Map.Entry> iterator = optionsObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = iterator.next();
            Object key = pair.getKey();
            Object value = pair.getValue();
            if (value instanceof JSONArray) {
                JSONArray optionValues = (JSONArray)value;
                configuration.addOption((String)key, optionValues);
            }
            else {
                configuration.addOption((String)key, String.valueOf(value));
            }
        }
    }

    private static void parseSummary(Map optionsObject, MtaConfiguration configuration) {
        AnalysisResultsSummary summary = new AnalysisResultsSummary();
        configuration.setSummary(summary);
        Iterator<Map.Entry> iterator = optionsObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = iterator.next();
            String key = (String)pair.getKey();
            Object value = pair.getValue();
            if ("skippedReports".equals(key)) {
                summary.skippedReports = (Boolean)value;
            }
            else if ("executedTimestamp".equals(key)) {
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
            else if ("quickfixes".equals(key)) {
                MtaModelParser.parseQuickfixesData((JSONObject)value, configuration);
            }
        }
    }

    private static void parseQuickfixesData(JSONObject jsonObject, MtaConfiguration configuration) {
        QuickfixData quickfixData = new QuickfixData();
        configuration.getSummary().quickfixData = quickfixData;
        Iterator<Map.Entry> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = iterator.next();
            Object key = pair.getKey();
            JSONObject value = (JSONObject)pair.getValue();

            QuickfixEntry entry = new QuickfixEntry();
            quickfixData.entries.put((String)key, entry);

            entry.originalLineSource = (String)value.get("originalLineSource");
            JSONObject quickFixedLines = (JSONObject)value.get("quickfixedLines");

            Iterator<Map.Entry> quickfixIterator = quickFixedLines.entrySet().iterator();
            while (quickfixIterator.hasNext()) {
                Map.Entry quickfixItem = quickfixIterator.next();
                entry.quickfixes.put((String) quickfixItem.getKey(), (String)quickfixItem.getValue());
            }
        }
    }
//    public static void loadQuickfixData(MtaConfiguration configuration) {
//        QuickfixData quickfixData = new QuickfixData();
//        configuration.getSummary().hints.forEach(issue -> {
//            QuickfixEntry entry = new QuickfixEntry();
//            entry.originalLineSource = issue.originalLineSource;
//            entry.quickfixes = issue.quickfixedLines;
//            quickfixData.entries.put(issue.id, entry);
//        });
//        configuration.getSummary().quickfixData = quickfixData;
//    }
}
