/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.model;

import java.util.ArrayList;
import java.util.List;

public class WindupModel {

    private List<WindupConfiguration> configurations = new ArrayList<>();

    public void addConfiguration(WindupConfiguration configuration) {
        this.configurations.add(configuration);
    }

    public boolean deleteConfiguration(WindupConfiguration configuration) {
        return this.configurations.remove(configuration);
    }
    
    public List<WindupConfiguration> getConfigurations() {
        return this.configurations;
    }
}