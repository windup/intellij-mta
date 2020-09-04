package org.jboss.tools.intellij.mta.explorer;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.mta.model.MtaModel;

public class MtaTreeCellRenderer extends NodeRenderer {

    private MtaModel model;
    private StructureTreeModel treeModel;

    public MtaTreeCellRenderer(MtaModel model, StructureTreeModel treeModel) {
        this.model = model;
        this.treeModel = treeModel;
    }

    public MtaModel getModel() {
        return this.model;
    }

    public StructureTreeModel getTreeModel() {
        return this.treeModel;
    }
}
