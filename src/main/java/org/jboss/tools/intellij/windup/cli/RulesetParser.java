package org.jboss.tools.intellij.windup.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jboss.tools.intellij.windup.model.WindupConfiguration.*;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RulesetParser {

    public static List<Ruleset> parseRuleset(String resultFilePath) {
        Thread currentThread = Thread.currentThread();
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();
        ClassLoader jacksonClassLoader = RulesetParser.class.getClassLoader();

        try {
            currentThread.setContextClassLoader(jacksonClassLoader);

            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            File yamlFile = new File(resultFilePath);
            List<WindupConfiguration.Ruleset> ruleSets = objectMapper.readValue(
                    yamlFile, new TypeReference<List<WindupConfiguration.Ruleset>>(){}
            );
            if (ruleSets != null) {
                System.out.println("**************** In Parser --> Size of the ruleSet ***************" + ruleSets.size());
                ruleSets.removeIf(ruleset -> (ruleset.getViolations() == null || ruleset.getViolations().isEmpty()));
                return ruleSets;
            } else {
                System.out.println("YAML file is empty or invalid.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found : this is the problem ----> " + resultFilePath);
        } catch (Exception e) {
            System.err.println("Error parsing YAML: " + e.getMessage());
        } finally {
            currentThread.setContextClassLoader(originalClassLoader);
        }
        return null;
    }

    public static void parseRulesetForKantraConfig (WindupConfiguration configuration){
        if (configuration.getOptions() != null){
            String outputLocation = configuration.getRulesetResultLocation();
            configuration.setRulesets(parseRuleset(outputLocation));

            if(configuration.getSummary() != null){
                configuration.getSummary().setRulesets(parseRuleset(outputLocation));
                parseIncidents(configuration.getRulesets(), configuration);
                System.out.println("size of the Incident: ");
                System.out.println(configuration.getSummary().getIssues().size());
            }else {
                System.out.println(" configuration.getSummary() is null");
            }

        }else{
            System.out.println("Error parsing Ruleset at: " + configuration.getRulesetResultLocation());
        }

    }

    public static void parseIncidents (List<WindupConfiguration.Ruleset> rulesets, WindupConfiguration configuration) {
        if (rulesets != null){
            for (WindupConfiguration.Ruleset ruleset: rulesets){
                Map<String, Violation> violations = ruleset.getViolations();
                if (violations != null ){
                    for (Map.Entry<String, WindupConfiguration.Violation> entry : violations.entrySet()) {
                    //for(WindupConfiguration.Violation violation : violations.values()){
                        WindupConfiguration.Violation violation = entry.getValue();
                        List<WindupConfiguration.Incident> incidents = violation.getIncidents();
                        for (WindupConfiguration.Incident incident : incidents ) {
                            incident.id = WindupConfiguration.generateUniqueId();
                            incident.title = violation.getDescription().split("\n", 2)[0];
                            ArrayList<String> inputs = (ArrayList<String>) configuration.getOptions().get("input");
                            String input = inputs.get(0);
                            String filePath =  incident.getUri();;
                            incident.ruleId = entry.getKey();
                            String absolutePath = filePath.substring(filePath.indexOf("/source-code") + "/source-code".length());
                            System.out.println("input:  " + input);
                            System.out.println("Absolute path:  "+input + absolutePath);
                            incident.file = input + absolutePath;
                            incident.setUri(input + absolutePath);
                            System.out.println("File path of the incidents:  " + incident.file);
                            incident.effort = String.valueOf(violation.getEffort());
                            incident.links = violation.getLinks();
                            incident.category = violation.getCategory();
                            if (configuration.getSummary().completeIssues.contains(incident.id)) {
                                incident.complete = true;
                            }
                            incident.configuration = configuration;
                            configuration.getSummary().incidents.add(incident);
                        }
                    }
                }
            }

        }

    }


}
