package org.jboss.tools.intellij.mta.utils;

import com.google.common.collect.Maps;
import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaModelParser;
import org.jboss.tools.intellij.mta.services.ModelService;
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
        MtaConfiguration config1 = this.modelService.createConfiguration();
        MtaConfiguration config2 = this.modelService.createConfiguration();
        assertFalse(Objects.equals(config1.getName(), config2.getName()));
    }

    @Test
    public void testParseOptions() {
        MtaConfiguration config = this.modelService.createConfiguration();
        String target = "eap7";
        Map<String, String> options = Maps.newHashMap();
        options.put("target", target);
        MtaModelParser.parseConfigurationOptionsObject(options, config);
        assertEquals(config.getOptions().get("target"), target);
    }
}