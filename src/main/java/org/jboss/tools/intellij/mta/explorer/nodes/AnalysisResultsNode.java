package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AnalysisResultsNode extends MtaExplorerNode<AnalysisResultsSummary> {

    public AnalysisResultsNode(AnalysisResultsSummary summary) {
        super(summary);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<MtaExplorerNode<?>> children = Lists.newArrayList();
        children.addAll(super.getValue().hints.stream().map(HintNode::new).collect(Collectors.toList()));
        children.addAll(super.getValue().classifications.stream().map(ClassificationNode::new).collect(Collectors.toList()));
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Results");
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }
}
