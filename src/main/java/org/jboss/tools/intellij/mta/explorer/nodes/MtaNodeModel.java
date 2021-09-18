/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.nodes;

import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.mta.editor.server.VertxService;
import org.jboss.tools.intellij.mta.services.ModelService;

import java.util.*;

public class MtaNodeModel {

    private final ModelService modelService;
    private final VertxService vertxService;
    private final StructureTreeModel treeModel;
    private final List<ConfigurationNode> configurationNodes = new ArrayList<>();

    public MtaNodeModel(ModelService modelService, VertxService vertxService, StructureTreeModel treeModel) {
        this.modelService = modelService;
        this.vertxService = vertxService;
        this.treeModel = treeModel;
        this.buildModel();
    }

    private void buildModel() {
        this.modelService.getModel().getConfigurations().forEach(configuration -> {
            ConfigurationNode node = new ConfigurationNode(configuration, this.modelService, this.vertxService, this.treeModel);
            this.configurationNodes.add(node);
            configuration.setNode(node);
        });
    }

    public List<ConfigurationNode> getConfigurationNodes() {
        this.configurationNodes.clear();
        this.buildModel();
        return this.configurationNodes;
    }
}
