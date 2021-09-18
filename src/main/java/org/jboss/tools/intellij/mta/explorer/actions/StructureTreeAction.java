/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.ide.util.treeView.NodeDescriptor;

import javax.swing.tree.DefaultMutableTreeNode;

public abstract class StructureTreeAction extends AbstractTreeAction {

    public StructureTreeAction(Class... filters) {
        super(filters);
    }

    public StructureTreeAction(boolean acceptMultipleItems, Class... filters) {
        super(acceptMultipleItems, filters);
    }

    public static <T> T getElement(Object selected) {
        if (selected instanceof DefaultMutableTreeNode) {
            selected = ((DefaultMutableTreeNode)selected).getUserObject();
        }
        if (selected instanceof NodeDescriptor) {
            selected = ((NodeDescriptor)selected).getElement();
        }
        return (T) selected;
    }

    @Override
    protected Object adjust(Object selected) {
        return getElement(selected);
    }
}
