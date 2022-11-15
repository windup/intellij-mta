/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.cli;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.windup.explorer.actions.RunConfigurationAction;
import org.jboss.tools.intellij.windup.explorer.dialog.WindupNotifier;

import java.util.List;

public class RunAnalysisCommandHandler {

    private static final long LONG_TIME = 10 * 1000;

    protected final Project project;

    private final GeneralCommandLine commandLine;

    private ProgressIndicator progressIndicator;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private Process process;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private OSProcessHandler handler;

    private long startTime;

    private ProgressMonitor progressMonitor;

    private WindupConsole console;

    public RunAnalysisCommandHandler(Project project,
                                     String executable,
                                     List<String> params,
                                     WindupConsole console,
                                     Runnable onComplete) {
        this.project = project;
        this.console = console;
        commandLine = new GeneralCommandLine();
        commandLine.setExePath(executable);
        commandLine.addParameters(params);
        this.progressMonitor = new ProgressMonitor(this.createProgressListener(onComplete));
    }

    private ProgressMonitor.IProgressListener createProgressListener(Runnable onCompleteHandler) {
        return new ProgressMonitor.IProgressListener() {
            @Override
            public void report(String message, int percentage, double fraction) {
                if (percentage > 0) {
                    progressIndicator.setFraction(fraction);
                }
                progressIndicator.setText(message);
            }
            @Override
            public void onComplete() {
                onCompleteHandler.run();
            }
        };
    }

    public void runAnalysis() {
        final ProgressManager manager = ProgressManager.getInstance();
        manager.run(new Task.Backgroundable(this.project, "Migration Toolkit for Runtimes", true) {
            public void run(final ProgressIndicator indicator) {
                try {
                    progressIndicator = indicator;
                    startTime = System.currentTimeMillis();
                    process = commandLine.createProcess();
                    handler = new WindupCliProcessHandler(process, commandLine, progressMonitor, progressIndicator, console);
                    console.init(project, handler, commandLine.toString());
                    handler.startNotify();
                    progressIndicator.setText("Starting windup-cli process...");
                    progressIndicator.setIndeterminate(true);
                    progressIndicator.setFraction(0.01);
                    handler.waitFor();
                    logTime();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    RunConfigurationAction.running = false;
                    WindupNotifier.notifyError("Error while running analysis: " + e.getMessage());
                }
            }
        });
    }

    private void logTime() {
        if (startTime > 0) {
            long time = System.currentTimeMillis() - startTime;
            if (time > LONG_TIME) {
                System.out.println(String.format("windup-cli took %s ms. Command parameters: %n%s",
                        time,
                        commandLine.getCommandLineString()));
            } else {
                System.out.println(String.format("windup-cli took %s ms", time));
            }
        } else {
            System.out.println(String.format("windup-cli finished."));
        }
    }
}