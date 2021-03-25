package org.jboss.tools.intellij.mta.model;

import com.google.common.collect.Lists;
import org.eclipse.jface.text.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public static IDocument insertLine(IDocument document, int lineNumber, String newLine) throws Exception {
        IRegion previousLine = document.getLineInformation(lineNumber);
        List<String> indentChars = FileUtil.getLeadingChars(document, previousLine);

        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator());
        for (String indentChar : indentChars) {
            builder.append(indentChar);
        }
        builder.append(newLine);

        int newLineOffset = previousLine.getOffset() + previousLine.getLength();
        document.replace(newLineOffset, 0, builder.toString());
        return document;
    }

    private static List<String> getLeadingChars(IDocument document, IRegion line) throws BadLocationException {
        List<String> result = Lists.newArrayList();
        int pos= line.getOffset();
        int max= pos + line.getLength();
        while (pos < max) {
            char next = document.getChar(pos);
            if (!Character.isWhitespace(next))
                break;
            result.add(String.valueOf(next));
            pos++;
        }
        return result;
    }
}
