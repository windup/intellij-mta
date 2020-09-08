package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.AnalysisResults;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AnalysisResultsNode extends MtaExplorerNode<AnalysisResults> {

    public AnalysisResultsNode(AnalysisResults analysisResults) {
        super(analysisResults);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        return Lists.newArrayList();
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Results");
    }

    @Override
    protected boolean shouldUpdateData() {
        return false;
    }
}