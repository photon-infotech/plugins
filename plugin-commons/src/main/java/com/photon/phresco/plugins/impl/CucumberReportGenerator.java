package com.photon.phresco.plugins.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.UUID;

import com.photon.phresco.commons.FileListFilter;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;

public class CucumberReportGenerator implements PluginConstants {
	
	private static String testSuiteNameValue = TEST_SUITE;
	private int testsError = 0;
	private int testsSkip = 0;
	private long tduration = 0;
	private String testsTotal = null;
	private String testsErrors = null;
	private String testsSkipped = null;
	private String testsFailed = null;
	private float duration = 0;

	private File[] getTestResultFiles(String path, String name) {
		File testDir = new File(path);
		if (testDir.isDirectory()) {
			FilenameFilter filter = new FileListFilter(name, JSON);
			return testDir.listFiles(filter);
		}
		return null;
	}
	
	private JSONArray getAsJSONArray(File file) throws IOException,
			ParseException {
		FileReader reader = new FileReader(file.getPath());
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(reader);
		return (JSONArray) obj;
	}

	private String genarateFileName(JSONObject xmlNameObj) {
		String outputXmlName = (String) xmlNameObj.get(NAME);
		// Test it in Mac 
		outputXmlName=outputXmlName.replaceAll("[^a-zA-Z]+","");
		outputXmlName = outputXmlName + HYPEN + UUID.randomUUID();
		return outputXmlName;
	}

	private void setTestSuiteValue(JSONArray elementsArray) {
		for (Object object : elementsArray) {
			JSONObject elementObj = (JSONObject) object;
			JSONArray stepsArray = (JSONArray) elementObj.get(STEPS);
			testsTotal = Integer.toString(stepsArray.size());
			for (int i = 0; i < stepsArray.size(); i++) {
				JSONObject steptObj = (JSONObject) stepsArray.get(i);
				JSONObject resObj = (JSONObject) steptObj.get(RESULT);
				if (resObj.get(STATUS).equals(PASSED)) {
					tduration = tduration + (Long) resObj.get(DURATION);
				}
				if (resObj.get(STATUS).equals(FAILED)) {
					testsError = testsError + 1;
					tduration = tduration + (Long) resObj.get(DURATION);
				}
				if (resObj.get(STATUS).equals(SKIPED)) {
					testsSkip = testsSkip + 1;
				}
			}

			testsErrors = Integer.toString(testsError);
			testsSkipped = Integer.toString(testsSkip);
			testsFailed = Integer.toString(0);
			duration = (float) (tduration / 1000) % 60;
		}
	}
	
