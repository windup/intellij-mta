package org.jboss.tools.intellij.mta.explorer.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class QuickfixNode extends MtaExplorerNode<MtaConfiguration.QuickFix> {

    private MtaConfiguration.QuickFix quickFix;

    public QuickfixNode(MtaConfiguration.QuickFix quickfix) {
        super(quickfix);
    }

    @Override
    public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
        return null;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {

    }
}
