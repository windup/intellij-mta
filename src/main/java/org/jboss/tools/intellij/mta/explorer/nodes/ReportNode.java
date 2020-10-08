package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class ReportNode extends MtaExplorerNode<MtaConfiguration> {

    public ReportNode(MtaConfiguration configuration) {
        super(configuration);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        List<AnalysisResultsNode> children = Lists.newArrayList();
        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText("Report");
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }

    @Override
    public void onClick(Project project) {
        String report = this.getValue().getReportLocation();
        if (report != null) {
            File file = new File(report);
            if (file.exists()) {
                try {
                    BrowserUtil.browse(file.toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("" +
                    "Error - Cannot open report location for output " +
                    this.getValue().getOptions().get("output"));
        }
    }
}
