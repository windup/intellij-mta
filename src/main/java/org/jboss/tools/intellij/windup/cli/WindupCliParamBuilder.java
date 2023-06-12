/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.cli;

import com.google.common.collect.Lists;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;

import java.util.List;
import java.util.Map;

public class WindupCliParamBuilder {

    public static List<String> buildParams(WindupConfiguration config, String windupHome) {
        List<String> params = Lists.newArrayList();
        Map<String, Object> options = config.getOptions();
        params.add("--toolingMode");

        // input
        params.add("--input");
        List<String> input = (List<String>)options.get("input");
        input.forEach(path -> params.add("\"" + path + "\""));

        // output
        params.add("--output");
        String output = (String)options.get("output");
        params.add("\""+ output + "\"");

        // sourceMode
        if (options.containsKey("sourceMode")) {
            params.add("--sourceMode");
        }

        // skipReports
        if (options.containsKey("skipReports")) {
            params.add("--skipReports");
        }

        // ignorePattern
        params.add("--ignorePattern");
        params.add("\\.class$");

        // windupHome
        params.add("--windupHome");
        params.add(windupHome);

        // source
        List<String> source = (List<String>)options.get("source");
        if (source != null && !source.isEmpty()) {
            params.add("--source");
            params.add(String.join(" ", source));
        }

        // target
        List<String> target = (List<String>)options.get("target");
        if (target == null || target.isEmpty()) {
            target = Lists.newArrayList();
            target.add("eap7");
        }
        params.add("--target");

        for (String aTarget : target) {
            params.add(aTarget);
        }
//        params.add(String.join(",", target));

        // userRulesDirectory
        List<String> userRulesDirectory = (List<String>)options.get("userRulesDirectory");
        if (userRulesDirectory != null && !userRulesDirectory.isEmpty()) {
            params.add("--userRulesDirectory");
            List<String> pathStrings = Lists.newArrayList();
            userRulesDirectory.forEach(path -> pathStrings.add("\""+ path + "\""));
            params.add(String.join(" ", pathStrings));
        }

        // userIgnorePath
        List<String> userIgnorePath = (List<String>)options.get("userIgnorePath");
        if (userIgnorePath != null && !userIgnorePath.isEmpty()) {
            params.add("--userIgnorePath");
            params.add("\""+ userIgnorePath.get(0) +"\"");
        }

        // overwrite
        if (options.containsKey("overwrite")) {
            params.add("--overwrite");
        }

        // excludePackages
        List<String> excludePackages = (List<String>)options.get("excludePackages");
        if (excludePackages != null && !excludePackages.isEmpty()) {
            params.add("--excludePackages");
            params.add(String.join(" ", excludePackages));
        }

        // mavenizeGroupId
        String mavenizeGroupId = (String)options.get("mavenizeGroupId");
        if (mavenizeGroupId != null && !mavenizeGroupId.isEmpty()) {
            params.add("--mavenizeGroupId");
            params.add(mavenizeGroupId);
        }

        // exportCSV
        if (options.containsKey("exportCSV")) {
            params.add("--exportCSV");
        }

        // excludeTags
        List<String> excludeTags = (List<String>)options.get("excludeTags");
        if (excludeTags != null && !excludeTags.isEmpty()) {
            params.add("--excludeTags");
            params.add(String.join(" ", excludeTags));
        }

        // packages
        List<String> packages = (List<String>)options.get("packages");
        if (packages != null && !packages.isEmpty()) {
            params.add("--packages");
            params.add(String.join(" ", packages));
        }

        // additionalClasspath
        List<String> additionalClasspath = (List<String>)options.get("additionalClasspath");
        if (additionalClasspath != null && !additionalClasspath.isEmpty()) {
            params.add("--additionalClasspath");
            List<String> pathStrings = Lists.newArrayList();
            additionalClasspath.forEach(path -> pathStrings.add("\""+ path + "\""));
            params.add(String.join(" ", pathStrings));
        }

        // disableTattletale
        if (options.containsKey("disableTattletale")) {
            params.add("--disableTattletale");
        }

        // enableCompatibleFilesReport
        if (options.containsKey("enableCompatibleFilesReport")) {
            params.add("--enableCompatibleFilesReport");
        }

        // includeTags
        List<String> includeTags = (List<String>)options.get("includeTags");
        if (includeTags != null && !includeTags.isEmpty()) {
            params.add("--includeTags");
            params.add(String.join(" ", includeTags));
        }

        // online
        if (options.containsKey("online")) {
            params.add("--online");
        }

        // enableClassNotFoundAnalysis
        if (options.containsKey("enableClassNotFoundAnalysis")) {
            params.add("--enableClassNotFoundAnalysis");
        }

        // enableTransactionAnalysis
        if (options.containsKey("enableTransactionAnalysis")) {
            params.add("--enableTransactionAnalysis");
        }

        // enableTattletal
        if (options.containsKey("enableTattletale")) {
            params.add("--enableTattletale");
        }

        // explodedApp
        if (options.containsKey("explodedApp")) {
            params.add("--explodedApp");
        }

        // keepWorkDirs
        if (options.containsKey("keepWorkDirs")) {
            params.add("--keepWorkDirs");
        }

        // mavenize
        if (options.containsKey("mavenize")) {
            params.add("--mavenize");
        }

        // legacyReports
        if (options.containsKey("legacyReports")) {
            params.add("--legacyReports");
        }

        // inputApplicationName
        String inputApplicationName = (String)options.get("inputApplicationName");
        if (inputApplicationName != null && !inputApplicationName.isEmpty()) {
            params.add("--inputApplicationName");
            params.add(inputApplicationName);
        }
        return params;
    }
}
