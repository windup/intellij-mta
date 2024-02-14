/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.model;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.io.FileUtils;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.intellij.windup.explorer.dialog.WindupNotifier;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jboss.tools.intellij.windup.model.WindupConfiguration.*;

public class QuickfixUtil {

    private static String replaceValue(String tag, String value, String content) {
        String regex = "<(.*?)>(.*?)</(.*?)>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            content = content.replaceAll(matcher.group(1), tag)
                    .replaceAll(matcher.group(2), value)
                    .replaceAll(matcher.group(3), tag);
        }
        return content;
    }

    public static String getQuickFixedContent(QuickFix quickFix) throws Exception {
        System.out.println("applyQuickfix...");
        String contents = FileUtils.readFileToString(new File(quickFix.file));
        IDocument document = new Document(contents);

        String searchString = quickFix.searchString;
        String replacementString = quickFix.replacementString;

        Incident hint = (Incident)quickFix.issue;
        String type = quickFix.type.type;
        if (QuickFixType.REPLACE.toString().equals(type) && new File(quickFix.file).getName().equals("pom.xml")) {
            int lineNumber = ((Incident) quickFix.issue).getLineNumber()-1;
            String currentText = FileUtil.getLine(quickFix.file, lineNumber);
            if (currentText.contains(searchString)) {
                FileUtil.replace(document, lineNumber, searchString, replacementString);
                return document.get();
            }
            else {
                String line = FileUtil.getLine(quickFix.file, lineNumber);
                int linesRead = 0; // let's not read forever. 10 lines for a <dependency> tag is more than reasonable.
                System.out.println("start reading dependency entry");
                System.out.println(line);
                while (!line.contains("</dependency>") && linesRead < 10) {
                    System.out.println("line: " + line);
                    if (line.contains(searchString)) {
                        /*
                            We're opting not to replace the inner text of a tag because
                            some quickfixes replace only part of the artifact or group ID instead of
                            the entire ID.
                         */
                        if (line.contains("<groupId>")) {
//                            String newLine = QuickfixUtil.replaceValue("groupId", replacementString, line);
                            String newLine = line.replace(searchString, replacementString);
                            System.out.println("newLine: " + newLine);
                            FileUtil.replace(document, lineNumber, line, newLine);
                        }
                        else if (line.contains("<artifactId>")) {
//                            String newLine = QuickfixUtil.replaceValue("artifactId", replacementString, line);
                            String newLine = line.replace(searchString, replacementString);
                            System.out.println("newLine: " + newLine);
                            FileUtil.replace(document, lineNumber, line, newLine);
                        }
                        System.out.println("done apply quickfix");
                        return document.get();
                    }
                    line = FileUtil.getLine(quickFix.file, ++lineNumber);
                    linesRead++;
                }
                System.out.println("null!");
            }
            System.out.println("null!");
            return null;
        }
        else if (QuickFixType.REPLACE.toString().equals(type)) {
            int lineNumber = hint.getLineNumber()-1;
            FileUtil.replace(document, lineNumber, searchString, replacementString);
            return document.get();
        }
        else if (QuickFixType.DELETE_LINE.toString().equals(type)) {
            int lineNumber = hint.getLineNumber()-1;
            FileUtil.deleteLine(document, lineNumber);
            return document.get();
        }
        else if (QuickFixType.INSERT_LINE.toString().equals(type)) {
            int lineNumber = hint.getLineNumber();
            lineNumber = lineNumber > 1 ? lineNumber - 2 : lineNumber-1;
            String newLine = replacementString;
            try {
                FileUtil.insertLine(document, lineNumber, newLine);
                return document.get();
            }
            catch (Exception e) {
                e.printStackTrace();
                WindupNotifier.notifyError("Error processing insert quickfix on file - " + hint.file);
            }
        }
        return null;
    }

    public static void applyQuickfix(QuickFix quickfix, Project project, String content) {
        final com.intellij.openapi.editor.Document doc = QuickfixUtil.findDocument(project, quickfix.file);
        if (content == null) {
            System.out.println("null!");
        }
        if (doc != null) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                doc.setText(content);
                PsiDocumentManager.getInstance(project).commitDocument(doc);
                FileDocumentManager.getInstance().saveDocument(doc);
            });

        }
        else {
            WindupNotifier.notifyError("Unable to find document for - " + quickfix.file);
        }
    }

    private static com.intellij.openapi.editor.Document findDocument(Project project, String path) {
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(path);
        PsiFile importPsi = PsiManager.getInstance(project).findFile(file);
        com.intellij.openapi.editor.Document doc = null;
        PsiDocumentManager.getInstance(project).getCachedDocument(importPsi);
        if (doc == null) {
            doc = PsiDocumentManager.getInstance(project).getDocument(importPsi);
        }
        return doc;
    }
}
