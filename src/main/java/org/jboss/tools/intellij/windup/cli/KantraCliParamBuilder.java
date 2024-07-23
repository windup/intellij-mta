/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.cli;

import com.google.common.collect.Lists;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;

import java.util.List;
import java.util.Map;

public class KantraCliParamBuilder {

    public static List<String> buildParams(WindupConfiguration config, String windupHome) {
        List<String> params = Lists.newArrayList();
        Map<String, Object> options = config.getOptions();
        params.add("analyze");

        // input
        params.add("--input");
        List<String> input = (List<String>)options.get("input");
        input.forEach(path -> params.add(path));

        // output
        params.add("--output");
        String output = (String)options.get("output");
        params.add(output);

        if (options.containsKey("analyze-known-libraries")) {
            params.add("--analyze-known-libraries");
        }

        // userRulesDirectory
        List<String> rules = (List<String>)options.get("rules");
        if (rules != null && !rules.isEmpty()) {
           // params.add("--userRulesDirectory");
//            List<String> pathStrings = Lists.newArrayList();
//            rules.forEach(path -> pathStrings.add("\""+ path + "\""));
            for (String aPathStrings : rules) {
                params.add("--rules");
                params.add(aPathStrings);
            }
           // params.add(String.join(" ", pathStrings));
        }


        // overwrite
        if (options.containsKey("overwrite")) {
            params.add("--overwrite");
        }

        if (options.containsKey("source-only")) {
            params.add("--mode");
            params.add("source-only");
        }

        // target
        List<String> target = (List<String>)options.get("target");
        if (target == null || target.isEmpty()) {
            target = Lists.newArrayList();
            target.add("eap7");
        }
      //  params.add("--target");

        for (String aTarget : target) {
            params.add("--target");
            params.add(aTarget);
        }
//        params.add(String.join(",", target));


        // source
        List<String> source = (List<String>)options.get("source");
        if (source != null && !source.isEmpty()) {
            for (String aSource : source) {
                params.add("--source");
                params.add(aSource);
            }
        }
        //  params.add("--target");
        return params;
    }
}
