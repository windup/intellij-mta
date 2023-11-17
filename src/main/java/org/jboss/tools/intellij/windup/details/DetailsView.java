/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.details;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;

import static org.jboss.tools.intellij.windup.model.WindupConfiguration.*;

import javax.swing.*;
import java.awt.*;

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
        Font font = new Font("Courier", Font.BOLD,16);

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        // Title
        JLabel label = new JLabel("Title ");
        label.setFont(font);
        panel.add(label);
        this.title = new JLabel("");
        panel.add(this.title);

        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        // Message or Description
        this.messageLabel = new JLabel("");
        this.messageLabel.setFont(font);
        panel.add(this.messageLabel);
        this.message = new JLabel("");
        panel.add(this.message);

        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        // Effort
        label = new JLabel("Level of Effort ");
        label.add(Box.createVerticalGlue());
        label.setFont(font);
        panel.add(label);
        this.effort = new JLabel("");
        panel.add(this.effort);

        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        // Category
        label = new JLabel("Category ID ");
        label.setFont(font);
        panel.add(label);
        this.category = new JLabel("");
        panel.add(this.category);

        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        // Rule
        label = new JLabel("Rule ID ");
        label.setFont(font);
        panel.add(label);
        this.rule = new JLabel("");
        panel.add(this.rule);

        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        // Source Snippet
        label = new JLabel("Source Snippet ");
        label.setFont(font);
        panel.add(label);
        this.sourceSnippet = new JLabel();
        panel.add(this.sourceSnippet);

        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        // More Info
        label = new JLabel("More Information ");
        label.setFont(font);
        panel.add(label);
        this.moreInfoPanel = new JPanel();
        panel.add(this.moreInfoPanel);

        JBScrollPane pane = new JBScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return pane;
    }

    private void bindValues(Issue issue) {
        // Title
        this.title.setText(issue.title);

        // Message or Description
        boolean isHint = issue instanceof Incident;
        this.messageLabel.setText(isHint ? "Message" : "Description");

        String message = "---";
        if (isHint) {
            String hint = ((Incident)issue).getMessage();
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
            String value = ((Incident)issue).getCodeSnip();
            if (value != null && !("".equals(value))) {
                snippet = value;
            }
            this.sourceSnippet.setText(snippet);
        }
    }
}
