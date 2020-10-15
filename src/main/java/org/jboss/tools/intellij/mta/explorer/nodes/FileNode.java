package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FileNode extends ResourceNode {

    private List<Hint> hints = Lists.newArrayList();
    private List<Classification> classifications = Lists.newArrayList();

    public FileNode(AnalysisResultsSummary summary, String file) {
        super(summary, file);
        this.computeIssue();
    }

    private void computeIssue() {
        for (Issue issue : super.getValue().getIssues()) {
            if (issue.file.equals(this.file.getAbsolutePath())) {
                this.addIssue(issue);
            }
        }
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<MtaExplorerNode<?>> children = Lists.newArrayList();
        children.addAll(this.classifications.stream().map(ClassificationNode::new).collect(Collectors.toList()));
        this.hints.sort(Comparator.comparingInt(o -> o.lineNumber));
        children.addAll(this.hints.stream().map(HintNode::new).collect(Collectors.toList()));
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(this.getFile().getName() +
                " (" + (this.hints.size() + this.classifications.size()) + ")");
        presentation.setIcon(AllIcons.FileTypes.Any_type);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }

    private void addIssue(Issue issue) {
        if (issue instanceof  Hint) {
            this.hints.add((Hint)issue);
        }
        else {
            this.classifications.add((Classification)issue);
        }
    }

    public File getFile() {
        return this.file;
    }
}
