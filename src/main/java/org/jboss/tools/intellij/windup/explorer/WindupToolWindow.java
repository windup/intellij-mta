/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.*;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.tree.AsyncTreeModel;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.windup.cli.WindupCliRunner;
import org.jboss.tools.intellij.windup.editor.server.VertxService;
import org.jboss.tools.intellij.windup.explorer.nodes.*;
import org.jboss.tools.intellij.windup.model.WindupModel;
import org.jboss.tools.intellij.windup.services.ModelService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;

public class WindupToolWindow extends SimpleToolWindowPanel implements Disposable {

    private ModelService modelService;
    private WindupCliRunner cliRunner;
    private Project project;
    private ToolWindow toolWindow;
    private VertxService vertxService;
    private Tree tree;

    public WindupToolWindow(ModelService modelService, Project project, ToolWindow toolWindow) {
        super(true, true);
        this.modelService = modelService;
        this.project = project;
        this.toolWindow = toolWindow;
        this.vertxService = new VertxService();
        this.init();
        this.cliRunner = new WindupCliRunner();
    }

    private void init() {
        WindupExplorerTreeStructure treeStructure = new WindupExplorerTreeStructure(modelService, vertxService);
        AsyncTreeModel asyncTreeModelModel = new AsyncTreeModel(treeStructure.getTreeModel(), true, project);
        Tree windupTree = (this.tree = this.createTree(asyncTreeModelModel, this.modelService.getModel(), treeStructure.getTreeModel(), this.vertxService));
        JScrollPane windupTreePanel = ScrollPaneFactory.createScrollPane(windupTree);
        NonOpaquePanel treePanelWrapper = new NonOpaquePanel();
        treePanelWrapper.setContent(windupTreePanel);
        PopupHandler.installPopupHandler(windupTree, "org.jboss.tools.intellij.windup.explorer", ActionPlaces.UNKNOWN);
        super.setContent(treePanelWrapper);
    }

    private Tree createTree(AsyncTreeModel asyncTreeModel, WindupModel model, StructureTreeModel treeModel, VertxService vertxService) {
        Tree windupTree = new Tree(asyncTreeModel);
        TreeUIHelper.getInstance().installTreeSpeedSearch(windupTree);
        windupTree.setRootVisible(false);
        windupTree.setAutoscrolls(true);
        WindupTreeCellRenderer renderer = new WindupTreeCellRenderer(modelService, vertxService, treeModel);
        windupTree.setCellRenderer(renderer);
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                TreePath path = windupTree.getClosestPathForLocation(event.getX(), event.getY());
                if (path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (treeNode.getUserObject() instanceof WindupExplorerNode) {
                        WindupExplorerNode windupNode = (WindupExplorerNode) treeNode.getUserObject();
                        windupNode.onDoubleClick(WindupToolWindow.this.project, treeModel);
                    }
                }
                return true;
            }
        }.installOn(windupTree);
        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent event, int clickCount) {
                TreePath path = windupTree.getClosestPathForLocation(event.getX(), event.getY());
                if (path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (treeNode.getUserObject() instanceof WindupExplorerNode) {
                        WindupExplorerNode windupNode = (WindupExplorerNode) treeNode.getUserObject();
                        if (windupNode instanceof IssueNode || windupNode instanceof ReportNode) {
                            windupNode.onClick(WindupToolWindow.this.project);
                        }
                        else if (windupNode instanceof QuickfixNode) {
                            windupNode.onClick(treeNode, path, renderer);
                        }
                    }
                }
                return true;
            }
        }.installOn(windupTree);
        return windupTree;
    }

    public VertxService getVertxService() {
        return this.vertxService;
    }

    public Tree getTree() {
        return this.tree;
    }

    @Override
    public void dispose() {
        this.vertxService.dispose();
    }
}