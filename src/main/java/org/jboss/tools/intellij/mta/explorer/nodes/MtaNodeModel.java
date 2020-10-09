package org.jboss.tools.intellij.mta.explorer.nodes;

import org.jboss.tools.intellij.mta.services.ModelService;

import java.util.*;

public class MtaNodeModel {

    private final ModelService modelService;
    private final List<ConfigurationNode> configurationNodes = new ArrayList<>();

    public MtaNodeModel(ModelService modelService) {
        this.modelService = modelService;
        this.buildModel();
    }

    private void buildModel() {
        this.modelService.getModel().getConfigurations().forEach(configuration -> {
            ConfigurationNode node = new ConfigurationNode(configuration, this.modelService);
            this.configurationNodes.add(node);
        });
    }

    public List<ConfigurationNode> getConfigurationNodes() {
        this.configurationNodes.clear();
        this.buildModel();
        return this.configurationNodes;
    }
}
