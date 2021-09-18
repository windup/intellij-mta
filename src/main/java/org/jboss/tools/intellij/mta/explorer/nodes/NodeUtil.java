/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class NodeUtil {

    public static @NotNull Collection<? extends AbstractTreeNode<?>> getConfigurationNodeChildren(
            MtaConfiguration configuration) {
        List<MtaExplorerNode<?>> children = Lists.newArrayList();
        MtaConfiguration.AnalysisResultsSummary summary = configuration.getSummary();
        if (summary != null) {
            if (!configuration.skippedReports()) {
                children.add(new ReportNode(configuration));
            }
            children.add(new AnalysisResultsNode(summary));
        }
        return children;
    }
}
