package org.jboss.tools.intellij.mta.explorer.nodes;

import gcom.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class HintsGroupNode extends ResourceNode {

    private List<MtaConfiguration.Hint> hints = Lists.newArrayList();

    public HintsGroupNode(MtaConfiguration.AnalysisResultsSummary summary, String file) {
        super(summary, file);
        this.computeIssue();
    }

    private void computeIssue() {
        List<MtaConfiguration.Hint> hints = super.getValue().hints;
        hints.sort(Comparator.comparingInt(o -> o.lineNumber));
        for (MtaConfiguration.Hint hint : hints) {
            if (hint.file.equals(this.file.getAbsolutePath())) {
                this.hints.add(hint);
            }
        }
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<MtaExplorerNode<?>> children = Lists.newArrayList();
        children.addAll(this.hints.stream().map(HintNode::new).collect(Collectors.toList()));
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Hints" +
                " (" + (this.hints.size()) + ")");
        presentation.setIcon(AllIcons.FileTypes.Any_type);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }

    public File getFile() {
        return this.file;
    }
}
