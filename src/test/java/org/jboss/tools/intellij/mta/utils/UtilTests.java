package org.jboss.tools.intellij.windup.utils;

import com.google.common.collect.Maps;
import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.model.WindupModelParser;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.junit.Test;
import org.junit.Before;

import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UtilTests {

    private Project project;
    private ModelService modelService;

    @Before
    public void before() {
        this.project = mock(Project.class);
        modelService = new ModelService(this.project);
        modelService.loadModel();
    }

    @Test
    public void testUniqueNames() {
        WindupConfiguration config1 = this.modelService.createConfiguration();
        WindupConfiguration config2 = this.modelService.createConfiguration();
        assertFalse(Objects.equals(config1.getName(), config2.getName()));
    }

    @Test
    public void testParseOptions() {
        WindupConfiguration config = this.modelService.createConfiguration();
        String target = "eap7";
        Map<String, String> options = Maps.newHashMap();
        options.put("target", target);
        WindupModelParser.parseConfigurationOptionsObject(options, config);
        assertEquals(config.getOptions().get("target"), target);
    }
}