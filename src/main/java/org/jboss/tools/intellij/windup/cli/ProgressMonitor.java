/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.cli;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ProgressMonitor {

    public static final String PROGRESS = ":progress:";

    private static JsonParser jsonParser = new JsonParser();

    public interface IProgressListener {
        void report(String message, int percentage, double fraction);
        void onComplete();
    }

    public static class ProgressMessage {
        String op = "";
        String task = "";
        int totalWork = 0;
        String value = "";
    }

    private IProgressListener progressListener;

    private boolean started = false;
    private double preWork = 0;
    private String title = "";
    private double totalWork = 0;
    private boolean finalizing = false;
    private boolean done = false;

    public ProgressMonitor(IProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void handleMessage(ProgressMessage msg) {
        this.delegateMessage(msg);
    }

    private void delegateMessage(ProgressMessage msg) {
        if (msg.op.equals("beginTask")) {
            String task = msg.task;
            int work = msg.totalWork;
            this.beginTask(task, work);
            return;
        }

        if (msg.op.equals("complete")) {
            this.finish();
            return;
        }

        String value = msg.value;
        switch (msg.op) {
            case "logMessage":
                this.logMessage(value);
                break;
            case "done":
                this.done();
                break;
            case "setTaskName":
                this.setTaskName(value);
                break;
            case "subTask":
                this.subTask(value);
                break;
            case "worked":
                this.worked(Integer.valueOf(value));
                break;
        }

        if (!this.started) {
            this.started = true;
            this.report("Starting analysis...", -1);
        }
    }

    public void logMessage(String message) {
    }

    public void beginTask(String task, double total) {
        this.title = "Analysis in progress";
        this.totalWork = total;
        this.setTitle(this.title, -1);
    }

    public void done() {
        this.done = true;
        this.report("Finalizing...", -1);
    }

    public boolean isDone() {
        return this.done;
    }

    public void setTaskName(String task) {
        this.report(task, -1);
    }

    public void subTask(String name) {
    }

    public void worked(double worked) {
        this.preWork += worked;
        this.setTitle(this.computeTitle(), this.getPercentageDone());
    }

    private int getPercentageDone() {
        return (int)Math.floor(Math.min((this.preWork * 100 / this.totalWork), 100));
    }

    private String computeTitle() {
        int done = this.getPercentageDone();
        String label = this.title;
        if (done > 0) {
            label += " (" + done + " % done)";
        }
        return label;
    }

    private void setTitle(String value, int percentage) {
        this.report(value, percentage);
    }

    public void report(String msg, int percentage) {
        if (!this.done && !this.finalizing) {
            if (this.getPercentageDone() == 99) {
                this.finalizing = true;
                msg = "Preparing analysis results...";
            }
            this.progressListener.report(msg, percentage, (this.preWork / this.totalWork));
        }
        else {
            System.out.println("progress done or cancelled, cannot report: " + msg);
        }
    }

    private void finish() {
        System.out.println("analysis complete...");
        this.finalizing = true;
        this.progressListener.onComplete();
    }

    public static JsonObject parseProgressMessage(String text) {
        if (text.contains(PROGRESS)) {
            String replaced = text.replace(PROGRESS, "").trim();
            boolean isOp = replaced.contains("{\"op\":\"");
            if (isOp && !replaced.contains("\"op\":\"logMessage\"")) {
                try {
                    return jsonParser.parse(replaced).getAsJsonObject();
                }
                catch (JsonSyntaxException e) {
                    System.out.println("Error parsing CLI output: " + e.getMessage());
                }
            }
        }
        return null;
    }

    public static ProgressMessage parse(JsonObject json) throws JsonSyntaxException {
        ProgressMonitor.ProgressMessage msg = new ProgressMonitor.ProgressMessage();
        msg.op = json.get("op").getAsString();
        if (json.has("value")) {
            msg.value = json.get("value").getAsString();
        }
        if (json.has("task")) {
            msg.task = json.get("task").getAsString();
        }
        if (json.has("totalWork")) {
            msg.totalWork = json.get("totalWork").getAsInt();
        }
        return msg;
    }

    public static JsonObject parseOperationMessage(String text) {
        String replaced = text.replace(PROGRESS, "").trim();
        boolean isOp = replaced.contains("{\"op\":\"");
        if (isOp) {
            try {
                return jsonParser.parse(replaced).getAsJsonObject();
            }
            catch (JsonSyntaxException e) {
                System.out.println("Error parsing CLI output: " + e.getMessage());
            }
        }
        return null;
    }
}
