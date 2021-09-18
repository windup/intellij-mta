/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.nodes;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public abstract class MtaExplorerNode<T> extends AbstractTreeNode<T> {

    protected MtaExplorerNode(T value) {
        super(null, value);
    }

    public void onDoubleClick(Project project, StructureTreeModel treeModel) {
    }

    public void onClick(Project project) {
    }

    public void onClick(DefaultMutableTreeNode treeNode, TreePath path, MtaTreeCellRenderer renderer) {

    }
}
