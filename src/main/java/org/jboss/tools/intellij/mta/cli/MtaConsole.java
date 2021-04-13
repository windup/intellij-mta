package org.jboss.tools.intellij.mta.cli;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.eclipse.core.runtime.Path;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MtaConsole {

    private ConsoleView console;

    public void init(Project project, OSProcessHandler handler, String commandLine) {
        Runnable r = () -> {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            String name = "Migration Toolkit for Applications (MTA) Console";
            ToolWindow window = manager.getToolWindow(name);

            if (window == null) {
                TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
                TextConsoleBuilder builder = factory.createBuilder(project);
                console = builder.getConsole();
                window = manager.registerToolWindow(name, console.getComponent(), ToolWindowAnchor.BOTTOM);
            }

            console.clear();
            console.attachToProcess(handler);
            window.show();
        };

        ApplicationManager.getApplication().invokeAndWait(r);
    }

    public void print(String msg, ConsoleViewContentType type) {
        console.print(msg, type);
    }

    private static final class MtaFilter implements Filter {
        @Override
        public Result applyFilter(@NotNull String line, int entireLength) {
            return null;
        }
    }
}
