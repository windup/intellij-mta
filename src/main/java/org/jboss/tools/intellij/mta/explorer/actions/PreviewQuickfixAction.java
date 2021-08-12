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
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.explorer.MtaTreeCellRenderer;
import org.jboss.tools.intellij.mta.explorer.dialog.MtaNotifier;
import org.jboss.tools.intellij.mta.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.mta.explorer.nodes.HintNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jboss.tools.intellij.mta.model.QuickfixUtil;
import org.jboss.tools.intellij.mta.services.ModelService;

import javax.swing.tree.TreePath;
import java.io.File;
import java.nio.charset.Charset;

public class PreviewQuickfixAction extends StructureTreeAction {

    public PreviewQuickfixAction() {
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
            String oldValue = org.apache.commons.io.FileUtils.readFileToString(new File(hint.file));
            MtaConfiguration.QuickFix quickfix = hint.quickfixes.size() > 1 ? hint.quickfixes.get(1) : hint.quickfixes.get(0);
            String newValue = QuickfixUtil.getQuickFixedContent(quickfix);

            DocumentContent oldContent = DiffContentFactory.getInstance().create(oldValue);
            DocumentContent newContent = DiffContentFactory.getInstance().create(newValue);

            SimpleDiffRequest request = new SimpleDiffRequest("Diff", oldContent, newContent, "Original", "New Changes");

            request.putUserData(DiffUserDataKeys.SCROLL_TO_LINE, Pair.create(Side.RIGHT, 0));
            request.putUserData(DiffUserDataKeys.SCROLL_TO_LINE,Pair.create(Side.LEFT, 0));

            DialogBuilder diffBuilder = new DialogBuilder();
            DiffRequestPanel diffPanel = DiffManager.getInstance().createRequestPanel(project, diffBuilder, diffBuilder.getWindow());
            diffPanel.setRequest(request);
            diffBuilder.setCenterPanel(diffPanel.getComponent());
            diffBuilder.setDimensionServiceKey("FileDocumentManager.FileCacheConflict");
            diffBuilder.addOkAction().setText("Apply Quickfix");
            diffBuilder.addCancelAction();
            diffBuilder.setTitle("Preview Quickfix");
            if (diffBuilder.show() == DialogWrapper.OK_EXIT_CODE) {
                QuickfixUtil.applyQuickfix(quickfix, project, newValue);
                node.setComplete();
                renderer.getTreeModel().invalidate(path, false);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            MtaNotifier.notifyError("Error processing quickfix file - " + hint.file);
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
