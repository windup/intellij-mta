package org.jboss.tools.intellij.mta.cli;

import com.google.gson.JsonObject;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.jboss.tools.intellij.mta.cli.ProgressMonitor.PROGRESS;

public class MtaCliProcessHandler extends OSProcessHandler {

    private ProgressMonitor progressMonitor;
    private ProgressIndicator progressIndicator;
    private MtaConsole console;
    private boolean isCancelled = false;

    public MtaCliProcessHandler(
            Process process,
            GeneralCommandLine commandLine,
            ProgressMonitor progressMonitor,
            ProgressIndicator progressIndicator,
            MtaConsole console
    ) {
        super(process, commandLine.getCommandLineString());
        this.progressMonitor = progressMonitor;
        this.progressIndicator = progressIndicator;
        this.console = console;
    }

    @Override
    public final void notifyTextAvailable(@NotNull String text, @NotNull final Key outputType) {
        if (progressIndicator.isCanceled()) {
            destroyProcess();
            if (!isCancelled){
                isCancelled = true;
                console.print("analysis cancelled", ConsoleViewContentType.LOG_INFO_OUTPUT);
            }
            else {
                System.out.println("previously cancelled.");
            }
            return;
        }
        if (text.contains("userRulesDirectory")) {
            progressIndicator.setText("Preparing analysis configuration...");
        }
        else if (text.contains("Reading tags definitions")) {
            progressIndicator.setText("Reading tags definitions...");
        }
        else if (text.contains("Finished provider load")) {
            progressIndicator.setText("Loading transformation paths...");
        }
        else if (text.contains(PROGRESS)) {
            JsonObject json = ProgressMonitor.parseProgressMessage(text);
            if (json != null) {
                progressMonitor.handleMessage(ProgressMonitor.parse(json));
            }
        }

        JsonObject json = ProgressMonitor.parseOperationMessage(text);
        if (json != null) {
            ProgressMonitor.ProgressMessage msg = ProgressMonitor.parse(json);
            text = msg.value + System.lineSeparator();
        }
        if (text.startsWith("1")) {
            text = "";
        }

        if (text.contains("{\"op\":\"")) {
            String replaced = text.replace(PROGRESS, "").trim();
            text = replaced.replace("{\"op\":\"logMessage\",\"value\":\"", "");
        }

        super.notifyTextAvailable(text, outputType);
    }

    @Override
    public Charset getCharset() {
        return CharsetToolkit.UTF8_CHARSET;
    }
}
