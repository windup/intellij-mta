package org.jboss.tools.intellij.mta.explorer;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.mta.editor.server.VertxService;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jboss.tools.intellij.mta.services.ModelService;

public class MtaTreeCellRenderer extends NodeRenderer {

    private ModelService modelService;
    private StructureTreeModel treeModel;
    private VertxService vertxService;

    public MtaTreeCellRenderer(ModelService modelService, VertxService vertxService, StructureTreeModel treeModel) {
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
