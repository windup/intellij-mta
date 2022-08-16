/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.console;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class ConsoleManager {

    private ConsoleView outputConsole;
    private RunnerLayoutUi runnerLayoutUi;

    public static synchronized ConsoleManager getInstance(Project project) {
        return ServiceManager.getService(project, ConsoleManager.class);
    }

    public ConsoleView getConsoleWindow(Project project) {
        if (outputConsole == null) {
            outputConsole = new ConsoleViewImpl(project, false);
        }
        return outputConsole;
    }

    public RunnerLayoutUi getLayoutUi(Project project) {
        if (runnerLayoutUi == null) {
            runnerLayoutUi = RunnerLayoutUi.Factory.getInstance(project).create(
                    "windup", "windup", "windup", project);
        }
        return runnerLayoutUi;
    }
}