/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.windup.explorer.WindupTreeCellRenderer;
import org.jboss.tools.intellij.windup.explorer.nodes.IssueNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;

import javax.swing.tree.TreePath;

public class MarkIssueCompleteAction extends StructureTreeAction {

    public MarkIssueCompleteAction() {
        super(IssueNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        Tree tree = super.getTree(anActionEvent);
        WindupTreeCellRenderer renderer = (WindupTreeCellRenderer) tree.getCellRenderer();
        IssueNode node = (IssueNode) super.adjust(selected);
        node.setComplete();
        renderer.getTreeModel().invalidate(path, false);
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length != 1) return false;
        boolean valid = super.isVisible(selected);
        if (!valid) return false;
        Object node = super.adjust(selected[0]);
        if (node instanceof IssueNode) {
            return !((IssueNode)node).isComplete();
        }
        return false;
    }

    protected Object getSelected(Tree tree) {
        return tree.getSelectionModel().getSelectionPath().getLastPathComponent();
    }
}

