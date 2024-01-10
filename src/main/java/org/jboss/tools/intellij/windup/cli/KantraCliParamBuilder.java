/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.cli;

import com.google.common.collect.Lists;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;

import java.util.List;
import java.util.Map;

public class KantraCliParamBuilder {

    public static List<String> buildParams(WindupConfiguration config) {
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

        return params;
    }
}
