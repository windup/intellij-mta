package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ClassificationNode extends IssueNode<Classification> {

    public ClassificationNode(Classification classification) {
        super(classification);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        return Lists.newArrayList();
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(super.getValue().title);
        Classification classification = this.getValue();
        if (classification.complete) {
            presentation.setIcon(AllIcons.Actions.Commit);
        }
        else if (classification.category == null || classification.category.equals("") ||
                classification.category.contains("error") || classification.category.contains("mandatory")) {
            presentation.setIcon(AllIcons.General.BalloonError);
        }
        else if (classification.category.contains("potential")) {
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
}
