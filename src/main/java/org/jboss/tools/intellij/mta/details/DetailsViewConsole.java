package org.jboss.tools.intellij.mta.details;

import com.intellij.execution.filters.BrowserHyperlinkInfo;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import static org.jboss.tools.intellij.mta.model.MtaConfiguration.*;

public class DetailsViewConsole {

    private Project project;
    private ConsoleView console;

    public DetailsViewConsole(Project project) {
        this.project = project;
    }

    public void open(Issue issue) {
        Runnable r = () -> {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            String name = "Issue Details (MTA)";
            ToolWindow window = manager.getToolWindow(name);
            if (window == null) {
                TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
                TextConsoleBuilder builder = factory.createBuilder(project);
                console = builder.getConsole();
                window = manager.registerToolWindow(name, console.getComponent(), ToolWindowAnchor.BOTTOM);
            }
            console.clear();
            bindValues(issue);
            window.show();
        };
        ApplicationManager.getApplication().invokeAndWait(r);
    }

    private void bindValues(Issue issue) {
        ConsoleViewContentType labelType = ConsoleViewContentType.LOG_INFO_OUTPUT;
        ConsoleViewContentType valueType = ConsoleViewContentType.SYSTEM_OUTPUT;

        // Title
        console.print("\nTitle\n", labelType);
        console.print(issue.title, valueType);
        console.print("\n\n", valueType);

        // Report
        console.print("Report\n", labelType);
        console.printHyperlink("Open Report", new BrowserHyperlinkInfo(issue.report));
        console.print("\n\n", valueType);

        // Message or Description
        boolean isHint = issue instanceof Hint;
        String msgLabel = isHint ? "Message" : "Description";
        console.print(msgLabel, labelType);

        String message = "---";
        if (isHint) {
            String hint = ((Hint)issue).hint;
            if (hint != null && !("".equals(hint))) {
                message = hint;
            }
        }
        else {
            String description = ((Classification)issue).description;
            if (description != null && !("".equals(description))) {
                message = description;
            }
        }
        console.print("\n"+message, valueType);
        console.print("\n\n", valueType);

        // Category
        console.print("Category\n", labelType);
        console.print(issue.category, valueType);
        console.print("\n\n", valueType);

        // Effort
        console.print("Effort\n", labelType);
        console.print(issue.effort, valueType);
        console.print("\n\n", valueType);

        // Rule
        console.print("Rule ID\n", labelType);
        console.print(issue.ruleId, valueType);
        console.print("\n\n", valueType);

        // Source Snippet
        String snippet = "---";
        if (isHint) {
            String value = ((Hint)issue).sourceSnippet;
            if (value != null && !("".equals(value))) {
                snippet = value;
            }
        }
        console.print("Source Snippet\n", labelType);
        console.print(snippet, valueType);
        console.print("\n\n", valueType);

        // More Information
        console.print("More Information\n", labelType);
        if (issue.links.isEmpty()) {
            console.print("---", valueType);
        }
        else {
            for (Link link : issue.links) {
                console.printHyperlink(link.title, new BrowserHyperlinkInfo(link.url));
            }
        }
    }
}
