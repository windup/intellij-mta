/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.editor;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import org.jboss.tools.intellij.windup.editor.server.VertxService;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.jetbrains.annotations.NotNull;

public class ConfigurationFile extends LightVirtualFile {

    private WindupConfiguration configuration;
    private VertxService vertxService;
    private ModelService modelService;

    public ConfigurationFile(
            WindupConfiguration configuration,
            VertxService vertxService,
            ModelService modelService
    ) {
        super(configuration.getName());
        this.configuration = configuration;
        this.vertxService = vertxService;
        this.modelService = modelService;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public String toString() {
        return this.configuration.getId();
    }

    @Override
    public @NotNull String getPath() {
        return this.configuration.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConfigurationFile)) {
            return false;
        }
        return this.configuration.getId().equals(((ConfigurationFile) o).configuration.getId());
    }

    @Override
    public int hashCode() {
        return configuration.getId().hashCode();
    }

    public WindupConfiguration getConfiguration() {
        return this.configuration;
    }

    public VertxService getVertxService() {
        return this.vertxService;
    }

    public ModelService getModelService() {
        return this.modelService;
    }
}
