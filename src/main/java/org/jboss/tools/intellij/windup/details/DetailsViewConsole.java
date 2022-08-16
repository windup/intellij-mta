/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.details;

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

import java.util.Arrays;

import static org.jboss.tools.intellij.windup.model.WindupConfiguration.*;

public class DetailsViewConsole {

    private Project project;
    private ConsoleView console;
    private Issue activeIssue = null;

    public DetailsViewConsole(Project project) {
        this.project = project;
    }

    public void clear() {
        this.console.clear();
    }

    public void open(Issue issue) {
        Runnable r = () -> {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            String name = "Issue Details";
            ToolWindow window = manager.getToolWindow(name);
            if (window == null) {
                TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
                TextConsoleBuilder builder = factory.createBuilder(project);
                console = builder.getConsole();
                window = manager.registerToolWindow(name, console.getComponent(), ToolWindowAnchor.BOTTOM);
            }
            
            if (this.activeIssue != null && this.activeIssue.id.equals(issue.id)) {
                window.show();
                return;
            }
            console.clear();
            this.activeIssue = issue;
            bindValues(issue);
            console.scrollTo(0);
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
        if (issue.report != null) {
            console.printHyperlink("Open Report", new BrowserHyperlinkInfo(issue.report));
        }
        else {
            console.print("Not available", valueType);
        }

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
        String[] lines = message.split("\\. ");
        Arrays.stream(lines).forEach(line -> console.print("\n"+line, valueType));
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
                console.print(link.title + " - ", valueType);
                console.printHyperlink(link.url, new BrowserHyperlinkInfo(link.url));
                console.print("\n", valueType);
            }
        }
    }
}
