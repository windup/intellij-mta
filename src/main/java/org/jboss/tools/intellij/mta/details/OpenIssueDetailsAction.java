package org.jboss.tools.intellij.mta.details;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.actions.StructureTreeAction;
import org.jboss.tools.intellij.mta.explorer.dialog.MtaNotifier;
import org.jboss.tools.intellij.mta.explorer.nodes.HintNode;
import org.jboss.tools.intellij.mta.explorer.nodes.IssueNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

import javax.swing.tree.TreePath;

public class OpenIssueDetailsAction extends StructureTreeAction {

    private DetailsViewConsole detailsView;

    public OpenIssueDetailsAction() {
        super(IssueNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        Tree tree = super.getTree(anActionEvent);
        MtaTreeCellRenderer renderer = (MtaTreeCellRenderer) tree.getCellRenderer();
        Project project = renderer.getModelService().getProject();
        if (this.detailsView == null) {
            this.detailsView = new DetailsViewConsole(project);
        }
        IssueNode node = (IssueNode)super.adjust(selected);
        MtaConfiguration.Issue issue = (MtaConfiguration.Issue)node.getValue();
        try {
//            IssueDetailsView.showIssueDetailsView(issue, project);
            this.detailsView.open(issue);
        }
        catch (Exception e) {
            e.printStackTrace();
            MtaNotifier.notifyError("Error opening issue details - " + node.getValue().toString());
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
}