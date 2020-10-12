package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class HintNode extends IssueNode<Hint> {

    public HintNode(Hint hint) {
        super(hint);
    }

    @NotNull
    @Override
    public Collection<MtaExplorerNode<?>> getChildren() {
        return Lists.newArrayList();
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(super.getValue().title);
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }
}
