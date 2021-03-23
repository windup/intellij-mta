package org.jboss.tools.intellij.mta.model;

import org.eclipse.jface.text.*;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static String getLine(String file, int lineNumber) {
        try {
            String contents = org.apache.commons.io.FileUtils.readFileToString(new File(file));
            IDocument document = new org.eclipse.jface.text.Document(contents);
            IRegion region = document.getLineInformation(lineNumber);
            return document.get(region.getOffset(), region.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void replace(IDocument document, int lineNumber, String searchString, String replacement) {
        try {
            IRegion info = document.getLineInformation(lineNumber);
            FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(document);
            IRegion search = adapter.find(info.getOffset(), searchString, true, true, false/*true*/, false);
            if (search != null) {
                document.replace(search.getOffset(), search.getLength(), replacement);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static IDocument deleteLine(File file, int lineNumber) {
        try {
            String contents = org.apache.commons.io.FileUtils.readFileToString(file);
            IDocument document = new Document(contents);
            return FileUtil.deleteLine(document, lineNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IDocument deleteLine(IDocument document, int lineNumber) {
        try {
            IRegion info = document.getLineInformation(lineNumber);
            document.replace(info.getOffset(), info.getLength()+1, null);
            return document;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
