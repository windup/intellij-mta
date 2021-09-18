/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.editor.server;


import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonUtil {

    public static JsonObject getOptions(MtaConfiguration configuration) {
        JsonObject configObject = new JsonObject();
        configObject.put("id", configuration.getId());
        configObject.put("name", configuration.getName());
        JsonObject options = new JsonObject();
        configObject.put("options", options);

        for (Map.Entry<String, Object> entry : configuration.getOptions().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                options.put(key, (String)value);
            }
            else {
                JsonArray jsonArray = new JsonArray();
                options.put(key, jsonArray);
                for (String v : (List<String>)value) {
                    jsonArray.add(v);
                }
            }
        }

        configObject.put("help", JsonUtil.getHelpData());
        return configObject;
    }

    public static JsonObject getHelpData() {

        IdeaPluginDescriptor descriptor = PluginManager.getPlugin(PluginId.getId("org.jboss.tools.intellij.mta"));
        File helpFile = new File(descriptor.getPath(), "lib/webroot/help.json");

        JsonObject data = new JsonObject();

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(helpFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray options = (JSONArray) jsonObject.get("options");
            data.put("options", options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
