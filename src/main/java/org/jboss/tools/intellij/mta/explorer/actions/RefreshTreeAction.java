package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaExplorerNode;

import javax.swing.tree.TreePath;

public class RefreshTreeAction extends StructureTreeAction {

    public RefreshTreeAction() {
        super(MtaExplorerNode.class);
    }

    public void actionPerformed(AnActionEvent anActionEvent, TreePath[] path, Object[] selected) {
        Tree tree = super.getTree(anActionEvent);
        MtaTreeCellRenderer renderer = (MtaTreeCellRenderer) tree.getCellRenderer();
        //noinspection UnstableApiUsage
        renderer.getTreeModel().invalidate(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
    }
}

