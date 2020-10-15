package org.jboss.tools.intellij.mta.explorer.nodes;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;

import java.io.File;
import java.util.Collection;

public abstract class IssueNode<T extends Issue> extends MtaExplorerNode<T> {

    public IssueNode(T issue) {
        super(issue);
    }

    @Override
    public void onClick(Project project) {
        String filePath = this.getValue().file;
        if (project != null) {
            String name = new File(filePath).getName();
            Collection<VirtualFile> vFiles = FilenameIndex.getVirtualFilesByName(project, name,
                    GlobalSearchScope.allScope(project));
            boolean found = false;
            for (VirtualFile vFile : vFiles) {
                String vFilePath = vFile.getCanonicalPath();
                if (vFilePath.equals(filePath)) {
                    found = true;
                    new OpenFileDescriptor(project, vFile, 0).navigate(true);
                }
            }

            if (!found) {
                System.out.println("" +
                    "Error - Cannot open source editor. " +
                    "Unable to find project virtual file corresponding to file: " +
                    filePath);
            }
        }
        else {
            System.out.println("" +
                    "Error - Cannot open source editor." +
                    " Unable to find project corresponding to file: " +
                    filePath);
        }
    }

    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }
}
