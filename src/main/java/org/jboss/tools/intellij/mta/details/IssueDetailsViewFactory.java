package org.jboss.tools.intellij.mta.details;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jboss.tools.intellij.mta.explorer.MtaToolWindow;
import org.jboss.tools.intellij.mta.services.ModelService;
import org.jetbrains.annotations.NotNull;

public class IssueDetailsViewFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(Project project, @NotNull ToolWindow toolWindow) {
    }
}
