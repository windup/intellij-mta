package org.jboss.tools.intellij.mta.explorer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jboss.tools.intellij.mta.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

import javax.swing.tree.TreePath;

public class RunConfigurationAction extends AbstractTreeAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent, TreePath path, Object selected) {
        ConfigurationNode node = (ConfigurationNode)selected;
        MtaConfiguration configuration = (MtaConfiguration)node.getValue();
    }
}
