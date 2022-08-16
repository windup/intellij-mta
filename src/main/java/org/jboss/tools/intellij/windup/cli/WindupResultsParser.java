/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.cli;

import org.jboss.tools.intellij.windup.explorer.dialog.WindupNotifier;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;
import org.jboss.tools.intellij.windup.model.WindupConfiguration.*;
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

public class WindupResultsParser {

    public static void parseResults(WindupConfiguration configuration) {
        WindupConfiguration.AnalysisResultsSummary summary = configuration.getSummary();
        if (summary != null) {
            Document doc = WindupResultsParser.openDocument(summary.outputLocation + File.separator + "results.xml");
            if (doc != null) {
                WindupResultsParser.parseReports(doc, configuration);
                WindupResultsParser.parseHints(doc, configuration);
                WindupResultsParser.parseClassifications(doc, configuration);
            }
            else {
                System.out.println("Error parsing analysis results at: " + summary.outputLocation);
            }
        }
    }

    public static void loadAndPersistIDs(WindupConfiguration configuration, String outputLocation) {
        String resultsLocation = outputLocation + File.separator + "results.xml";
        try {
            Document doc = WindupResultsParser.openDocument(resultsLocation);
            if (doc != null) {
                WindupResultsParser.setElementIDs(doc, "hint");
                WindupResultsParser.setElementIDs(doc, "classification");
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                Result output = new StreamResult(new File(resultsLocation));
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
                eElement.setAttribute("id", WindupConfiguration.generateUniqueId());
                NodeList quickfixList = eElement.getElementsByTagName("quickfix");
                for (int temp2 = 0; temp2 < quickfixList.getLength(); temp2++) {
                    Node quickfixNode = quickfixList.item(temp2);
                    if (quickfixNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element quickfixNode1 = (Element) quickfixNode;
                        quickfixNode1.setAttribute("id", WindupConfiguration.generateUniqueId());
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

    private static void parseReports(Document doc, WindupConfiguration configuration) {
        NodeList reportLinks = doc.getElementsByTagName("report-link");
        for (int temp = 0; temp < reportLinks.getLength(); temp++) {
            Node reportLink = reportLinks.item(temp);
            if (reportLink.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) reportLink;
                String inputFile = WindupResultsParser.getValue(eElement,"input-file");
                String reportFile = WindupResultsParser.getValue(eElement,"report-file");
                configuration.getSummary().reports.put(inputFile, reportFile);
            }
        }
    }

    private static void parseHints(Document doc, WindupConfiguration configuration) {
        NodeList hintList = doc.getElementsByTagName("hint");
        for (int temp = 0; temp < hintList.getLength(); temp++) {
            Node hintNode = hintList.item(temp);
            if (hintNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) hintNode;
                if (eElement.getParentNode().getNodeName() != "hints") continue;

                String id = eElement.getAttribute("id");
                if (configuration.getSummary().deletedIssues.contains(id)) {
                    continue;
                }

                Hint hint = new Hint();
                hint.id = eElement.getAttribute("id");
                if ("".equals(hint.id)) {
                    hint.id = WindupConfiguration.generateUniqueId();
                    System.out.println("Windup results.xml not serialized with IDs");
                    WindupNotifier.notifyError("Windup results.xml not serialized with IDs. Please try re-running the analysis with the latest version of the Windup plugin.");
                }

                if (configuration.getSummary().completeIssues.contains(id)) {
                    hint.complete = true;
                }

                hint.configuration = configuration;
                configuration.getSummary().hints.add(hint);

                String complete = eElement.getAttribute("complete");
                if (!"".equals(complete)) {
                    hint.complete = true;
                }

                String title = WindupResultsParser.getValue(eElement,"title");
                if (title != null) {
                    hint.title = title;
                }

                String effort = WindupResultsParser.getValue(eElement,"effort");
                if (effort != null) {
                    hint.effort = effort;
                }

                String file = WindupResultsParser.getValue(eElement,"file");
                if (file != null) {
                    hint.file = file;
                    String report = configuration.getSummary().reports.get(hint.file);
                    if (report != null) {
                        hint.report = report;
                    }
                }

                String hintText = WindupResultsParser.getValue(eElement,"hint");
                if (hintText != null) {
                    hint.hint = hintText;
                }

                String category = WindupResultsParser.getValue(eElement,"categoryID");
                if (category != null) {
                    hint.category = category;
                }

                String ruleId = WindupResultsParser.getValue(eElement,"rule-id");
                if (ruleId != null) {
                    hint.ruleId = ruleId;
                }

                String length = WindupResultsParser.getValue(eElement,"length");
                if (length != null) {
                    hint.length = Integer.parseInt(length);
                }

                String lineNumber = WindupResultsParser.getValue(eElement,"line-number");
                if (lineNumber != null) {
                    hint.lineNumber = Integer.parseInt(lineNumber);
                }

                String column = WindupResultsParser.getValue(eElement,"column");
                if (column != null) {
                    hint.column = Integer.parseInt(column);
                }

                NodeList links = eElement.getElementsByTagName("link");
                for (int temp1 = 0; temp1 < links.getLength(); temp1++) {
                    Node linkNode = links.item(temp1);
                    if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                        Link link = new Link();
                        hint.links.add(link);
                        link.id = WindupConfiguration.generateUniqueId();
                        String linkDescription = WindupResultsParser.getValue((Element)linkNode, "description");
                        if (linkDescription != null) {
                            link.title = linkDescription;
                        }
                        String url = WindupResultsParser.getValue((Element)linkNode, "url");
                        if (url != null) {
                            link.url = url;
                        }
                    }
                }
                WindupResultsParser.computeQuickfixes(hint, eElement);
            }
        }
    }

    private static void parseClassifications(Document doc, WindupConfiguration configuration) {
        NodeList classificationList = doc.getElementsByTagName("classification");
        for (int temp = 0; temp < classificationList.getLength(); temp++) {
            Node classificationNode = classificationList.item(temp);
            if (classificationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) classificationNode;
                if (eElement.getParentNode().getNodeName() != "classifications") continue;

                String id = eElement.getAttribute("id");
                if (configuration.getSummary().deletedIssues.contains(id)) {
                    continue;
                }

                Classification classification = new Classification();
                classification.id = eElement.getAttribute("id");
                if ("".equals(classification.id)) {
                    classification.id = WindupConfiguration.generateUniqueId();
                    System.out.println("Windup results.xml not serialized with IDs");
                    WindupNotifier.notifyError("Windup results.xml not serialized with IDs. Please try re-running the analysis with the latest version of the Windup plugin.");
                }

                if (configuration.getSummary().completeIssues.contains(id)) {
                    classification.complete = true;
                }

                classification.configuration = configuration;
                configuration.getSummary().classifications.add(classification);

                String title = WindupResultsParser.getValue(eElement,"classification");
                if (title != null) {
                    classification.title = title;
                }

                String description = WindupResultsParser.getValue(eElement,"description");
                if (description != null) {
                    classification.description = description;
                }

                String effort = WindupResultsParser.getValue(eElement,"effort");
                if (effort != null) {
                    classification.effort = effort;
                }

                String file = WindupResultsParser.getValue(eElement,"file");
                if (file != null) {
                    classification.file = file;
                    String report = configuration.getSummary().reports.get(classification.file);
                    if (report != null) {
                        classification.report = report;
                    }
                }

                String category = WindupResultsParser.getValue(eElement,"categoryID");
                if (category != null) {
                    classification.category = category;
                }

                NodeList links = eElement.getElementsByTagName("link");
                for (int temp1 = 0; temp1 < links.getLength(); temp1++) {
                    Node linkNode = links.item(temp1);
                    if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                        Link link = new Link();
                        classification.links.add(link);
                        link.id = WindupConfiguration.generateUniqueId();
                        String linkDescription = WindupResultsParser.getValue((Element)linkNode,"description");
                        if (linkDescription != null) {
                            link.title = linkDescription;
                        }
                        String url = WindupResultsParser.getValue((Element)linkNode,"url");
                        if (url != null) {
                            link.url = url;
                        }
                    }
                }
                String ruleId = WindupResultsParser.getValue(eElement,"rule-id");
                if (ruleId != null) {
                    classification.ruleId = ruleId;
                }
            }
        }
    }

    private static void computeQuickfixes(Hint hint,
                                          Element element) {
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

                String file = WindupResultsParser.getValue(eElement,"file");
                if (file != null) {
                    quickfix.file = file;
                }

                String name = WindupResultsParser.getValue(eElement,"name");
                if (name != null) {
                    quickfix.name = name;
                }

                String newLine = WindupResultsParser.getValue(eElement,"newLine");
                if (newLine != null) {
                    quickfix.newLine = newLine;
                }

                String replacement = WindupResultsParser.getValue(eElement,"replacement");
                if (replacement != null) {
                    quickfix.replacementString = replacement;
                }

                String search = WindupResultsParser.getValue(eElement,"search");
                if (search != null) {
                    quickfix.searchString = search;
                }

                String type = WindupResultsParser.getValue(eElement,"type");
                if (type != null) {
                    quickfix.type = QuickFixType.valueOf(type);
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
