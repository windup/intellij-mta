package org.jboss.tools.intellij.mta.cli;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vcs.LineHandlerHelper;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

public class RunAnalysisCommandHandler {

    private static final long LONG_TIME = 10 * 1000;
    private static final String PROGRESS = ":progress:";

    protected final Project project;

    private final GeneralCommandLine commandLine;

    private ProgressIndicator progressIndicator;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private Process process;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private OSProcessHandler handler;

    private long startTime;

    private final StringBuilder stderrLine = new StringBuilder();

    private MtaConfiguration configuration;
    private ProgressMonitor progressMonitor;

    private JsonParser jsonParser = new JsonParser();

    public RunAnalysisCommandHandler(Project project,
                                     MtaConfiguration configuration,
                                     String executable,
                                     List<String> params) {
        this.configuration = configuration;
        this.project = project;
        commandLine = new GeneralCommandLine();
        commandLine.setExePath(executable);
        commandLine.addParameters(params);
        this.progressMonitor = new ProgressMonitor(this.createProgressListener());
    }

    private ProgressMonitor.IProgressListener createProgressListener() {
        return new ProgressMonitor.IProgressListener() {
            int i = 0;

            @Override
            public void report(String message, int percentage) {
                if (percentage > 0) {
                    progressIndicator.setFraction(percentage);
                }
                progressIndicator.setText(message);
            }
            @Override
            public void onComplete() {
            }
        };
    }

    public void runAnalysis() {
        final ProgressManager manager = ProgressManager.getInstance();
        manager.run(new Task.Backgroundable(this.project, "Migration Toolkit for Applications", true) {
            public void run(final ProgressIndicator indicator) {
                try {
                    progressIndicator = indicator;
                    startTime = System.currentTimeMillis();
                    process = commandLine.createProcess();
                    handler = new MtaCliProcessHandler(process, commandLine);
                    registerProcessListeners();
                    progressIndicator.setText("Analysis in progress...");
                    progressIndicator.setIndeterminate(true);
                    handler.waitFor();
                    logTime();
                }
                catch (Exception e) {
                    System.out.println("Error while running analysis: " + e.getMessage());
                }
            }
        });
    }

    protected void registerProcessListeners() {
        handler.addProcessListener(new ProcessListener() {
            public void startNotified(ProcessEvent event) {
                System.out.println("mta-cli process started.");
            }
            public void processTerminated(ProcessEvent event) {
                System.out.println("mta-cli processTerminated.");
                RunAnalysisCommandHandler.this.processTerminated();
            }
            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
                System.out.println("mta-cli processWillTerminate.");
            }
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                RunAnalysisCommandHandler.this.onTextAvailable(event.getText(), outputType);
            }
        });
        handler.startNotify();
    }

    private static class MtaCliProcessHandler extends OSProcessHandler {
        public MtaCliProcessHandler(
                Process process,
                GeneralCommandLine commandLine) {
            super(process, commandLine.getCommandLineString());
        }
        @Override
        public Charset getCharset() {
            return CharsetToolkit.UTF8_CHARSET;
        }
    }

    private void logTime() {
        if (startTime > 0) {
            long time = System.currentTimeMillis() - startTime;
            if (time > LONG_TIME) {
                System.out.println(String.format("mta-cli took %s ms. Command parameters: %n%s",
                        time,
                        commandLine.getCommandLineString()));
            } else {
                System.out.println(String.format("mta-cli took %s ms", time));
            }
        } else {
            System.out.println(String.format("mta-cli finished."));
        }
    }

    protected void processTerminated() {
        if (stderrLine.length() != 0) {
            onTextAvailable("\n\r", ProcessOutputTypes.STDERR);
        }
    }

    protected void onTextAvailable(String text, final Key outputType) {
//        System.out.println("mta-cli onTextAvailable: " + text);
//        Iterator<String> lines = LineHandlerHelper.splitText(text).iterator();
//        while (lines.hasNext()) {
//            System.out.println("onTextAvailable: " + lines.next());
//        }

        System.out.println("Message from mta-cli: " + text);
        if (text.contains(PROGRESS)) {
            text = text.replace(PROGRESS, "").trim();
            if (text.contains("{\"op\":\"") && !text.contains("\"op\":\"logMessage\"")) {
                try {
                    progressMonitor.handleMessage(ProgressMonitor.parse(jsonParser, text));
                }
                catch (JsonSyntaxException e) {
                    System.out.println("Error parsing mta-cli output: " + e.getMessage());
                }
            }
        }
    }
}