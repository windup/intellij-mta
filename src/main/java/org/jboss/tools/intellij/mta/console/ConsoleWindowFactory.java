package org.jboss.tools.intellij.mta.console;

import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jetbrains.annotations.NotNull;

public class ConsoleWindowFactory implements ToolWindowFactory, DumbAware {

    private static final String OUTPUT_WINDOW_CONTENT_ID = "MtaOutputWindowContent";
    public static final String TOOL_WINDOW_ID = "MTA";

    public static Project project;

    public static void updateToolWindowTitle(MtaConfiguration configuration) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(ConsoleWindowFactory.project).getToolWindow(TOOL_WINDOW_ID);
        String name = configuration.getName();
        name = (name == null || "".equals(name)) ? "unknown" : name;
        if (name != null) {
            toolWindow.setTitle("Configuration: " + name);
        }
    }

    public static boolean isToolWindowVisible() {
        ToolWindow toolWindow = ToolWindowManager.getInstance(ConsoleWindowFactory.project).getToolWindow(TOOL_WINDOW_ID);
        return toolWindow.isVisible();
    }

    public static synchronized void outputConsoleMessage(String message, ConsoleViewContentType type) {
        ConsoleManager.getInstance(ConsoleWindowFactory.project).getConsoleWindow(ConsoleWindowFactory.project).print(message, type);
    }

    public static synchronized void outputConsoleHyperlink(String link, HyperlinkInfo linkInfo) {
        ConsoleManager.getInstance(ConsoleWindowFactory.project).getConsoleWindow(ConsoleWindowFactory.project).printHyperlink(link, linkInfo);
    }

    public static synchronized void cleanConsole() {
        ConsoleManager.getInstance(ConsoleWindowFactory.project).getConsoleWindow(ConsoleWindowFactory.project).clear();
    }

    public static synchronized void updateActionsNow() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ConsoleManager.getInstance(ConsoleWindowFactory.project).getLayoutUi(ConsoleWindowFactory.project).updateActionsNow();
            }
        });
    }

    @Override
    public void createToolWindowContent(
            @NotNull final Project project, @NotNull ToolWindow toolWindow) {
        ConsoleWindowFactory.project = project;
        toolWindow.setAvailable(true, null);
        toolWindow.setToHideOnEmptyContent(true);

        RunnerLayoutUi runnerLayoutUi = ConsoleManager.getInstance(project).getLayoutUi(project);
        Content consoleContent = createConsoleContent(runnerLayoutUi, project);

        runnerLayoutUi.addContent(consoleContent, 0, PlaceInGrid.center, false);
        runnerLayoutUi.getOptions().setLeftToolbar(
                getLeftToolbarActions(), ActionPlaces.UNKNOWN);

        runnerLayoutUi.updateActionsNow();

        final ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(
                runnerLayoutUi.getComponent(), "", true);
        contentManager.addContent(content);
    }

    private Content createConsoleContent(RunnerLayoutUi layoutUi) {
        ConsoleView consoleView = ConsoleManager.getInstance(ConsoleWindowFactory.project).getConsoleWindow(ConsoleWindowFactory.project);
        Content consoleWindowContent = layoutUi.createContent(
                OUTPUT_WINDOW_CONTENT_ID, consoleView.getComponent(), "Output Logs", null, null);
        consoleWindowContent.setCloseable(false);
        return consoleWindowContent;
    }

    public ActionGroup getLeftToolbarActions() {
        ActionManager actionManager = ActionManager.getInstance();

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(actionManager.getAction("mta.ChooseConfiguration"));
        group.addSeparator();
        group.add(actionManager.getAction("mta.Install"));
        group.add(actionManager.getAction("mta.Run"));
        group.add(actionManager.getAction("mta.Kill"));
        return group;
    }
}
