package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaModel;

import javax.swing.tree.TreePath;

public class DeleteConfigurationAction extends StructureTreeAction {

    public DeleteConfigurationAction() {
        super(ConfigurationNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        ConfigurationNode node = (ConfigurationNode)super.adjust(selected);
        MtaConfiguration configuration = node.getValue();
        Tree tree = super.getTree(anActionEvent);
        MtaTreeCellRenderer renderer = (MtaTreeCellRenderer) tree.getCellRenderer();
        MtaModel model = renderer.getModelService().getModel();
        model.deleteConfiguration(configuration);
        renderer.getModelService().saveModel();
        //noinspection UnstableApiUsage
        renderer.getTreeModel().invalidate(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length == 0) return false;
        return super.isVisible(selected);
    }
}
