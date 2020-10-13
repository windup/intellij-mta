package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class FolderNode extends MtaExplorerNode<AnalysisResultsSummary> {

    public FolderNode(AnalysisResultsSummary summary) {
        super(summary);
    }

    @Override
    public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
        return Lists.newArrayList();
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Results");
        presentation.setIcon(AllIcons.Nodes.Folder);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }
}
