/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.windup.explorer.WindupTreeCellRenderer;
import org.jboss.tools.intellij.windup.explorer.nodes.WindupExplorerNode;
import org.jboss.tools.intellij.windup.model.WindupModel;

import javax.swing.tree.TreePath;

public class RefreshTreeAction extends StructureTreeAction {

    public RefreshTreeAction() {
        super(WindupExplorerNode.class);
    }

    public void actionPerformed(AnActionEvent anActionEvent, TreePath[] path, Object[] selected) {
        Tree tree = super.getTree(anActionEvent);
        WindupTreeCellRenderer renderer = (WindupTreeCellRenderer) tree.getCellRenderer();
        renderer.getModelService().forceReload();
        renderer.getTreeModel().invalidate();
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
    }
}

