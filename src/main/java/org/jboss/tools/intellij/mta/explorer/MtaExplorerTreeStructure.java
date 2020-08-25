package org.jboss.tools.intellij.mta.explorer;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.util.treeView.AbstractTreeStructureBase;
import com.intellij.openapi.project.Project;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaExplorerRootNode;
import org.jboss.tools.intellij.mta.explorer.nodes.MtaNodeModel;
import org.jboss.tools.intellij.mta.model.MtaModel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MtaExplorerTreeStructure extends AbstractTreeStructureBase {

    private MtaExplorerRootNode rootNode;

    protected MtaExplorerTreeStructure(MtaExplorerRootNode rootNode) {
        super(null);
        this.rootNode = rootNode;
    }

    @Nullable
    @Override
    public List<TreeStructureProvider> getProviders() {
        return null;
    }

    @Override
    public Object getRootElement() {
        return this.rootNode;
    }

    @Override
    public void commit() {
    }

    @Override
    public Object[] getChildElements(Object element) {
        return super.getChildElements(element);
    }

    @Override
    public boolean hasSomethingToCommit() {
        return false;
    }
}
