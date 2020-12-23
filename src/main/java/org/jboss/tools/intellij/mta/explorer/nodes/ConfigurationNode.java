package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.tree.StructureTreeModel;
import org.apache.maven.model.Model;
import org.jboss.tools.intellij.mta.cli.MtaResultsParser;
import org.jboss.tools.intellij.mta.editor.ConfigurationFile;
import org.jboss.tools.intellij.mta.editor.server.VertxService;
import org.jboss.tools.intellij.mta.explorer.dialog.SetOutputLocationDialog;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jboss.tools.intellij.mta.services.ModelService;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ConfigurationNode extends MtaExplorerNode<MtaConfiguration> {

    private ModelService modelService;
    private VertxService vertxService;
    private StructureTreeModel treeModel;

    public ConfigurationNode(MtaConfiguration configuration, ModelService modelService, VertxService vertxService, StructureTreeModel treeModel) {
        super(configuration);
        this.modelService = modelService;
        this.vertxService = vertxService;
        this.treeModel = treeModel;
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
//        String currentOutput = (String)this.getValue().getOptions().get("output");
//        currentOutput = currentOutput != null ? currentOutput : "";
//        SetOutputLocationDialog dialog = new SetOutputLocationDialog(currentOutput);
//        if (dialog.showAndGet()) {
//            String output = dialog.getOutputLocation();
//            this.getValue().getOptions().put("output", output);
//            MtaConfiguration.AnalysisResultsSummary summary = new MtaConfiguration.AnalysisResultsSummary(this.modelService);
//            summary.outputLocation = output;
//            this.getValue().setSummary(summary);
//            MtaResultsParser.parseResults(this.getValue(),true);
//            this.modelService.saveModel();
//            treeModel.invalidate();
//        }
        ConfigurationNode.openConfigurationEditor(this.getValue(), modelService, vertxService);
    }

    public static void openConfigurationEditor(MtaConfiguration configuration, ModelService modelService, VertxService vertxService) {
        try {
            Project project = modelService.getProject();
            FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project,
                    new ConfigurationFile(configuration, vertxService, modelService)), true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ModelService getModelService() {
        return this.modelService;
    }

    public StructureTreeModel getTreeModel() {
        return this.treeModel;
    }
}
