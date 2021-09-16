package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class QuickfixGroupNode extends MtaExplorerNode<MtaConfiguration.Hint> {

    private HintNode hintNode;

    public QuickfixGroupNode(HintNode hintNode, MtaConfiguration.Hint hint) {
        super(hint);
        this.hintNode = hintNode;
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<MtaExplorerNode<?>> children = Lists.newArrayList();
        children.addAll(this.getValue().quickfixes.stream().map(quickFix -> {
            return new QuickfixNode(quickFix, this.hintNode);
        }).collect(Collectors.toList()));
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Quickfixes" +
                " (" + (this.getValue().quickfixes.size()) + ")");
        presentation.setIcon(AllIcons.FileTypes.Any_type);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }

    @Override
    public boolean isAlwaysLeaf() {
        return false;
    }
}
