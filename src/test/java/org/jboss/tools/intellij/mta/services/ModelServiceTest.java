/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.services;

import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class ModelServiceTest {

    private Project project;
    private ModelService modelService;

    @Before
    public void before() {
        this.project = mock(Project.class);
        modelService = new ModelService(this.project);
    }

    @After
    public void tearDown() {
        this.modelService.getModel().getConfigurations().clear();
    }

    @Test
    public void testNewConfigurationAdded() {
        WindupConfiguration configuration = this.modelService.createConfiguration();
        assertNotNull(configuration);
        assertNotNull(configuration.getName());
    }

    @Test
    public void testDefaultTargetTechnology() {
        WindupConfiguration configuration = this.modelService.createConfiguration();
        assertNotNull(configuration);
        assertNotNull(configuration.getOptions().get("target"));
    }
}
