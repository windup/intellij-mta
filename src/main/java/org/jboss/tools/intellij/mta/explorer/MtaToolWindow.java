package org.jboss.tools.intellij.mta.explorer;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TreeUIHelper;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.tree.AsyncTreeModel;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.cli.MtaCliRunner;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaExplorerNode;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaExplorerRootNode;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaNodeModel;
import org.jboss.tools.intellij.mta.model.MtaModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;

public class MtaToolWindow extends SimpleToolWindowPanel {

    private MtaModel mtaModel;
    private MtaCliRunner cliRunner;

    public MtaToolWindow(MtaModel mtaModel) {
        super(true, true);
        this.mtaModel = mtaModel;
        this.init();
        this.cliRunner = new MtaCliRunner();
    }

    private void init() {
        MtaNodeModel nodeModel = new MtaNodeModel(this.mtaModel);
        MtaExplorerRootNode rootNode = new MtaExplorerRootNode(nodeModel);
        MtaExplorerTreeStructure treeStructure = new MtaExplorerTreeStructure(rootNode);
        //noinspection UnstableApiUsage
        StructureTreeModel structureTreeModel = new StructureTreeModel(true);
        //noinspection UnstableApiUsage
        structureTreeModel.setStructure(treeStructure);
        AsyncTreeModel asyncTreeModelModel = new AsyncTreeModel(structureTreeModel, true);
        Tree mtaTree = this.createTree(asyncTreeModelModel, this.mtaModel, structureTreeModel);
        JScrollPane mtaTreePanel = ScrollPaneFactory.createScrollPane(mtaTree);
        NonOpaquePanel treePanelWrapper = new NonOpaquePanel();
        treePanelWrapper.setContent(mtaTreePanel);
        PopupHandler.installPopupHandler(mtaTree, "org.jboss.tools.intellij.mta.explorer", ActionPlaces.UNKNOWN);
        super.setContent(treePanelWrapper);
    }

    private Tree createTree(AsyncTreeModel asyncTreeModel, MtaModel model, StructureTreeModel treeModel) {
        Tree mtaTree = new Tree(asyncTreeModel);
        TreeUIHelper.getInstance().installTreeSpeedSearch(mtaTree);
        mtaTree.setRootVisible(false);
        mtaTree.setAutoscrolls(true);
        mtaTree.setCellRenderer(new MtaTreeCellRenderer(model, treeModel));
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                TreePath path = mtaTree.getClosestPathForLocation(event.getX(), event.getY());
                if (path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    MtaExplorerNode mtaNode = (MtaExplorerNode)treeNode.getUserObject();
                    mtaNode.onDoubleClick();
                }
                return true;
            }
        }.installOn(mtaTree);
        return mtaTree;
    }
}