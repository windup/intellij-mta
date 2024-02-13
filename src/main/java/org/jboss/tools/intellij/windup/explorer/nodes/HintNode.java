/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import org.jetbrains.annotations.NotNull;
import org.jboss.tools.intellij.windup.model.WindupConfiguration.*;
import java.util.Collection;
import java.util.List;

public class HintNode extends IssueNode<Incident> {

    public HintNode(Incident hint) {
        super(hint);
        // System.out.println("<<<<<<<<<<<<<<<<This is hint node>>>>>>>>>>>>>>>>>  " + hint.file);
    }

    @NotNull
    @Override
    public Collection<WindupExplorerNode<?>> getChildren() {
        List<WindupExplorerNode<?>> children = Lists.newArrayList();
        if (!this.getValue().quickfixes.isEmpty()) {
            children.add(new QuickfixGroupNode(this, this.getValue()));
        }
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(super.getValue().title);
        Incident hint = this.getValue();
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
