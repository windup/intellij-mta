package org.jboss.tools.intellij.mta.details;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import static org.jboss.tools.intellij.mta.model.MtaConfiguration.*;

import javax.swing.*;

public class DetailsView {

    private Project project;
    private ToolWindow window;

    private JLabel messageLabel;
    private JLabel title;
    private JLabel effort;
    private JLabel message;
    private JLabel category;
    private JLabel rule;
    private JPanel moreInfoPanel;
    private JLabel sourceSnippet;

    public DetailsView (Project project) {
        this.project = project;
    }

    public void open(Issue issue) {
        Runnable r = () -> {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            String name = "Issue Details";
            this.window = manager.getToolWindow(name);
            if (this.window == null) {
                this.window = manager.registerToolWindow(name, createComponent(), ToolWindowAnchor.BOTTOM);
            }
            bindValues(issue);
            window.show();
        };
        ApplicationManager.getApplication().invokeAndWait(r);
    }

    private JComponent createComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        // Title
        JLabel label = new JLabel("Title ");
        panel.add(label);
        this.title = new JLabel("");
        panel.add(this.title);

        // Message or Description
        this.messageLabel = new JLabel("");
        panel.add(this.messageLabel);
        this.message = new JLabel("");
        panel.add(this.message);

        // Effort
        label = new JLabel("Level of Effort ");
        panel.add(label);
        this.effort = new JLabel("");
        panel.add(this.effort);

        // Category
        label = new JLabel("Category ID ");
        panel.add(label);
        this.category = new JLabel("");
        panel.add(this.category);

        // Rule
        label = new JLabel("Rule ID ");
        panel.add(label);
        this.rule = new JLabel("");
        panel.add(this.rule);

        // Source Snippet
        label = new JLabel("Source Snippet ");
        panel.add(label);
        this.sourceSnippet = new JLabel();
        panel.add(this.sourceSnippet);

        // More Info
        label = new JLabel("More Information ");
        panel.add(label);
        this.moreInfoPanel = new JPanel();
        panel.add(this.moreInfoPanel);

        return panel;
    }

    private void bindValues(Issue issue) {
        // Title
        this.title.setText(issue.title);

        // Message or Description
        boolean isHint = issue instanceof Hint;
        this.messageLabel.setText(isHint ? "Message" : "Description");

        String message = "---";
        if (isHint) {
            String hint = ((Hint)issue).hint;
            if (hint != null && !("".equals(hint))) {
                message = hint;
            }
            this.message.setText(hint);
        }
        else {
            String description = ((Classification)issue).description;
            if (description != null && !("".equals(description))) {
                message = description;
            }
            this.message.setText(description);
        }

        // Category
        this.category.setText(issue.category);

        // Effort
        this.effort.setText(issue.effort);

        // Rule
        this.rule.setText(issue.ruleId);

        // More Information
        this.moreInfoPanel.removeAll();

        // Source Snippet
        if (isHint) {
            String snippet = "---";
            String value = ((Hint)issue).sourceSnippet;
            if (value != null && !("".equals(value))) {
                snippet = value;
            }
            this.sourceSnippet.setText(snippet);
        }
    }
}
