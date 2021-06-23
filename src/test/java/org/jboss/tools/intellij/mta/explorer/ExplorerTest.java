package org.jboss.tools.intellij.mta.explorer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.services.ModelService;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ExplorerTest {

    private Project project;
    private ModelService modelService;
    private ToolWindow toolWindow;

    @Before
    public void before() {
        this.project = mock(Project.class);
        this.toolWindow = mock(ToolWindow.class);
        modelService = new ModelService(this.project);
        modelService.loadModel();
    }

    @Test
    public void testInitializedTree() {
        MtaToolWindow panel = new MtaToolWindow(modelService, project, toolWindow);
        assertTrue(panel.getTree().isEmpty());
    }

    @Test
    public void testPopulated() {
        MtaToolWindow panel = new MtaToolWindow(modelService, project, toolWindow);
        modelService.createConfiguration();
        MtaTreeCellRenderer renderer = (MtaTreeCellRenderer)panel.getTree().getCellRenderer();
        renderer.getTreeModel().invalidate();
        assertFalse(panel.getTree().isEmpty());
    }

    @Test
    public void testDefaultTargetTechnology() {
        MtaConfiguration configuration = this.modelService.createConfiguration();
        assertNotNull(configuration);
        assertNotNull(configuration.getOptions().get("target"));
    }
}