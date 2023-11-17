/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jboss.tools.intellij.windup.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.windup.model.NameUtil;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.jetbrains.annotations.NotNull;

public class WindupExplorerFactory implements ToolWindowFactory {
    
    @Override
    public void createToolWindowContent(Project project, @NotNull ToolWindow toolWindow) {
        ModelService modelService = new ModelService(project);
        modelService.loadModel();
        WindupToolWindow panel = new WindupToolWindow(modelService, project, toolWindow);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(panel, null, false);
        contentManager.addContent(content);
        Disposer.register(content, modelService);
        Disposer.register(content, panel);
        if (modelService.getModel().getConfigurations().size() == 1) {
            WindupConfiguration configuration = modelService.getModel().getConfigurations().get(0);
            // If single default configuration, open in editor by default
            if (configuration.getName().startsWith(NameUtil.CONFIGURATION_ELEMENT_PREFIX)) {
                ConfigurationNode.openConfigurationEditor(
                        configuration, modelService, panel.getVertxService());
            }
        }
    }
}
