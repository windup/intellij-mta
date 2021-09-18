/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.actions.PreviewQuickfixAction;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Collection;

public class QuickfixNode extends MtaExplorerNode<MtaConfiguration.QuickFix> {

    private HintNode hintNode;

    public QuickfixNode(MtaConfiguration.QuickFix quickfix, HintNode hintNode) {
        super(quickfix);
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
    public void onClick(DefaultMutableTreeNode treeNode, TreePath path, MtaTreeCellRenderer renderer) {
        path = path.getParentPath().getParentPath();
        PreviewQuickfixAction.openPreviewAndApply(this.getValue(), this.hintNode, path, renderer);
    }
}
