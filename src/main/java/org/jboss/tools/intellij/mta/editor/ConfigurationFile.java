package org.jboss.tools.intellij.mta.editor;

import com.intellij.testFramework.LightVirtualFile;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

public class ConfigurationFile extends LightVirtualFile {

    private MtaConfiguration configuration;

    public ConfigurationFile(MtaConfiguration configuration) {
        super(configuration.getId());
        this.configuration = configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConfigurationFile)) {
            return false;
        }
        return this.configuration.getId() == ((ConfigurationFile)o).configuration.getId();
    }

    @Override
    public int hashCode() {
        return configuration.getId().hashCode();
    }
}
