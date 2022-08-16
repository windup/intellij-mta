/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.details;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.windup.explorer.WindupTreeCellRenderer;
import org.jboss.tools.intellij.windup.explorer.actions.StructureTreeAction;
import org.jboss.tools.intellij.windup.explorer.dialog.WindupNotifier;
import org.jboss.tools.intellij.windup.explorer.nodes.IssueNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Arrays;

public class OpenIssueDetailsAction extends StructureTreeAction {

    private DetailsViewConsole detailsView;

    public OpenIssueDetailsAction() {
        super(IssueNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        Tree tree = super.getTree(anActionEvent);
        WindupTreeCellRenderer renderer = (WindupTreeCellRenderer) tree.getCellRenderer();
        Project project = renderer.getModelService().getProject();
        if (this.detailsView == null) {
            this.detailsView = new DetailsViewConsole(project);
            registerListeners(tree);
        }
        IssueNode node = (IssueNode)super.adjust(selected);
        this.showDetails(node);
    }

    private void showDetails(IssueNode node) {
        try {
            if (node == null) {
                this.detailsView.clear();
            }
            WindupConfiguration.Issue issue = (WindupConfiguration.Issue)node.getValue();
            this.detailsView.open(issue);
        }
        catch (Exception e) {
            e.printStackTrace();
            WindupNotifier.notifyError("Error opening issue details - " + node.getValue());
            return;
        }
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length != 1) return false;
        boolean valid = super.isVisible(selected);
        if (!valid) return false;
        return true;
    }

    private void registerListeners(Tree tree) {
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                if (path != null) {
                    Object obj = path.getLastPathComponent();
                    if (obj instanceof DefaultMutableTreeNode) {
                        Object userObj = ((DefaultMutableTreeNode) obj).getUserObject();
                        if (userObj instanceof IssueNode) {
                            IssueNode data = (IssueNode) userObj;
                            showDetails(data);
                        } else {
                            showDetails(null);
                        }
                    }
                }
            }
        });
    }

    protected Object getSelected(Tree tree) {
        return tree.getSelectionModel().getSelectionPath().getLastPathComponent();
    }
}
