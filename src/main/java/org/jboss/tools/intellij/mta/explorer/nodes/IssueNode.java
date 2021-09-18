/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.explorer.nodes;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
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
            System.out.println(name + " vFiles.size() for : " + vFiles.size());
            for (VirtualFile vFile : vFiles) {
                String vFilePath = vFile.getCanonicalPath();
                if (vFilePath.equals(filePath)) {
                    found = true;
                    try {
                        Issue issue = super.getValue();
                        if (issue instanceof Hint) {
                            Hint hint = (Hint)issue;
                            Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vFile, hint.lineNumber - 1, hint.column), true);
                            int offset1 = editor.getSelectionModel().getLeadSelectionOffset();
                            editor.getSelectionModel().setSelection(offset1, offset1+hint.length);
                        }
                        else {
                            new OpenFileDescriptor(project, vFile, 0).navigate(true);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public void setComplete() {
        this.getValue().complete = true;
        this.getValue().configuration.getSummary().completeIssues.add(this.getValue().id);
    }

    public boolean isComplete() {
        return this.getValue().complete;
    }

    public void deleteIssue() {
        this.getValue().deleted = true;
        this.getValue().configuration.getSummary().deletedIssues.add(this.getValue().id);
    }

    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }
}
