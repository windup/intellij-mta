/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
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
        List<MtaExplorerNode<?>> children = Lists.newArrayList();
        if (!this.getValue().quickfixes.isEmpty()) {
            children.add(new QuickfixGroupNode(this, this.getValue()));
        }
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(super.getValue().title);
        Hint hint = this.getValue();
        if (hint.complete) {
            presentation.setIcon(AllIcons.Actions.Commit);
        }
        else if (hint.category == null || hint.category.equals("") ||
                hint.category.contains("error") || hint.category.contains("mandatory")) {
            presentation.setIcon(AllIcons.General.BalloonError);
        }
        else if (hint.category.contains("potential")) {
            presentation.setIcon(AllIcons.General.BalloonWarning);
        }
        else {
            presentation.setIcon(AllIcons.General.BalloonInformation);
        }
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
