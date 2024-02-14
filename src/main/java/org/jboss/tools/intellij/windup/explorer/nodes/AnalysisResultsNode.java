/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.windup.model.WindupConfiguration.*;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AnalysisResultsNode extends WindupExplorerNode<AnalysisResultsSummary> {

    private Map<String, ? extends AbstractTreeNode<?>> resourceNodes = Maps.newHashMap();

    public AnalysisResultsNode(AnalysisResultsSummary summary) {
        super(summary, summary);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<ResourceNode> children = Lists.newArrayList();
        ModelService modelService = super.getValue().getModelService();
        Project project = modelService.getProject();
        String root = project.getBasePath();
        if (root != null) {
            children.add(new FolderNode(super.getValue(), root));
            children.add (new DependencyNode(super.getValue(), "Dependecies"));
        }
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Results");
        presentation.setIcon(AllIcons.Actions.ShowAsTree);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }


}
