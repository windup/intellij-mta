package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class FileNode extends ResourceNode {

    private HintsGroupNode hintsGroupNode;
    private ClassificationsGroupNode classificationsGroupNode;

    public FileNode(AnalysisResultsSummary summary, String file) {
        super(summary, file);
        this.computeIssue();
    }

    private void computeIssue() {
        boolean containsHints = false;
        boolean containsClassifications = false;
        for (Issue issue : super.getValue().getIssues()) {
            if (issue.file.equals(this.file.getAbsolutePath())) {
                if (issue instanceof Hint) {
                    containsHints = true;
                }
                else {
                    containsClassifications = true;
                }
            }
        }
        String file = this.getFile().getAbsolutePath();
        if (containsHints) {
            this.hintsGroupNode = new HintsGroupNode(super.getValue(), file);
        }
        if (containsClassifications) {
            this.classificationsGroupNode = new ClassificationsGroupNode(this.getValue(), file);
        }
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<MtaExplorerNode<?>> children = Lists.newArrayList();
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
        presentation.setPresentableText(this.getFile().getName());
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
