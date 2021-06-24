package org.jboss.tools.intellij.mta.explorer;

import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.mta.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.mta.explorer.nodes.NodeUtil;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.services.ModelService;
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
    public void testConfigurationNodeWithoutResults() {
        MtaConfiguration config = this.modelService.createConfiguration();
        ConfigurationNode node = mock(ConfigurationNode.class);
        Collection children = NodeUtil.getConfigurationNodeChildren(config);
        when(node.getChildren()).thenReturn(children);
        assertTrue(node.getChildren().isEmpty());
    }
}