package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.apache.commons.io.comparator.NameFileComparator;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class AnalysisResultsNode extends MtaExplorerNode<AnalysisResultsSummary> {

    public AnalysisResultsNode(AnalysisResultsSummary summary) {
        super(summary);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<Issue> issues = Lists.newArrayList();
        issues.addAll(super.getValue().hints);
        issues.addAll(super.getValue().classifications);

        Map<String, FileNode> fileMap = Maps.newHashMap();
        issues.forEach(issue -> {
            FileNode fileNode = fileMap.get(issue.file);
            if (fileNode == null) {
                fileNode = new FileNode(super.getValue(), issue.file);
                fileMap.put(issue.file, fileNode);
            }
            fileNode.addIssue(issue);
        });

        List<FileNode> children = new ArrayList<>(fileMap.values());
        children.sort((o1, o2) -> {
            NameFileComparator comparator = new NameFileComparator();
            return comparator.compare(o1.getFile(), o2.getFile());
        });

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
