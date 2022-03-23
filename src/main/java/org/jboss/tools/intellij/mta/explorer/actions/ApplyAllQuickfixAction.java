/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.actions;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.dialog.MtaNotifier;
import org.jboss.tools.intellij.mta.explorer.nodes.*;
import org.jboss.tools.intellij.mta.model.QuickfixUtil;

import static org.jboss.tools.intellij.mta.model.MtaConfiguration.*;

import javax.swing.tree.TreePath;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ApplyAllQuickfixAction extends StructureTreeAction {

    public ApplyAllQuickfixAction() {
        super(MtaExplorerNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        Tree tree = super.getTree(anActionEvent);
        MtaTreeCellRenderer renderer = (MtaTreeCellRenderer) tree.getCellRenderer();
        Project project = renderer.getModelService().getProject();
        Object adjusted = super.adjust(selected);
        if (!(adjusted instanceof MtaExplorerNode)) {
            MtaNotifier.notifyError("Error processing all quickfixes");
            return;
        }
        try {
            MtaExplorerNode explorerNode = (MtaExplorerNode)adjusted;
            this.applyQuickfixes(explorerNode, project);
            if (explorerNode instanceof QuickfixNode) {
                path = path.getParentPath().getParentPath();
            }
            else if (explorerNode instanceof QuickfixGroupNode) {
                path = path.getParentPath();
            }
            renderer.getTreeModel().invalidate(path, true);
        }
        catch (Exception e) {
            e.printStackTrace();
            MtaNotifier.notifyError("Error processing all quickfixes");
        }
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length != 1) return false;
        boolean valid = super.isVisible(selected);
        if (!valid) return false;
        Object node = super.adjust(selected[0]);
        if (!(node instanceof MtaExplorerNode)) return false;
        if (node instanceof ClassificationNode) return false;
        if (node instanceof ClassificationsGroupNode) return false;
        List<QuickFix> quickfixes = this.computeQuickfixes((MtaExplorerNode)node);
        return !quickfixes.isEmpty();
    }

    private void applyQuickfixes(MtaExplorerNode explorerNode, Project project) {
        for (QuickFix quickfix : this.computeQuickfixes(explorerNode)) {
            try {
                String newValue = QuickfixUtil.getQuickFixedContent(quickfix);
                QuickfixUtil.applyQuickfix(quickfix, project, newValue);
                quickfix.issue.complete = true;
                explorerNode.getSummary().completeIssues.add(quickfix.issue.id);
            }
            catch (Exception e) {
                e.printStackTrace();
                MtaNotifier.notifyError("Error applying quickfix to file - " + quickfix.file);
                return;
            }
        }
    }

    private List<QuickFix> getQuickfixes(Hint hint) {
        List<QuickFix> quickfixes = Lists.newArrayList();
        if (!hint.quickfixes.isEmpty()) {
            QuickFix quickfix = hint.quickfixes.size() > 1 ?
                    hint.quickfixes.get(1) : hint.quickfixes.get(0);
            quickfixes.add(quickfix);
        }
        return quickfixes;
    }

    private List<QuickFix> computeQuickfixes(MtaExplorerNode explorerNode) {
        if (explorerNode instanceof QuickfixGroupNode) {
            Hint hint = ((QuickfixGroupNode)explorerNode).getHint();
            return this.getQuickfixes(hint);
        }
        if (explorerNode instanceof QuickfixNode) {
            List<QuickFix> quickfixes = Lists.newArrayList();
            QuickFix quickFix = ((QuickfixNode)explorerNode).getValue();
            quickfixes.add(quickFix);
            return quickfixes;
        }
        if (explorerNode instanceof HintNode) {
            return this.getQuickfixes(((HintNode)explorerNode).getValue());
        }
        if (explorerNode instanceof ConfigurationNode || explorerNode instanceof AnalysisResultsNode) {
            return explorerNode.getSummary().getIssues()
                    .stream()
                    .filter(issue -> issue instanceof Hint)
                    .map(issue -> (Hint)issue)
                    .map(hint -> this.getQuickfixes(hint))
                    .flatMap(fixes -> fixes.stream())
                    .collect(Collectors.toList());
        }

        // Otherwise, collect all quickfixes from all hints under this resource node.
        // Only hints under this (file/folder) resource (ie., FolderNode, FileNode, HintsGroupNode)
        if (explorerNode instanceof ResourceNode) {
            ResourceNode node = (ResourceNode)explorerNode;
            return explorerNode.getSummary().getIssues()
                    .stream()
                    .filter(issue -> issue instanceof Hint)
                    .map(issue -> (Hint)issue)
                    .filter(hint -> this.isChildFile(node.file, hint.file))
                    .map(hint -> this.getQuickfixes(hint))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private boolean isChildFile(File explorerNodeFile, String path) {
        if (explorerNodeFile.getAbsolutePath().equals(path)) {
            return true;
        }
        File file = new File(path);
        String explorerNodeFilePath = explorerNodeFile.getAbsolutePath();
        while (file != null && file.getParent() != null) {
            if (file.getParent().equals(explorerNodeFilePath)) {
                return true;
            }
            file = file.getParentFile();
        }
        return false;
    }
}
