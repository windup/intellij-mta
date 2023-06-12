/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.windup.explorer.WindupTreeCellRenderer;
import org.jboss.tools.intellij.windup.explorer.dialog.WindupNotifier;
import org.jboss.tools.intellij.windup.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.model.WindupModel;
import org.jboss.tools.intellij.windup.services.ModelService;

import javax.swing.tree.TreePath;
import java.util.Arrays;

public class DeleteConfigurationAction extends StructureTreeAction {

    public DeleteConfigurationAction() {
        super(ConfigurationNode.class);
    }

    @Override
    public void update(AnActionEvent e) {
        Tree tree = (Tree) e.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        boolean visible = (tree != null && tree.getSelectionPaths() != null && tree.getSelectionPaths().length >= 1);
        e.getPresentation().setEnabledAndVisible(visible);
    }
    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        // Extract the selected nodes
        Tree tree = getTree(anActionEvent);
        Object[] selectedNodes = getSelectedNodes(tree);

        Project project = anActionEvent.getProject();

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);


        // Iterate through each selected node
        for (Object selectedNode : selectedNodes) {
            ConfigurationNode node = (ConfigurationNode) super.adjust(selectedNode);
            WindupConfiguration configuration = node.getValue();
            WindupTreeCellRenderer renderer = (WindupTreeCellRenderer) tree.getCellRenderer();
            WindupModel model = renderer.getModelService().getModel();
            model.deleteConfiguration(configuration);
            ModelService.deleteOutput(configuration);
            renderer.getModelService().saveModel();
            renderer.getTreeModel().invalidate();

            // Close the corresponding tab
            Arrays.stream(fileEditorManager.getOpenFiles())
                    .filter(file -> file.getName().equals(configuration.getName()))
                    .findFirst()
                    .ifPresent(fileEditorManager::closeFile);

        }

        WindupNotifier.notifyInformation("Configurations have been deleted");
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length == 0) return false;
        return super.isVisible(selected);
    }
}
