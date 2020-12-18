package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.cli.MtaCliParamBuilder;
import org.jboss.tools.intellij.mta.cli.MtaCliRunner;
import org.jboss.tools.intellij.mta.cli.MtaResultsParser;
import org.jboss.tools.intellij.mta.cli.RunAnalysisCommandHandler;
import org.jboss.tools.intellij.mta.explorer.dialog.MtaNotifier;
import org.jboss.tools.intellij.mta.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.services.ModelService;

import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class RunConfigurationAction extends StructureTreeAction {

    public RunConfigurationAction() {
        super(ConfigurationNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        ConfigurationNode node = (ConfigurationNode)super.adjust(selected);
        MtaConfiguration configuration = node.getValue();
        ModelService modelService = node.getModelService();
        if (this.validateConfiguration(configuration)) {
            String executable = (String)configuration.getOptions().get("mta-cli");
            try {
                String windupHome = new File(executable).getParentFile().getParent();
                List<String> params = MtaCliParamBuilder.buildParams(configuration, windupHome);
                RunAnalysisCommandHandler handler = new RunAnalysisCommandHandler(
                        anActionEvent.getProject(),
                        configuration,
                        executable,
                        params,
                        () -> this.loadAnalysisResults(configuration, modelService, node.getTreeModel()));
                handler.runAnalysis();
            }
            catch (Exception e) {
                System.out.println("Error building mta-cli params");
                e.printStackTrace();
            }
        }
    }

    private void loadAnalysisResults(MtaConfiguration configuration, ModelService modelService, StructureTreeModel treeModel) {
        MtaConfiguration.AnalysisResultsSummary summary = new MtaConfiguration.AnalysisResultsSummary(modelService);
        summary.outputLocation = (String)configuration.getOptions().get("output");
        configuration.setSummary(summary);
        MtaResultsParser.parseResults(configuration,true);
        modelService.saveModel();
        treeModel.invalidate();
    }

    private boolean validateConfiguration(MtaConfiguration configuration) {
        boolean valid = true;
        String cliLocation = (String)configuration.getOptions().get("mta-cli");
        if (cliLocation == null || "".equals(cliLocation)) {
            valid = false;
            MtaNotifier.notifyError("Path to mta-cli executable required.");
        }
        else if (cliLocation != null) {
            if (!new File(cliLocation).exists()) {
                valid = false;
                MtaNotifier.notifyError("Path to mta-cli executable does not exist.");
            }
            else if (!Files.isExecutable(Paths.get(cliLocation))) {
                valid = false;
                MtaNotifier.notifyError("Path to mta-cli executable is not executable.");
            }
        }
        if (!configuration.getOptions().containsKey("input")) {
            valid = false;
            MtaNotifier.notifyError("Path to input required.");
        }
        String output = (String)configuration.getOptions().get("output");
        if (output == null || "".equals(output)) {
            valid = false;
            MtaNotifier.notifyError("Output path is required.");
        }
        return valid;
    }

    @Override
    public boolean isVisible(Object[] selected) {
        if (selected.length == 0) return false;
        return super.isVisible(selected);
    }
}
