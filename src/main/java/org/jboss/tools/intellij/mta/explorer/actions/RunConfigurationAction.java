package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import org.jboss.tools.intellij.mta.cli.MtaCliRunner;
import org.jboss.tools.intellij.mta.explorer.dialog.MtaNotifier;
import org.jboss.tools.intellij.mta.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class RunConfigurationAction extends StructureTreeAction {

    public RunConfigurationAction() {
        super(ConfigurationNode.class);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        ConfigurationNode node = (ConfigurationNode)super.adjust(selected);
        MtaConfiguration configuration = node.getValue();
        if (this.validateConfiguration(configuration)) {
            MtaCliRunner.run(configuration);
        }
    }

    private boolean validateConfiguration(MtaConfiguration configuration) {
        boolean valid = true;
        String cliLocation = (String)configuration.getOptions().get("mta-cli");
        if (cliLocation == null || "".equals(cliLocation)) {
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
        return valid;
    }
}
