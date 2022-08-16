/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import static org.jboss.tools.intellij.windup.model.WindupConfiguration.*;

public class QuickfixGroupNode extends ResourceNode {

    private HintNode hintNode;
    private Hint hint;

    public QuickfixGroupNode(HintNode hintNode, WindupConfiguration.Hint hint) {
        super(hint.configuration.getSummary(), hint.file);
        this.hintNode = hintNode;
        this.hint = this.hintNode.getValue();
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<WindupExplorerNode<?>> children = Lists.newArrayList();
        children.addAll(this.hint.quickfixes.stream().
                map(quickFix -> new QuickfixNode(quickFix, this.hintNode))
                .collect(Collectors.toList()));
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Quickfixes" +
                " (" + (this.hint.quickfixes.size()) + ")");
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

    public HintNode getHintNode() {
        return this.hintNode;
    }

    public Hint getHint() {
        return this.hint;
    }
}
