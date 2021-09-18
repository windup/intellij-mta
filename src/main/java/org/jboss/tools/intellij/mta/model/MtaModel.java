/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.model;

import java.util.ArrayList;
import java.util.List;

public class MtaModel {

    private List<MtaConfiguration> configurations = new ArrayList<>();

    public void addConfiguration(MtaConfiguration configuration) {
        this.configurations.add(configuration);
    }

    public boolean deleteConfiguration(MtaConfiguration configuration) {
        return this.configurations.remove(configuration);
    }
    
    public List<MtaConfiguration> getConfigurations() {
        return this.configurations;
    }
}