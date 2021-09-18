/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.details;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class IssueDetailsViewFactory implements ToolWindowFactory {

    public static final String ID = "Issue Details";

    @Override
    public void createToolWindowContent(Project project, @NotNull ToolWindow toolWindow) {
        IssueDetailsView.getInstance(project).createToolWindowContent(toolWindow);
    }
}
