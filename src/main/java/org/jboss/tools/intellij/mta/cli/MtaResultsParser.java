package org.jboss.tools.intellij.mta.cli;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.intellij.mta.model.FileUtil;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.model.MtaConfiguration.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

public class MtaResultsParser {

    public static void parseResults(MtaConfiguration configuration, Boolean readQuickfixes) {
        MtaConfiguration.AnalysisResultsSummary summary = configuration.getSummary();
        if (summary != null) {
            Document doc = MtaResultsParser.openDocument(summary.outputLocation + File.separator + "results.xml");
            if (doc != null) {
                MtaResultsParser.parseReports(doc, configuration);
                MtaResultsParser.parseHints(doc, configuration, readQuickfixes);
                MtaResultsParser.parseClassifications(doc, configuration);
            }
            else {
                System.out.println("Error parsing analysis results at: " + summary.outputLocation);
            }
        }
    }

    public static void loadAndPersistIDs(MtaConfiguration configuration, String outputLocation) {
        try {
            Document doc = MtaResultsParser.openDocument(outputLocation + File.separator + "results.xml");
            if (doc != null) {
                MtaResultsParser.setElementIDs(doc, "hint");
                MtaResultsParser.setElementIDs(doc, "classification");
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                Result output = new StreamResult(new File(outputLocation + File.separator + "results.xml"));
                Source input = new DOMSource(doc);
                transformer.transform(input, output);
            } else {
                System.out.println("Error loading analysis results at: " + outputLocation);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setElementIDs(Document doc, String element) {
        NodeList nodeList = doc.getElementsByTagName(element);
        for (int temp = 0; temp < nodeList.getLength(); temp++) {
            Node elementNode = nodeList.item(temp);
            if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) elementNode;
                eElement.setAttribute("id", MtaConfiguration.generateUniqueId());
                NodeList quickfixList = eElement.getElementsByTagName("quickfix");
                for (int temp2 = 0; temp2 < quickfixList.getLength(); temp2++) {
                    Node quickfixNode = quickfixList.item(temp2);
                    if (quickfixNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element quickfixNode1 = (Element) quickfixNode;
                        quickfixNode1.setAttribute("id", MtaConfiguration.generateUniqueId());
                    }
                }
            }
        }
    }

    private static Document openDocument(String location) {
        try {
            File inputFile = new File(location);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void parseReports(Document doc, MtaConfiguration configuration) {
        NodeList reportLinks = doc.getElementsByTagName("report-link");
        for (int temp = 0; temp < reportLinks.getLength(); temp++) {
            Node reportLink = reportLinks.item(temp);
            if (reportLink.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) reportLink;
                String inputFile = MtaResultsParser.getValue(eElement,"input-file");
                String reportFile = MtaResultsParser.getValue(eElement,"report-file");
                configuration.getSummary().reports.put(inputFile, reportFile);
            }
        }
    }

    private static void parseHints(Document doc, MtaConfiguration configuration, Boolean readQuickfixes) {
        NodeList hintList = doc.getElementsByTagName("hint");
        for (int temp = 0; temp < hintList.getLength(); temp++) {
            Node hintNode = hintList.item(temp);
            if (hintNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) hintNode;
                if (eElement.getParentNode().getNodeName() != "hints") continue;

                String deleted = eElement.getAttribute("deleted");
                if (!"".equals(deleted)) {
                    continue;
                }

                Hint hint = new Hint();
                hint.id = eElement.getAttribute("id");
                if ("".equals(hint.id)) {
                    hint.id = MtaConfiguration.generateUniqueId();
                }
                hint.configuration = configuration;
                configuration.getSummary().hints.add(hint);

                String complete = eElement.getAttribute("complete");
                if (!"".equals(complete)) {
                    hint.complete = true;
                }

                String title = MtaResultsParser.getValue(eElement,"title");
                if (title != null) {
                    hint.title = title;
                }

                String effort = MtaResultsParser.getValue(eElement,"effort");
                if (effort != null) {
                    hint.effort = effort;
                }

                String file = MtaResultsParser.getValue(eElement,"file");
                if (file != null) {
                    hint.file = file;
                    String report = configuration.getSummary().reports.get(hint.file);
                    if (report != null) {
                        hint.report = report;
                    }
                }

                String hintText = MtaResultsParser.getValue(eElement,"hint");
                if (hintText != null) {
                    hint.hint = hintText;
                }

                String category = MtaResultsParser.getValue(eElement,"categoryID");
                if (category != null) {
                    hint.category = category;
                }

                String ruleId = MtaResultsParser.getValue(eElement,"rule-id");
                if (ruleId != null) {
                    hint.ruleId = ruleId;
                }

                String length = MtaResultsParser.getValue(eElement,"length");
                if (length != null) {
                    hint.length = Integer.parseInt(length);
                }

                String lineNumber = MtaResultsParser.getValue(eElement,"line-number");
                if (lineNumber != null) {
                    hint.lineNumber = Integer.parseInt(lineNumber);
                }

                String column = MtaResultsParser.getValue(eElement,"column");
                if (column != null) {
                    hint.column = Integer.parseInt(column);
                }

                NodeList links = eElement.getElementsByTagName("link");
                for (int temp1 = 0; temp1 < links.getLength(); temp1++) {
                    Node linkNode = links.item(temp1);
                    if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                        Link link = new Link();
                        hint.links.add(link);
                        link.id = MtaConfiguration.generateUniqueId();
                        String linkDescription = MtaResultsParser.getValue((Element)linkNode, "description");
                        if (linkDescription != null) {
                            link.title = linkDescription;
                        }
                        String url = MtaResultsParser.getValue((Element)linkNode, "url");
                        if (url != null) {
                            link.url = url;
                        }
                    }
                }

                MtaResultsParser.computeQuickfixes(hint, eElement, configuration, readQuickfixes);
            }
        }
    }

