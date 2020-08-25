package org.jboss.tools.intellij.mta.explorer.nodes;

import com.google.common.collect.Lists;
import com.intellij.ide.projectView.PresentationData;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConfigurationNode extends MtaExplorerNode<MtaConfiguration> {

    public ConfigurationNode(MtaConfiguration configuration) {
        super(configuration);
    }

    @NotNull
    @Override
    public Collection<AnalysisResultsNode> getChildren() {
        return Lists.newArrayList();
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(this.getValue().getName());
    }

    @Override
    protected boolean shouldUpdateData() {
        return true;
    }
}
