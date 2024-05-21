/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.cli;

import com.google.gson.JsonObject;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.jboss.tools.intellij.windup.explorer.actions.RunConfigurationAction;
import org.jboss.tools.intellij.windup.explorer.dialog.WindupNotifier;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;


import static org.jboss.tools.intellij.windup.cli.ProgressMonitor.PROGRESS;

public class WindupCliProcessHandler extends OSProcessHandler {

    private ProgressMonitor progressMonitor;
    private ProgressIndicator progressIndicator;
    private WindupConsole console;
    private boolean isCancelled = false;

  //  int i = 0;

    public WindupCliProcessHandler(
            Process process,
            GeneralCommandLine commandLine,
            ProgressMonitor progressMonitor,
            ProgressIndicator progressIndicator,
            WindupConsole console
    ) {
        super(process, commandLine.getCommandLineString());
        this.progressMonitor = progressMonitor;
        this.progressIndicator = progressIndicator;
        this.console = console;
    }

    @Override
    public final void notifyTextAvailable(@NotNull String text, @NotNull final Key outputType) {

//     if(i <= 10){
//         System.out.println ("This is output from Kantra -----------------------> " + text);
//     }
//     i++;
        if (progressIndicator.isCanceled()) {
            destroyProcess();
            RunConfigurationAction.running = false;
            if (!isCancelled){
                isCancelled = true;
                console.print("analysis cancelled", ConsoleViewContentType.LOG_INFO_OUTPUT);
            }
            else {
                System.out.println("previously cancelled.");
            }
            return;
        }
        if (text.contains("rules")) {
            progressIndicator.setText("Preparing analysis configuration...");
        }
        else if (text.contains("Reading tags definitions")) {
            progressIndicator.setText("Reading tags definitions...");
        }
        else if (text.contains("Finished provider load")) {
            progressIndicator.setText("Loading transformation paths...");
        }
        else if (text.contains("running source code analysis")) {
            progressIndicator.setText("Running source code analysis...");
            progressIndicator.setFraction(0.10);
        }
        else if (text.contains("generating analysis log in file")) {
            int progress = calculateWorkDonePercentage(text);
            progressIndicator.setText("Generating Analysis logs...");
           // progressIndicator.setText("Generating Analysis logs (" + progress + "%)");
            progressIndicator.setFraction(0.35);
        }
        else if (text.contains("running dependency analysis")) {
            progressIndicator.setText("Running Dependency Analysis...");
            progressIndicator.setFraction(0.65);
        }
        else if (text.contains("generating dependency log in file")) {
            progressIndicator.setText("Generating Dependency logs...");
            progressIndicator.setFraction(0.75);
        }
        else if (text.contains("generating static report")) {
            progressIndicator.setText("Generating static report...");
            progressIndicator.setFraction(0.95);
        }
        else if (text.contains("Static report created.")) {
            System.out.println(text + "---------------------------------------: detected ");
            ProgressMonitor.ProgressMessage msg = new ProgressMonitor.ProgressMessage("complete", "", 20, "");
            progressMonitor.handleMessage(msg);
            progressIndicator.setFraction(1);
            WindupNotifier.notifyInformation("Analysis is completed.");
            console.print("Analysis completed successfully. \n", ConsoleViewContentType.LOG_INFO_OUTPUT);

        }
        else if (text.contains("Error")) {
            System.out.println(text + "---------------------------------------: detected ");
            ProgressMonitor.ProgressMessage msg = new ProgressMonitor.ProgressMessage("complete", "", 20, "");
            progressMonitor.handleMessage(msg);
            progressIndicator.setFraction(1);
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

    public static int calculateWorkDonePercentage(String log) {
        // Regex pattern to extract the relevant numbers from the log string
        String pattern = "failed=(\\d+) matched=(\\d+) total=(\\d+) unmatched=(\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(log);

        if (m.find()) {
            // Parse numbers from the log string
            int failed = Integer.parseInt(m.group(1));
            int matched = Integer.parseInt(m.group(2));
            int total = Integer.parseInt(m.group(3));
            int unmatched = Integer.parseInt(m.group(4));

            // Calculate the work done percentage
            double workDone = (double) (failed + matched + unmatched) / total;
            return (int) Math.round(workDone * 100);
        } else {
            // If the pattern does not match, return an error code or throw an exception
            return -1; // Or you could throw an exception
        }
    }
}
