/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class HintsGroupNode extends ResourceNode {

    private List<WindupConfiguration.Hint> hints = Lists.newArrayList();

    public HintsGroupNode(WindupConfiguration.AnalysisResultsSummary summary, String file) {
        super(summary, file);
        this.computeIssue();
    }

    private void computeIssue() {
        List<WindupConfiguration.Hint> hints = this.summary.hints;
        hints.sort(Comparator.comparingInt(o -> o.lineNumber));
        for (WindupConfiguration.Hint hint : hints) {
            if (hint.file.equals(this.file.getAbsolutePath())) {
                this.hints.add(hint);
            }
        }
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<WindupExplorerNode<?>> children = Lists.newArrayList();
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
