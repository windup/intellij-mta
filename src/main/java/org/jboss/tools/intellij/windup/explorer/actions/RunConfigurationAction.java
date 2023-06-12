/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.windup.cli.*;
import org.jboss.tools.intellij.windup.explorer.dialog.WindupNotifier;
import org.jboss.tools.intellij.windup.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.services.ModelService;

import javax.swing.tree.TreePath;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RunConfigurationAction extends StructureTreeAction {

    private WindupConsole console;
    public static boolean running = false;

    public RunConfigurationAction() {
        super(ConfigurationNode.class);
        this.console = new WindupConsole();
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        ConfigurationNode node = (ConfigurationNode)super.adjust(selected);
        WindupConfiguration configuration = node.getValue();
        ModelService modelService = node.getModelService();
        if (this.validateConfiguration(configuration)) {
            String executable = (String)configuration.getOptions().get("cli");
            try {
                String windupHome = new File(executable).getParentFile().getParent();
                List<String> params = WindupCliParamBuilder.buildParams(configuration, windupHome);
                RunAnalysisCommandHandler handler = new RunAnalysisCommandHandler(
                        anActionEvent.getProject(),
                        executable,
                        params,
                        console,
                        () -> this.loadAnalysisResults(configuration, modelService, node.getTreeModel()));
                RunConfigurationAction.running = true;
                handler.runAnalysis();
            }
            catch (Exception e) {
                RunConfigurationAction.running = false;
                System.out.println("Error building CLI params");
                e.printStackTrace();
            }
        }
    }

    private String resolveCliPath(String path) {
        String resolved = path;
        if (path.endsWith("bin/")) {
            resolved += "windup-cli";
        }
        else if (path.endsWith("bin")) {
            resolved += "/windup-cli";
        }
        else if (path.endsWith("-SNAPSHOT") || path.endsWith(".Final")) {
            resolved += "/bin/windup-cli";
        }
        return resolved;
    }

    private void loadAnalysisResults(WindupConfiguration configuration, ModelService modelService, StructureTreeModel treeModel) {
        RunConfigurationAction.running = false;
        WindupConfiguration.AnalysisResultsSummary summary = new WindupConfiguration.AnalysisResultsSummary(modelService);
        summary.outputLocation = (String)configuration.getOptions().get("output");
        configuration.setSummary(summary);
        WindupResultsParser.loadAndPersistIDs(configuration, summary.outputLocation);
        WindupResultsParser.parseResults(configuration);
        modelService.saveModel();
        treeModel.invalidate();
    }

    private boolean validateConfiguration(WindupConfiguration configuration) {
        boolean valid = true;
        String cliLocation = (String)configuration.getOptions().get("cli");
        if (cliLocation == null || "".equals(cliLocation)) {
            valid = false;
            WindupNotifier.notifyError("Path to CLI executable required.");
        }
        else if (cliLocation != null) {
            cliLocation = this.resolveCliPath(cliLocation);
            configuration.getOptions().put("cli", cliLocation);
            if (!new File(cliLocation).exists()) {
                valid = false;
                WindupNotifier.notifyError("Path to CLI executable does not exist.");
            }
            else if (!Files.isExecutable(Paths.get(cliLocation))) {
                valid = false;
                WindupNotifier.notifyError("Path to CLI executable is not executable.");
            }
        }
        List<String> input = (List<String>)configuration.getOptions().get("input");
        if (input == null || input.isEmpty()) {
            WindupNotifier.notifyError("Path to input required.");
            return false;
        }
        for (String anInput : input) {
            if (!Files.exists(Paths.get(anInput))) {
                WindupNotifier.notifyError("Input location does not exist - " + anInput);
                return false;
            }
        }

        String output = (String)configuration.getOptions().get("output");
        if (output == null || "".equals(output)) {
            valid = false;
            WindupNotifier.notifyError("Output path is required.");
        }
        return valid;
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length == 0) return false;
        if (RunConfigurationAction.running) return false;
        return super.isVisible(selected);
    }
}
