/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration.AnalysisResultsSummary;
import org.jboss.tools.intellij.windup.model.WindupConfiguration.Incident;
import org.jboss.tools.intellij.windup.model.WindupConfiguration.Issue;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class DependencyNode extends ResourceNode {

    private HintsGroupNode hintsGroupNode;
    private ClassificationsGroupNode classificationsGroupNode;

    public DependencyNode(AnalysisResultsSummary summary, String name) {
        super(summary, name);
        this.computeIssue();
    }

    private void computeIssue() {
        boolean containsHints = false;
        boolean containsClassifications = false;
        for (Issue issue : super.summary.getIssues()) {
            Path path = Paths.get(issue.file);
            if (!Files.exists(path)) {
               // System.out.println("************** this is the dependency issue : "+ issue.file);
                issue.file = this.getFile().getAbsolutePath().toString();
               // System.out.println("************** this is the dependency issue : "+ issue.file);
                if (issue instanceof Incident) {
                  //  System.out.println("issue is the instance of the Incident ");
                    containsHints = true;
                }
            }
        }
        String file = this.getFile().getAbsolutePath();
        if (containsHints) {
            this.hintsGroupNode = new HintsGroupNode(this.summary, file);
        }

    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<WindupExplorerNode<?>> children = Lists.newArrayList();
        if (this.hintsGroupNode != null) {
            children.add(this.hintsGroupNode);
        }
        if (this.classificationsGroupNode != null) {
            children.add(this.classificationsGroupNode);
        }
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Dependencies");
        presentation.setIcon(AllIcons.Nodes.PpLib);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }

    public File getFile() {
        return this.file;
    }
}
