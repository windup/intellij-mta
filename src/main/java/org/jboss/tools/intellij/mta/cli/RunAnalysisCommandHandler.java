package org.jboss.tools.intellij.mta.cli;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
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

    protected static final Logger LOG = Logger.getInstance(RunAnalysisCommandHandler.class);
    private static final long LONG_TIME = 10 * 1000;

    protected final Project project;

    private final GeneralCommandLine commandLine;

    private ProgressIndicator progressIndicator;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private Process process;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private OSProcessHandler handler;

    private long startTime;

    private final StringBuilder stderrLine = new StringBuilder();

    public RunAnalysisCommandHandler(Project project, MtaConfiguration configuration) {
        String executable = (String)configuration.getOptions().get("mta-cli");
        List<String> params = MtaCliParamBuilder.buildParams(configuration, executable);
        this.project = project;
        commandLine = new GeneralCommandLine();
        commandLine.setExePath(executable);
        commandLine.addParameters(params);
    }

    public void runAnalysis() {
        final ProgressManager manager = ProgressManager.getInstance();
        manager.run(new Task.Backgroundable(this.project, "mta-cli title", true) {
            public void run(final ProgressIndicator indicator) {
                try {
                    progressIndicator = indicator;
                    startTime = System.currentTimeMillis();
                    process = commandLine.createProcess();
                    handler = new MtaCliProcessHandler(process, commandLine, CharsetToolkit.UTF8_CHARSET);
                    registerProcessListeners();
                    progressIndicator.setText("Analysis in progress");
                    progressIndicator.setText2("testing...");
                    progressIndicator.setIndeterminate(true);
                    handler.waitFor();
                    logTime();
                }
                catch (Exception e) {
                    System.out.println("Error while running analysis: " + e.getMessage());
                    LOG.error("Error while running analysis:");
                    LOG.error(e.getMessage());
                }
            }
        });
    }

    protected void registerProcessListeners() {
        handler.addProcessListener(new ProcessListener() {
            public void startNotified(ProcessEvent event) {
            }
            public void processTerminated(ProcessEvent event) {
                RunAnalysisCommandHandler.this.processTerminated();
            }
            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
            }
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                RunAnalysisCommandHandler.this.onTextAvailable(event.getText(), outputType);
            }
        });
        handler.startNotify();
    }

    private static class MtaCliProcessHandler extends OSProcessHandler {
        private final Charset myCharset;
        public MtaCliProcessHandler(
                Process process,
                GeneralCommandLine commandLine,
                Charset charset) {
            super(process, commandLine.getCommandLineString());
            myCharset = charset;
        }
        @Override
        public Charset getCharset() {
            return myCharset;
        }
    }

    private void logTime() {
        if (startTime > 0) {
            long time = System.currentTimeMillis() - startTime;
            if (!LOG.isDebugEnabled() && time > LONG_TIME) {
                LOG.info(String.format("mta-cli took %s ms. Command parameters: %n%s",
                        time,
                        commandLine.getCommandLineString()));
            } else {
                LOG.debug(String.format("mta-cli took %s ms", time));
            }
        } else {
            LOG.debug(String.format("mta-cli finished."));
        }
    }

    protected void processTerminated() {
        if (stderrLine.length() != 0) {
            onTextAvailable("\n\r", ProcessOutputTypes.STDERR);
        }
    }

    protected void onTextAvailable(final String text, final Key outputType) {
        Iterator<String> lines = LineHandlerHelper.splitText(text).iterator();
        System.out.println("onTextAvailable: " + lines.toString());
    }
}