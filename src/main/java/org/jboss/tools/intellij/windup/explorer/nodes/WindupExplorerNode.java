/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.windup.explorer.WindupTreeCellRenderer;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public abstract class WindupExplorerNode<T> extends AbstractTreeNode<T> {

    protected WindupConfiguration.AnalysisResultsSummary summary;

    protected WindupExplorerNode(T value, WindupConfiguration.AnalysisResultsSummary summary) {
        super(null, value);
        this.summary = summary;
    }

    public void onDoubleClick(Project project, StructureTreeModel treeModel) {
    }

    public void onClick(Project project) {
    }

    public void onClick(DefaultMutableTreeNode treeNode, TreePath path, WindupTreeCellRenderer renderer) {

    }

    public WindupConfiguration.AnalysisResultsSummary getSummary() {
        return this.summary;
    }
}
