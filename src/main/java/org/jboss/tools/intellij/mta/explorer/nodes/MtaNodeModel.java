package org.jboss.tools.intellij.mta.explorer.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MtaNodeModel {

    private MtaModel mtaModel;
    private List<ConfigurationNode> configurationNodes = new ArrayList<ConfigurationNode>();

    public MtaNodeModel(MtaModel mtaModel) {
        this.mtaModel = mtaModel;
        this.buildModel();
    }

    private void buildModel() {
        this.mtaModel.getConfigurations().forEach(configuration -> {
            ConfigurationNode node = new ConfigurationNode(configuration);
            this.configurationNodes.add(node);
        });
    }

    public List<ConfigurationNode> getConfigurationNodes() {
        return this.configurationNodes;
    }
}
