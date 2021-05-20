package org.jboss.tools.intellij.mta.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.io.FileUtils;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.intellij.mta.explorer.dialog.MtaNotifier;

import java.io.File;

import static org.jboss.tools.intellij.mta.model.MtaConfiguration.*;

public class QuickfixUtil {

    public static String getQuickFixedContent(QuickFix quickFix) throws Exception {
        String contents = FileUtils.readFileToString(new File(quickFix.file));
        IDocument document = new Document(contents);
        Hint hint = (Hint)quickFix.issue;
        if (QuickFixType.REPLACE.toString().equals(quickFix.type.type)) {
            int lineNumber = hint.lineNumber-1;
            String searchString = quickFix.searchString;
            String replacement = quickFix.replacementString;
            FileUtil.replace(document, lineNumber, searchString, replacement);
            return document.get();
        }
        else if (QuickFixType.DELETE_LINE.toString().equals(quickFix.type.type)) {
            int lineNumber = hint.lineNumber-1;
            FileUtil.deleteLine(document, lineNumber);
            return document.get();
        }
        else if (QuickFixType.INSERT_LINE.toString().equals(quickFix.type.type)) {
            int lineNumber = hint.lineNumber;
            lineNumber = lineNumber > 1 ? lineNumber - 2 : lineNumber-1;
            String newLine = quickFix.replacementString;
            try {
                FileUtil.insertLine(document, lineNumber, newLine);
                return document.get();
            }
            catch (Exception e) {
                e.printStackTrace();
                MtaNotifier.notifyError("Error processing insert quickfix on file - " + hint.file);
            }
        }
        return null;
    }

    public static void applyQuickfix(QuickFix quickfix, Project project, String content) throws Exception {
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(quickfix.file);
        PsiFile importPsi = PsiManager.getInstance(project).findFile(file);
        com.intellij.openapi.editor.Document doc = PsiDocumentManager.getInstance(project).getCachedDocument(importPsi);
        if (doc == null) {
            doc = PsiDocumentManager.getInstance(project).getDocument(importPsi);
        }
        if (doc != null) {
            doc.setText(content);
        }
        else {
            MtaNotifier.notifyError("Unable to find document for - " + quickfix.file);
        }
    }
}
