/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.model;

import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class WindupModelTest {

    private Project project;
    private ModelService modelService;

    @Before
    public void before() {
        this.project = mock(Project.class);
        modelService = new ModelService(this.project);
        modelService.loadModel();
    }

    @Test
    public void testConfigurationAdded() {
        this.modelService.createConfiguration();
        assertFalse(this.modelService.getModel().getConfigurations().isEmpty());
    }

    @Test
    public void testConfigurationDeleted() {
        this.modelService.getModel().getConfigurations().clear();
        WindupConfiguration config = this.modelService.createConfiguration();
        this.modelService.deleteConfiguration(config);
        assertTrue(this.modelService.getModel().getConfigurations().isEmpty());
    }
}