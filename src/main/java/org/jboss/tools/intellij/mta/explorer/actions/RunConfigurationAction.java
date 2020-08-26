package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.cli.MtaCliRunner;
import org.jboss.tools.intellij.mta.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.stream.Stream;

public class RunConfigurationAction extends StructureTreeAction {

    public RunConfigurationAction() {
        super(ConfigurationNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        ConfigurationNode node = (ConfigurationNode)super.adjust(selected);
        MtaConfiguration configuration = (MtaConfiguration)node.getValue();
        // if (this.validateConfiguration(configuration)) {
            MtaCliRunner.run(configuration);
        // }
    }

    private boolean validateConfiguration(MtaConfiguration configuration) {
        boolean validInput = MtaCliRunner.validateOptionExists(configuration, "input");
        if (!validInput) {
            // Notify input required
            return false;
        }
        boolean validOutput = MtaCliRunner.validateOptionExists(configuration, "output");
        if (!validOutput) {
            // Notify output required
            return false;
        }
        return true;
    }
}
