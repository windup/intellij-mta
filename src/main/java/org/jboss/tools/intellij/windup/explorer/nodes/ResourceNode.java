/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import java.io.File;

import static org.jboss.tools.intellij.windup.model.WindupConfiguration.*;

public abstract class ResourceNode extends WindupExplorerNode<String> {

    public final File file;

    public ResourceNode(AnalysisResultsSummary summary, String resource) {
        super(resource, summary);
        this.file = new File(resource);
    }
}
