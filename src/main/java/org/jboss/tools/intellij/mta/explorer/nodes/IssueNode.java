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
    public void onClick() {
        String filePath = this.getValue().file;
        Project project = this.get.getProject();
        if (project != null) {
            String name = new File(filePath).getName();
            Collection<VirtualFile> vFiles = FilenameIndex.getVirtualFilesByName(project, name,
                    GlobalSearchScope.allScope(project));
            for (VirtualFile vFile : vFiles) {
                String vFilePath = vFile.getCanonicalPath();
                if (vFilePath.equals(filePath)) {
                    new OpenFileDescriptor(project, vFile, 0).navigate(true);
                }
            }

            Collection<VirtualFile> foundFiles = FileBasedIndex.getInstance().getContainingFiles(FilenameIndex.NAME, filePath, GlobalSearchScope.allScope(project));
            VirtualFile dFile = ContainerUtil.getFirstItem(foundFiles);

            String[] files = FilenameIndex.getAllFilenames(project);

            if (!vFiles.isEmpty()) {
                VirtualFile vFile = ContainerUtil.getFirstItem(vFiles);
                new OpenFileDescriptor(project, vFile, 0).navigate(true);
            }
            else {
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
}
