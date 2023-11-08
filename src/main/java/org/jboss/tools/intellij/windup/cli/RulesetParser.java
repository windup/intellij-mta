package org.jboss.tools.intellij.windup.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jboss.tools.intellij.windup.model.KantraConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class RulesetParser {

    public static  List<KantraConfiguration.Ruleset> parseRuleset(String resultFilePath){

        try {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            File yamlFile = new File(resultFilePath);
            objectMapper.findAndRegisterModules();
            List<KantraConfiguration.Ruleset> ruleSets = objectMapper.readValue(yamlFile, new TypeReference<List<KantraConfiguration.Ruleset>>(){});
            if (ruleSets != null) {
                ruleSets.removeIf(ruleset -> (ruleset.getViolations() == null || ruleset.getViolations().isEmpty() ) );
                return ruleSets;
            } else {
                System.out.println("YAML file is empty or invalid.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + resultFilePath);
        } catch (Exception e) {
            System.err.println("Error parsing YAML: " + e.getMessage());
        }
        return null;
    }
    public static void parseRulesetForKantraConfig (KantraConfiguration configuration){
        if (configuration.getOptions() != null){
            String outputLocation = configuration.getRulesetResultLocation();
            configuration.setRulesets(parseRuleset(outputLocation));
            if(configuration.getSummary() != null){
                configuration.getSummary().setRulesets(parseRuleset(outputLocation));
            }

        }else{
            System.out.println("Error parsing Ruleset at: " + configuration.getRulesetResultLocation());
        }

    }

}
