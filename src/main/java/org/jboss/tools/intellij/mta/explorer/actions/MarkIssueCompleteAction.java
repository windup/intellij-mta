package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.details.DetailsViewConsole;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.dialog.MtaNotifier;
import org.jboss.tools.intellij.mta.explorer.nodes.IssueNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class MarkIssueCompleteAction extends StructureTreeAction {

    public MarkIssueCompleteAction() {
        super(IssueNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        Tree tree = super.getTree(anActionEvent);
        MtaTreeCellRenderer renderer = (MtaTreeCellRenderer) tree.getCellRenderer();
        IssueNode node = (IssueNode) super.adjust(selected);
        node.setComplete();
        renderer.getTreeModel().invalidate(path, false);
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length != 1) return false;
        boolean valid = super.isVisible(selected);
        if (!valid) return false;
        return true;
    }

    protected Object getSelected(Tree tree) {
        return tree.getSelectionModel().getSelectionPath().getLastPathComponent();
    }
}

