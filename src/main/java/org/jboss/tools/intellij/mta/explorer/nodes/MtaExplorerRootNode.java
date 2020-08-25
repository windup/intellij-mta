package org.jboss.tools.intellij.mta.explorer.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MtaExplorerRootNode extends MtaExplorerNode<MtaNodeModel> {

    public MtaExplorerRootNode(MtaNodeModel nodeModel) {
        super(nodeModel);
    }

    @NotNull
    @Override
    public Collection<ConfigurationNode> getChildren() {
        return super.getValue().getConfigurationNodes();
    }

    @Override
    protected void update(PresentationData presentation) {
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }
}
