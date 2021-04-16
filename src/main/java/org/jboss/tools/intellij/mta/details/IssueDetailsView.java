package org.jboss.tools.intellij.mta.details;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javax.swing.*;

import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * The contents Issue Details tool window.
 */
public class IssueDetailsView {

    private final Project project;

    private JPanel myToolWindowPanel;

    @NotNull
    public static IssueDetailsView getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, IssueDetailsView.class);
    }

    @VisibleForTesting
    public IssueDetailsView(@NotNull Project project) {
        this.project = project;
    }

    public void createToolWindowContent(@NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowPanel, "Issue Details", false);
        toolWindow.getContentManager().addContent(content);
    }

    public void updateContents(MtaConfiguration.Issue issue) {
//        Runnable task = () -> ...;
//        Application application = ApplicationManager.getApplication();
//        if (application.isDispatchThread()) {
//            task.run();
//        }
//        else {
//            application.invokeLater(task);
//        }
    }
}