	private boolean isEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
	}

	public void generateReport(String fileLocation, String repDirName)throws PhrescoException {
		Document xmlDocument = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File xmlFilePath = null;
			File[] testResultFiles = getTestResultFiles(fileLocation, "");
			if(testResultFiles == null || testResultFiles.length == 0){
				throw new PhrescoException("JSON File Not Found in specific directory");
			}

			for (File file : testResultFiles) {
				/*
				 * Getting the data from Json file
				 */
				JSONArray jsonArray = getAsJSONArray(file);
				for (int j = 0; j < jsonArray.size(); j++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(j);
					String testSuiteName = (String) jsonObject.get(NAME);
					JSONArray xmlName = (JSONArray) jsonObject.get(TAGS);
					if(isEmpty(testSuiteName) && xmlName.isEmpty()){
						continue;
					}
					/*
					 * Generate File Name
					 */
					JSONObject xmlNameObj = (JSONObject) xmlName.get(1);
					String xmlFileName = genarateFileName(xmlNameObj);
					xmlFilePath = new File(repDirName + "/TEST-" + xmlFileName+".xml");
					JSONArray elementsArray = (JSONArray) jsonObject.get(ELEMENTS);
					setTestSuiteValue(elementsArray);

					/*
					 * Creating the XML document
					 */
					xmlDocument = builder.newDocument();
					PluginUtils putil=new PluginUtils();
					Element testSuite = xmlDocument.createElement(TEST_SUITE);
					xmlDocument.appendChild(testSuite);
					xmlDocument=putil.xmlParser(xmlDocument, builder,testSuite, testsErrors, testsFailed, testsTotal, testsSkipped,duration, testSuiteName);

					/*
					 * TestCase Value
					 */
					for (Object object : elementsArray) {
						JSONObject elementobj = (JSONObject) object;
						JSONArray stepsarray = (JSONArray) elementobj.get(STEPS);
					for (int i = 0; i < stepsarray.size(); i++) {
						JSONObject steptobj = (JSONObject) stepsarray.get(i);
						JSONObject resobj = (JSONObject) steptobj.get(RESULT);
						if (resobj.get(STATUS).equals(PASSED)) {
							Element testCase = xmlDocument.createElement(TEST_CASE);
							testSuite.appendChild(testCase);
							
							Attr testCaseClassName = xmlDocument.createAttribute(CLASS_NAME);
							testCaseClassName.setValue((String) elementobj.get(KEYWORD));
							testCase.setAttributeNode(testCaseClassName);
							
							Attr testCaseName = xmlDocument.createAttribute(NAME);
							testCaseName.setValue((String) steptobj.get(NAME));
							testCase.setAttributeNode(testCaseName);
							
							Attr testCaseTime = xmlDocument.createAttribute(ATTR_TIME);
							long testDuration = (Long) resobj.get(DURATION) / 1000 % 60;
							testCaseTime.setValue("" + testDuration);
							testCase.setAttributeNode(testCaseTime);
						}
						if (resobj.get(STATUS).equals(FAILED)) {
							Element testCase = xmlDocument.createElement(TEST_CASE);
							testSuite.appendChild(testCase);
							Element failure = xmlDocument.createElement(ELEMENT_FAILURE);
							testCase.appendChild(failure);

							Attr failureType = xmlDocument.createAttribute(ATTR_TYPE);
							failureType.setValue(SCENERIOFAILURE);
							failure.setAttributeNode(failureType);

							Node failureMsg = xmlDocument.createTextNode((String) resobj.get(ERRORMESSAGE));
							failure.appendChild(failureMsg);
							
							Attr testCaseClassName = xmlDocument.createAttribute(CLASS_NAME);
							testCaseClassName.setValue((String) elementobj.get(KEYWORD));
							testCase.setAttributeNode(testCaseClassName);
							
							Attr testCaseName = xmlDocument.createAttribute(NAME);
							testCaseName.setValue((String) steptobj.get(NAME));
							testCase.setAttributeNode(testCaseName);
							
							Attr testCaseTime = xmlDocument.createAttribute(ATTR_TIME);
							long testDuration = (Long) resobj.get(DURATION) / 1000 % 60;
							testCaseTime.setValue("" + testDuration);
							testCase.setAttributeNode(testCaseTime);
						}
						if (resobj.get(STATUS).equals(SKIPED)) {
							Element testCase = xmlDocument.createElement(TEST_CASE);
							testSuite.appendChild(testCase);
							
							Attr testCaseClassName = xmlDocument.createAttribute(CLASS_NAME);
							testCaseClassName.setValue((String) elementobj.get(KEYWORD));
							testCase.setAttributeNode(testCaseClassName);
							
							Attr testCaseName = xmlDocument.createAttribute(NAME);
							testCaseName.setValue((String) steptobj.get(NAME));
							testCase.setAttributeNode(testCaseName);
							
							Attr testCaseTime = xmlDocument.createAttribute(ATTR_TIME);
							long testDuration = 0;
							testCaseTime.setValue("" + testDuration);
							testCase.setAttributeNode(testCaseTime);
						}

					 }
					}

					/*
					 * Writing the data to XML file
					 */
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(xmlDocument);
					File xmlfileloc = new File(xmlFilePath.getParent());
					xmlfileloc.mkdirs();
					StreamResult result = new StreamResult(xmlFilePath);
					transformer.transform(source, result);
				}
			}
		} catch (ParserConfigurationException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (TransformerException e) {
			throw new PhrescoException(e);
		} catch (org.json.simple.parser.ParseException e) {
			throw new PhrescoException(e);
		}
	}

}