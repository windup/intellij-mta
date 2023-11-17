/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jboss.tools.intellij.windup.model.WindupConfiguration.*;

import java.io.File;
import java.util.*;

public class FolderNode extends ResourceNode {

    public FolderNode(AnalysisResultsSummary summary, String file) {
        super(summary, file);
        System.out.println ("<<<<<<<<<<<<< This is FolderNode >>>>>>>>>>>>");
    }

    @Override
    public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<ResourceNode> children = Lists.newArrayList();
        Set<String> resolvedFiles = Sets.newHashSet();
        for (Issue issue : this.summary.getIssues()) {
            File childFile = this.findChildFile(issue.file);
            if (childFile != null && !resolvedFiles.contains(childFile.getAbsolutePath())) {
                ResourceNode node = childFile.isDirectory() ?
                        new FolderNode(this.summary, childFile.getAbsolutePath()) :
                        new FileNode(this.summary, childFile.getAbsolutePath());
                resolvedFiles.add(childFile.getAbsolutePath());
                children.add(node);
            }
        }
        children.sort(Comparator.comparing(o -> o.file.getName()));
        return children;
    }

    private File findChildFile(String path) {
        File file = new File(path);
        while (file != null && file.getParent() != null) {
            if (file.getParent().equals(this.file.getAbsolutePath())) {
                return file;
            }
            file = file.getParentFile();
        }
        return null;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(this.file.getName());
        presentation.setIcon(AllIcons.Nodes.Folder);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }
}
