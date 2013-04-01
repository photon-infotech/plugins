package com.photon.phresco.plugins.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FilenameUtils;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;

public class TRXParser implements PluginConstants {

	/**
	 * @param args
	 * @throws PhrescoException 
	 */
	
	private static Map<String, String> testSuiteMap = new HashMap<String, String>();
	private static File outputDir;
	
	public void parsingTrx(File inputFilePath, File outputFilePath)  throws PhrescoException  {
		try {
			if (!inputFilePath.exists()) {
				return;
			}
			outputDir = outputFilePath; 
			String className = "";
			String name = "";
			String duration = "";
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(false);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(inputFilePath);
			
			getTestSuiteInfo(doc);
			
			String expression = "//TestDefinitions/*";
			NodeList nodes = getNodeList(doc, expression);
			
			org.jdom.Element testsuite = new org.jdom.Element(TEST_SUITE);
			org.jdom.Document doc1 = new org.jdom.Document(testsuite);
			doc1.setRootElement(testsuite);

			for (int i = 0; i < nodes.getLength(); i++) {
				Node item = nodes.item(i);
				Element unitTests = (Element) item;
				String id = unitTests.getAttribute(ATTR_ID);
				duration = getDuration(doc, id);
				NodeList childNodes = item.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node testMethod = childNodes.item(j);
					if (testMethod.getNodeName().equalsIgnoreCase(TEST_METHOD)) {
						Element tests = (Element) testMethod;
						className = tests.getAttribute(CLASS_NAME);
						name = tests.getAttribute(NAME);
					}
				}
				writeTestCase(doc1, duration, className, name);
			}


		} catch (XPathExpressionException e) {
			throw new PhrescoException(e);
		} catch (DOMException e) {
			throw new PhrescoException(e);
		} catch (ParserConfigurationException e) {
			throw new PhrescoException(e);
		} catch (SAXException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
	}
	

	private static void getTestSuiteInfo(Document doc) throws XPathExpressionException {
		String expression = "//ResultSummary/*";
		NodeList nodeList = getNodeList(doc, expression);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			Element testSummary = (Element) item;
			
			String total = testSummary.getAttribute(TOTAL);
			String passed = testSummary.getAttribute(PASSED);
			String failed = testSummary.getAttribute(FAILED);
			String skipped = testSummary.getAttribute(SKIPPED);
			String errors = testSummary.getAttribute(ERROR);
			
			testSuiteMap.put(ATTR_FAILURES, failed);
			testSuiteMap.put(ATTR_ERRORS, errors);
			testSuiteMap.put(SKIPED, skipped);
			testSuiteMap.put(ATTR_TESTS, total);
			testSuiteMap.put(PASSED, passed);
		}
	}

	private static String getDuration(Document doc, String id) throws XPathExpressionException {
		String expression = "//Results/*";
		NodeList nodeList = getNodeList(doc, expression);
		String duration = "";
		String outcome = "";
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			Element unitTestResult = (Element) item;
			String testId = unitTestResult.getAttribute(TEST_ID);
			if (testId.equals(id)) {
				duration = unitTestResult.getAttribute(DURATION);
				outcome = unitTestResult.getAttribute(OUTCOME);
				StringBuffer errorMessage = new StringBuffer();
				if (outcome.equalsIgnoreCase(FAILED)) {
					String expr = "//UnitTestResult[@testId='"+testId+"']//Output//ErrorInfo//*/text()";
					NodeList errorNode = getNodeList(doc, expr);
					for (int j = 0; j < errorNode.getLength(); j++) {
						errorMessage.append(errorNode.item(j).getNodeValue());
					}
				}
				testSuiteMap.put(ERROR_MESSAGE, errorMessage.toString());
				testSuiteMap.put(OUTCOME, outcome);
			}
		}
		return duration;
	}

	private static NodeList getNodeList(Document doc, String expression) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		javax.xml.xpath.XPathExpression expr = xpath.compile(expression);
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		return nodes;
	}


	private static void writeTestCase(org.jdom.Document doc, String duration, String className, String name) throws IOException {
		String failures = testSuiteMap.get(ATTR_FAILURES);
		String errors = testSuiteMap.get(ATTR_ERRORS);
		String skipped = testSuiteMap.get(SKIPED);
		String tests = testSuiteMap.get(ATTR_TESTS);
		String passed = testSuiteMap.get(PASSED);
		String testSuiteName = className.substring(0, className.lastIndexOf("."));
		String errorMessage = testSuiteMap.get(ERROR_MESSAGE);
		String outcome = testSuiteMap.get(OUTCOME);
		
		
		doc.getRootElement().setAttribute(NAME, testSuiteName);
		doc.getRootElement().setAttribute(ELEMENT_FAILURE, failures);
		doc.getRootElement().setAttribute(ATTR_ERRORS, errors);
		doc.getRootElement().setAttribute(SKIPED, skipped);
		doc.getRootElement().setAttribute(ATTR_TESTS, tests);
		doc.getRootElement().setAttribute(PASSED, passed);

		org.jdom.Element testcase = new org.jdom.Element(TEST_CASE);
		testcase.setAttribute(ATTR_TIME, duration);
		testcase.setAttribute(ATTR_CLASSNAME, className);
		testcase.setAttribute(NAME, name);
		
		if (outcome.equalsIgnoreCase(FAILED)) {
			org.jdom.Element failure = new org.jdom.Element(ELEMENT_FAILURE);
			failure.addContent(errorMessage);
			testcase.addContent(failure);
		}

		doc.getRootElement().addContent(testcase);

		XMLOutputter output = new XMLOutputter();
		output.setFormat(Format.getPrettyFormat());
		File reportDir = new File(outputDir.getPath() + File.separator + REPORT + File.separator +  UNIT_TEST_REPORT);
		String fullPathNoEndSeparator = FilenameUtils.getFullPathNoEndSeparator(reportDir .getAbsolutePath());
		File fullPathNoEndSeparatorFile = new File(fullPathNoEndSeparator);
		fullPathNoEndSeparatorFile.mkdirs();
		output.output(doc, new FileWriter(reportDir.getAbsolutePath()));
	}
}
