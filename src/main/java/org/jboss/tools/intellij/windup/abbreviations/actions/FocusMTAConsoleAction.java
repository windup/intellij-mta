package org.jboss.tools.intellij.windup.abbreviations.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import java.awt.*;
import java.awt.event.InputEvent;
import com.intellij.openapi.diagnostic.Logger;

public class FocusMTAConsoleAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(FocusMTAConsoleAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Console (MTA)");
            if (toolWindow != null) {
                toolWindow.activate(() -> {
                    Rectangle bounds = toolWindow.getComponent().getBounds();
                    Point locationOnScreen = toolWindow.getComponent().getLocationOnScreen();
                    int yPosition = locationOnScreen.y + bounds.height / 2;
                    int xPosition = locationOnScreen.x + bounds.width / 2;
                    try {
                        Robot robot = new Robot();
                        robot.mouseMove(xPosition, yPosition);
                        robot.delay(150);
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    } catch (AWTException ex) {
                        LOG.error("Failed to move mouse and click on Console (MTA)", ex);
                    }
                });
            }
        }
    }
}
