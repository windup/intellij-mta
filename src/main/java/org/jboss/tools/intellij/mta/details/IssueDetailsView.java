/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.details;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javax.swing.*;

import static org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

/**
 * The contents Issue Details tool window.
 */
public class IssueDetailsView {

    private final Project project;

    private JPanel myToolWindowPanel;
    private JLabel detailsTitle;

    @NotNull
    public static IssueDetailsView getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, IssueDetailsView.class);
    }

    @VisibleForTesting
    public IssueDetailsView(@NotNull Project project) {
        this.project = project;
    }

    public static void showIssueDetailsView(Issue issue, Project project) {
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = manager.getToolWindow(IssueDetailsViewFactory.ID);
        if (toolWindow != null) {
            toolWindow.activate(() -> IssueDetailsView.getInstance(project).renderIssue(issue));
        }
    }

    public void createToolWindowContent(@NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowPanel, "Issue Details", false);
        toolWindow.getContentManager().addContent(content);
    }

    public void renderIssue(Issue issue) {
        Runnable setModelTask = () -> {
            this.bindIssue(issue);
        };
        Application application = ApplicationManager.getApplication();
        if (application.isDispatchThread()) {
            setModelTask.run();
        }
        else {
            application.invokeLater(setModelTask);
        }
    }

    private void bindIssue(Issue issue) {
        this.detailsTitle.setText(issue.title);
    }
}

