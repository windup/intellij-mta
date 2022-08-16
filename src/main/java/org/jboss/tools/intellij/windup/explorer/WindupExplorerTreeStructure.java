/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.util.treeView.AbstractTreeStructureBase;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.windup.editor.server.VertxService;
import org.jboss.tools.intellij.windup.explorer.nodes.WindupExplorerRootNode;
import org.jboss.tools.intellij.windup.explorer.nodes.WindupNodeModel;
import org.jboss.tools.intellij.windup.model.WindupModel;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WindupExplorerTreeStructure extends AbstractTreeStructureBase {

    private ModelService modelService;
    private VertxService vertxService;
    private StructureTreeModel treeModel;

    protected WindupExplorerTreeStructure(ModelService modelService, VertxService vertxService) {
        super(null);
        this.modelService = modelService;
        this.vertxService = vertxService;
        this.treeModel = new StructureTreeModel(this, modelService);
        this.modelService.setTreeModel(this.treeModel);
    }

    public StructureTreeModel getTreeModel() {
        return this.treeModel;
    }

    @Nullable
    @Override
    public List<TreeStructureProvider> getProviders() {
        return null;
    }

    @Override
    public Object getRootElement() {
        return new WindupExplorerRootNode(new WindupNodeModel(this.modelService, this.vertxService, this.treeModel));
    }

    @Override
    public void commit() {
    }

    @Override
    public Object[] getChildElements(Object element) {
        return super.getChildElements(element);
    }

    @Override
    public boolean hasSomethingToCommit() {
        return false;
    }
}
