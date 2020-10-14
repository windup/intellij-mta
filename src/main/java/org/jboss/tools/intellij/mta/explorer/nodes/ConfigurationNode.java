package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.tree.StructureTreeModel;
import org.jboss.tools.intellij.mta.cli.MtaResultsParser;
import org.jboss.tools.intellij.mta.explorer.dialog.SetOutputLocationDialog;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jboss.tools.intellij.mta.services.ModelService;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ConfigurationNode extends MtaExplorerNode<MtaConfiguration> {

    private ModelService modelService;

    public ConfigurationNode(MtaConfiguration configuration, ModelService modelService) {
        super(configuration);
        this.modelService = modelService;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(this.getText());
        presentation.setIcon(IconLoader.getIcon("/icons/configuration/configuration.svg"));
    }

    @Override
    public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<MtaExplorerNode<?>> children = Lists.newArrayList();
        AnalysisResultsSummary summary = this.getValue().getSummary();
        if (summary != null) {
            children.add(new AnalysisResultsNode(summary));
            if (!this.getValue().skippedReports()) {
                children.add(new ReportNode(this.getValue()));
            }
        }
        return children;
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }

    public String getText() {
        return this.getValue().getName();
    }

    @Override
    public void onDoubleClick(Project project, StructureTreeModel treeModel) {
        String currentOutput = (String)this.getValue().getOptions().get("output");
        currentOutput = currentOutput != null ? currentOutput : "";
        SetOutputLocationDialog dialog = new SetOutputLocationDialog(currentOutput);
        if (dialog.showAndGet()) {
            String output = dialog.getOutputLocation();
            this.getValue().getOptions().put("output", output);
            MtaConfiguration.AnalysisResultsSummary summary = new MtaConfiguration.AnalysisResultsSummary(this.modelService);
            summary.outputLocation = output;
            this.getValue().setSummary(summary);
            MtaResultsParser.parseResults(this.getValue(), true);
            this.modelService.saveModel();
            treeModel.invalidate();
        }
    }
}
