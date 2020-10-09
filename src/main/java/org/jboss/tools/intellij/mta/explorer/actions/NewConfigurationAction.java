package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tree.AsyncTreeModel;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.nodes.IssueNode;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaExplorerNode;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaNodeModel;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jboss.tools.intellij.mta.model.NameUtil;

import javax.swing.tree.TreePath;

public class NewConfigurationAction extends StructureTreeAction {

    public NewConfigurationAction() {
        super(MtaExplorerNode.class);
    }

    public void actionPerformed(AnActionEvent anActionEvent, TreePath[] path, Object[] selected) {
        Tree tree = super.getTree(anActionEvent);
        MtaTreeCellRenderer renderer = (MtaTreeCellRenderer) tree.getCellRenderer();
        MtaModel model = renderer.getModelService().getModel();
        MtaConfiguration configuration = new MtaConfiguration();
        configuration.setId(MtaConfiguration.generateUniqueId());
        configuration.setName(NameUtil.generateUniqueConfigurationName(model));
        model.addConfiguration(configuration);
        renderer.getModelService().saveModel();
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

