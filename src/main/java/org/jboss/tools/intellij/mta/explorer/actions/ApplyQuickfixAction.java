package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.Side;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.dialog.MtaNotifier;
import org.jboss.tools.intellij.mta.explorer.nodes.HintNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.QuickfixUtil;

import javax.swing.tree.TreePath;

public class ApplyQuickfixAction extends StructureTreeAction {

    public ApplyQuickfixAction() {
        super(HintNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        Tree tree = super.getTree(anActionEvent);
        MtaTreeCellRenderer renderer = (MtaTreeCellRenderer) tree.getCellRenderer();
        Project project = renderer.getModelService().getProject();
        HintNode node = (HintNode)super.adjust(selected);
        MtaConfiguration.Hint hint = node.getValue();
        try {
            MtaConfiguration.QuickFix quickfix = hint.quickfixes.size() > 1 ? hint.quickfixes.get(1) : hint.quickfixes.get(0);
            String newValue = QuickfixUtil.getQuickFixedContent(quickfix);
            QuickfixUtil.applyQuickfix(quickfix, project, newValue);
        }
        catch (Exception e) {
            e.printStackTrace();
            MtaNotifier.notifyError("Error processing quickfix - " + hint.file);
            return;
        }
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length != 1) return false;
        boolean valid = super.isVisible(selected);
        if (!valid) return false;

        HintNode node = (HintNode)super.adjust(selected[0]);
        if (node.getValue().quickfixes.isEmpty()) return false;
        return true;
    }
}
