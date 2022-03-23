/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.nodes;

import java.io.File;

import static org.jboss.tools.intellij.mta.model.MtaConfiguration.*;

public abstract class ResourceNode extends MtaExplorerNode<String> {

    public final File file;

    public ResourceNode(AnalysisResultsSummary summary, String resource) {
        super(resource, summary);
        this.file = new File(resource);
    }
}
