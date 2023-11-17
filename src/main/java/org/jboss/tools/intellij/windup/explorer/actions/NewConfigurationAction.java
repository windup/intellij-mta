/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.windup.explorer.WindupTreeCellRenderer;
import org.jboss.tools.intellij.windup.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.windup.explorer.nodes.WindupExplorerNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;

import javax.swing.tree.TreePath;

public class NewConfigurationAction extends StructureTreeAction {

    public NewConfigurationAction() {
        super(WindupExplorerNode.class);
    }

    public void actionPerformed(AnActionEvent anActionEvent, TreePath[] path, Object[] selected) {
        Tree tree = super.getTree(anActionEvent);
        WindupTreeCellRenderer renderer = (WindupTreeCellRenderer) tree.getCellRenderer();
        WindupConfiguration configuration = renderer.getModelService().createConfiguration();
        renderer.getModelService().saveModel();
        renderer.getTreeModel().invalidate();
        ConfigurationNode.openConfigurationEditor(configuration, renderer.getModelService(), renderer.getVertxService());
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length > 1) {
            return false;
        }
        if (selected.length == 1) {
            Object obj = StructureTreeAction.getElement(selected[0]);
            System.out.println("NewConfigurationAction isVisible???");
        }
        return super.isVisible(selected);
    }
}

