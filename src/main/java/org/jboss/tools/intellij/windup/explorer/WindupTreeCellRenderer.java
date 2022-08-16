/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.windup.editor.server.VertxService;
import org.jboss.tools.intellij.windup.model.WindupModel;
import org.jboss.tools.intellij.windup.services.ModelService;

public class WindupTreeCellRenderer extends NodeRenderer {

    private ModelService modelService;
    private StructureTreeModel treeModel;
    private VertxService vertxService;

    public WindupTreeCellRenderer(ModelService modelService, VertxService vertxService, StructureTreeModel treeModel) {
        this.modelService = modelService;
        this.treeModel = treeModel;
        this.vertxService = vertxService;
    }

    public ModelService getModelService() {
        return this.modelService;
    }

    public StructureTreeModel getTreeModel() {
        return this.treeModel;
    }

    public VertxService getVertxService() {
        return this.vertxService;
    }
}
