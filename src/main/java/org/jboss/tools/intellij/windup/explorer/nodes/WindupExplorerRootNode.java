/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.windup.model.WindupModel;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class WindupExplorerRootNode extends WindupExplorerNode<WindupNodeModel> {

    public WindupExplorerRootNode(WindupNodeModel nodeModel) {
        super(nodeModel, null);
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
