package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.ide.projectView.PresentationData;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class FileNode extends MtaExplorerNode<AnalysisResultsSummary> {

    public FileNode(AnalysisResultsSummary summary) {
        super(summary);
    }

    @NotNull
    @Override
    public Collection<MtaExplorerNode> getChildren() {
        List<MtaExplorerNode> children = Lists.newArrayList();
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
