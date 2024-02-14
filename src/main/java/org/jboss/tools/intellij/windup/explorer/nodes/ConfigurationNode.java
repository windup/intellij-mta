/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.windup.editor.ConfigurationFile;
import org.jboss.tools.intellij.windup.editor.server.VertxService;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConfigurationNode extends WindupExplorerNode<WindupConfiguration> {

    private ModelService modelService;
    private VertxService vertxService;
    private StructureTreeModel treeModel;

    public ConfigurationNode(WindupConfiguration configuration, ModelService modelService, VertxService vertxService, StructureTreeModel treeModel) {
        super(configuration, configuration.getSummary());
        this.modelService = modelService;
        this.vertxService = vertxService;
        this.treeModel = treeModel;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(this.getText());
        presentation.setIcon(IconLoader.getIcon("/icons/configuration/configuration.svg"));
    }

    @Override
    public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
        return NodeUtil.getConfigurationNodeChildren(this.getValue());
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }

    public String getText() {
        return this.getValue().getName();
    }

    @Override
    public void onDoubleClick(Project project, StructureTreeModel treeModel) {
        this.openConfigurationEditor(this.getValue(), modelService, vertxService);
    }

    public static void openConfigurationEditor(WindupConfiguration configuration, ModelService modelService, VertxService vertxService) {
        ConfigurationNode.openConfigurationEditor(new ConfigurationFile(configuration, vertxService, modelService), modelService.getProject());
    }

    public static void openConfigurationEditor(ConfigurationFile file, Project project) {
        try {
            FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, file),true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ModelService getModelService() {
        return this.modelService;
    }

    public StructureTreeModel getTreeModel() {
        return this.treeModel;
    }
}
