/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer;

import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.windup.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.windup.explorer.nodes.NodeUtil;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.junit.Test;
import org.junit.Before;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExplorerTest {

    private Project project;
    private ModelService modelService;

    @Before
    public void before() {
        this.project = mock(Project.class);
        modelService = new ModelService(this.project);
        modelService.loadModel();
    }

    @Test
    public void testConfigurationWithResults() {
        WindupConfiguration config = this.modelService.createConfiguration();
        config.getOptions().put("output", "tmp");
        WindupConfiguration.AnalysisResultsSummary summary = new WindupConfiguration.AnalysisResultsSummary(modelService);
        summary.outputLocation = (String)config.getOptions().get("output");
        config.setSummary(summary);
        assertTrue(summary.getIssues().isEmpty());
    }

    @Test
    public void testConfigurationNodeWithoutResults() {
        WindupConfiguration config = this.modelService.createConfiguration();
        ConfigurationNode node = mock(ConfigurationNode.class);
        Collection children = NodeUtil.getConfigurationNodeChildren(config);
        when(node.getChildren()).thenReturn(children);
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    public void testNotSkipReports() {
        WindupConfiguration config = this.modelService.createConfiguration();
        assertFalse(config.skippedReports());
    }
}