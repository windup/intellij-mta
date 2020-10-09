package org.jboss.tools.intellij.mta.explorer;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jboss.tools.intellij.mta.services.ModelService;

public class MtaTreeCellRenderer extends NodeRenderer {

    private ModelService modelService;
    private StructureTreeModel treeModel;

    public MtaTreeCellRenderer(ModelService modelService, StructureTreeModel treeModel) {
        this.modelService = modelService;
        this.treeModel = treeModel;
    }

    public ModelService getModelService() {
        return this.modelService;
    }

    public StructureTreeModel getTreeModel() {
        return this.treeModel;
    }
}
