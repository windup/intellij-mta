/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassificationsGroupNode extends ResourceNode {

    private List<WindupConfiguration.Classification> classifications = Lists.newArrayList();

    public ClassificationsGroupNode(WindupConfiguration.AnalysisResultsSummary summary, String file) {
        super(summary, file);
        this.computeIssue();
    }

    private void computeIssue() {
        for (WindupConfiguration.Issue issue : this.summary.getIssues()) {
            if (issue.file.equals(this.file.getAbsolutePath()) && issue instanceof WindupConfiguration.Classification) {
                this.classifications.add((WindupConfiguration.Classification) issue);
            }
        }
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<WindupExplorerNode<?>> children = Lists.newArrayList();
        children.addAll(this.classifications.stream().map(ClassificationNode::new).collect(Collectors.toList()));
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Classifications" +
                " (" + (this.classifications.size()) + ")");
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
