/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class NodeUtil {

    public static @NotNull Collection<? extends AbstractTreeNode<?>> getConfigurationNodeChildren(
            WindupConfiguration configuration) {
        List<WindupExplorerNode<?>> children = Lists.newArrayList();
        WindupConfiguration.AnalysisResultsSummary summary = configuration.getSummary();
        if (summary != null) {
            if (!configuration.skippedReports()) {
                children.add(new ReportNode(configuration));
            }
            children.add(new AnalysisResultsNode(summary));
        }
        return children;
    }
}