    private static void parseClassifications(Document doc, MtaConfiguration configuration) {
        NodeList classificationList = doc.getElementsByTagName("classification");
        for (int temp = 0; temp < classificationList.getLength(); temp++) {
            Node classificationNode = classificationList.item(temp);
            if (classificationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) classificationNode;
                if (eElement.getParentNode().getNodeName() != "classifications") continue;

                String deleted = eElement.getAttribute("deleted");
                if (!"".equals(deleted)) {
                    continue;
                }

                Classification classification = new Classification();
                classification.id = eElement.getAttribute("id");
                if ("".equals(classification.id)) {
                    classification.id = MtaConfiguration.generateUniqueId();
                }
                classification.configuration = configuration;
                configuration.getSummary().classifications.add(classification);

                String title = MtaResultsParser.getValue(eElement,"classification");
                if (title != null) {
                    classification.title = title;
                }

                String description = MtaResultsParser.getValue(eElement,"description");
                if (description != null) {
                    classification.description = description;
                }

                String effort = MtaResultsParser.getValue(eElement,"effort");
                if (effort != null) {
                    classification.effort = effort;
                }

                String file = MtaResultsParser.getValue(eElement,"file");
                if (file != null) {
                    classification.file = file;
                    String report = configuration.getSummary().reports.get(classification.file);
                    if (report != null) {
                        classification.report = report;
                    }
                }

                String category = MtaResultsParser.getValue(eElement,"categoryID");
                if (category != null) {
                    classification.category = category;
                }

                NodeList links = eElement.getElementsByTagName("link");
                for (int temp1 = 0; temp1 < links.getLength(); temp1++) {
                    Node linkNode = links.item(temp1);
                    if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                        Link link = new Link();
                        classification.links.add(link);
                        link.id = MtaConfiguration.generateUniqueId();
                        String linkDescription = MtaResultsParser.getValue((Element)linkNode,"description");
                        if (linkDescription != null) {
                            link.title = linkDescription;
                        }
                        String url = MtaResultsParser.getValue((Element)linkNode,"url");
                        if (url != null) {
                            link.url = url;
                        }
                    }
                }
                String ruleId = MtaResultsParser.getValue(eElement,"rule-id");
                if (ruleId != null) {
                    classification.ruleId = ruleId;
                }
            }
        }
    }

    private static void computeQuickfixes(Hint hint,
                                          Element element,
                                          MtaConfiguration configuration,
                                          boolean readQuickfixes) {
        NodeList quickfixList = element.getElementsByTagName("quickfix");
        for (int temp = 0; temp < quickfixList.getLength(); temp++) {
            Node quickfixNode = quickfixList.item(temp);
            if (quickfixNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) quickfixNode;
                QuickFix quickfix = new QuickFix();
                quickfix.id = eElement.getAttribute("id");
                quickfix.issue = hint;
                quickfix.file = hint.file;
                quickfix.type = QuickFixType.REPLACE;
                hint.quickfixes.add(quickfix);

                String file = MtaResultsParser.getValue(eElement,"file");
                if (file != null) {
                    quickfix.file = file;
                }

                String name = MtaResultsParser.getValue(eElement,"name");
                if (name != null) {
                    quickfix.name = name;
                }

                String newLine = MtaResultsParser.getValue(eElement,"newLine");
                if (newLine != null) {
                    quickfix.newLine = newLine;
                }

                String replacement = MtaResultsParser.getValue(eElement,"replacement");
                if (replacement != null) {
                    quickfix.replacementString = replacement;
                }

                String search = MtaResultsParser.getValue(eElement,"search");
                if (search != null) {
                    quickfix.searchString = search;
                }

                String type = MtaResultsParser.getValue(eElement,"type");
                if (type != null) {
                    quickfix.type = QuickFixType.valueOf(type);
                }

                if (readQuickfixes) {
                    if (isTextMimeType(hint)) {
                        hint.originalLineSource = FileUtil.getLine(hint.file, hint.lineNumber-1);
                        if (quickfix.type == QuickFixType.REPLACE) {
                            String quickfixedLine = hint.originalLineSource.replace(
                                    quickfix.searchString,
                                    quickfix.replacementString);
                            hint.quickfixedLines.put(quickfix.id, quickfixedLine);
                        }
                    }
                    else {
                        System.err.println("Unable to read mime type for file: " + hint.file);
                    }
                }
                else {
                    AnalysisResultsSummary summary = configuration.getSummary();
                    if (summary != null && summary.quickfixData != null) {
                        QuickfixEntry entry = summary.quickfixData.entries.get(hint.id);
                        if (entry != null) {
                            hint.originalLineSource = entry.originalLineSource;
                            hint.quickfixedLines.put(quickfix.id, entry.quickfixes.get(quickfix.id));
                        }
                        else {
                            System.err.println("No quickfix info mapped to issue with ID=" + hint.id);
                        }
                    }
                }
            }
        }
    }

    private static boolean isTextMimeType(Hint hint) {
        File file = new File(hint.file);
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());
        if (mimeType != null && mimeType.startsWith("text")) {
            return true;
        }
        else {
            System.err.println("Hint corresponding to file " + hint.file +
                    " contains unsupported mime type " + mimeType +
                    " - Rule ID: " + hint.ruleId + " Hint: " + hint.hint);
            return false;
        }
    }

    private static String getValue(Element parent, String tag) {
        Node item = parent.getElementsByTagName(tag).item(0);
        if (item != null) {
            return item.getTextContent();
        }
        return null;
    }
}
