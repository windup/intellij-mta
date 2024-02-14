/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.windup.explorer.WindupTreeCellRenderer;
import org.jboss.tools.intellij.windup.explorer.actions.PreviewQuickfixAction;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Collection;

public class QuickfixNode extends WindupExplorerNode<WindupConfiguration.QuickFix> {

    private HintNode hintNode;

    public QuickfixNode(WindupConfiguration.QuickFix quickfix, HintNode hintNode) {
        super(quickfix, hintNode.getSummary());
        this.hintNode = hintNode;
    }

    @Override
    public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
        return Lists.newArrayList();
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(super.getValue().name);
        presentation.setIcon(AllIcons.General.BalloonInformation);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }

    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }

    @Override
    public void onClick(DefaultMutableTreeNode treeNode, TreePath path, WindupTreeCellRenderer renderer) {
        path = path.getParentPath().getParentPath();
        PreviewQuickfixAction.openPreviewAndApply(this.getValue(), this.hintNode, path, renderer);
    }

    public HintNode getHintNode() {
        return this.hintNode;
    }

    public WindupConfiguration.Incident getHint() {
        return this.hintNode.getValue();
    }
}
