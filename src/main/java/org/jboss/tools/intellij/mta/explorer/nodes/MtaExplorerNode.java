package org.jboss.tools.intellij.mta.explorer.nodes;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;

public abstract class MtaExplorerNode<T> extends AbstractTreeNode<T> {

    protected MtaExplorerNode(T value) {
        super(null, value);
    }

    public void onDoubleClick() {
    }
}