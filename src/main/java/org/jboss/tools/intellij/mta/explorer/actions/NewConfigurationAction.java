package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaExplorerNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jboss.tools.intellij.mta.model.NameUtil;
import org.jboss.tools.intellij.mta.services.ModelService;

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
        configuration.getOptions().put("mta-cli", ModelService.computeMtaCliLocation());
        configuration.getOptions().put("output", ModelService.getConfigurationOutputLocation(configuration));
        model.addConfiguration(configuration);
        renderer.getModelService().saveModel();
        renderer.getTreeModel().invalidate();
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

