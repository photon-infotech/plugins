package com.photon.phresco.plugins.dotnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.photon.phresco.commons.FileListFilter;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.util.PomProcessor;

public class DotnetTest implements PluginConstants {
	private File baseDir;
	private MavenProject project;
	private File workingDirectory;
	private String subModule = "";
	private File pomFile;
	private String dotPhrescoDirName;
	private File dotPhrescoDir;
	private File srcDirectory;
	public static String dateFormatString = "yyyy-MM-dd'T'hh:mm:ss.SSS";
	public static String testSuiteNameValue = "TestSuite";

	public void runTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			baseDir = mavenProjectInfo.getBaseDir();
			if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
				subModule = mavenProjectInfo.getModuleName();
			}
			project = mavenProjectInfo.getProject();
			workingDirectory = new File(baseDir.getPath() + File.separator
					+ subModule);
			PluginUtils pluginUtils = new PluginUtils();
			dotPhrescoDirName = project.getProperties().getProperty(
					Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
			dotPhrescoDir = baseDir;
			if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
				dotPhrescoDir = new File(baseDir.getParent() + File.separator
						+ dotPhrescoDirName);
			}
			dotPhrescoDir = new File(dotPhrescoDir.getPath()
					+ File.separatorChar + subModule);
			pomFile = pluginUtils.getPomFile(dotPhrescoDir, workingDirectory);
			File splitProjectDirectory = pluginUtils.getSplitProjectSrcDir(
					pomFile, dotPhrescoDir, subModule);
			srcDirectory = workingDirectory;
			if (splitProjectDirectory != null) {
				srcDirectory = splitProjectDirectory;
			}
			PomProcessor processor = new PomProcessor(new File(
					workingDirectory, pomFile.getName()));
			String dllFileName = processor.getProperty("phresco.unittest.dll");
			String unitExecuteDir = processor
					.getProperty("phresco.unitTest.execute.dir");
			String unitTargetDir = processor.getProperty("phresco.unitTest.report.dir");
			File targetDir = new File(srcDirectory + unitExecuteDir + File.separator
					+ "TestResults");
			if (targetDir.exists()) {
				FileUtils.deleteDirectory(targetDir);
			}
			executeTest(dllFileName, srcDirectory, new File(
					srcDirectory + unitExecuteDir), new File(srcDirectory + unitTargetDir), targetDir);
		} catch (Exception e) {
			
			throw new PhrescoException(e);
		}
	}

	private void executeTest(String dllfilename, 
			File srcDirectory, File workingDirectory, File unitTargetDir, File targetDir)
			throws PhrescoException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("mstest");
			sb.append(STR_SPACE);
			sb.append("/testcontainer:");
			sb.append(dllfilename);
			sb.append(STR_SPACE);
			sb.append("/detail:testtype");
			System.out.println("COMMAND IS  " + sb.toString());
			boolean status = Utility.executeStreamconsumer(sb.toString(),
					workingDirectory.getPath(), srcDirectory.getPath(), UNIT);
//			if (!status) {
//				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
//			}
			convertTrxtoXml(targetDir, unitTargetDir);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void convertTrxtoXml(File targetDir, File unitTargetDir) throws PhrescoException {
		try {
			File outputDirectory = new File(unitTargetDir + File.separator
					+ "results.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			/*
			 * Getting the data from MSTest TRX file
			 */
			File[] testResultFiles = getTestResultFiles(targetDir.getPath(), "");
			for (File trxFilePath : testResultFiles) {
			Document trxDocument = builder.parse(new FileInputStream(trxFilePath));

			Node counters = trxDocument.getElementsByTagName("Counters")
					.item(0);
			String testsTotal = getAttribute("total", counters);
			String testsErrors = getAttribute("error", counters);
			String testsFailed = getAttribute("failed", counters);
			String testsSkipped = getAttribute("inconclusive", counters);
			String testSuiteName = testSuiteNameValue;

			Node times = trxDocument.getElementsByTagName("Times").item(0);
			String testsStartTime = getAttribute("start", times);
			String testsFinishTime = getAttribute("finish", times);
			float duration = (float) getDuration(testsStartTime,
					testsFinishTime) / 1000;

			NodeList unitTestResults = trxDocument
					.getElementsByTagName("UnitTestResult");
			NodeList testMethods = trxDocument
					.getElementsByTagName("TestMethod");

			/*
			 * Creating the XML document
			 */
			Document xmlDocument = builder.newDocument();

			// testsuite
			Element testSuite = xmlDocument.createElement("testsuite");
			xmlDocument.appendChild(testSuite);

			Attr testSuiteErrors = xmlDocument.createAttribute("errors");
			testSuiteErrors.setValue(testsErrors);
			testSuite.setAttributeNode(testSuiteErrors);

			Attr testSuiteFailures = xmlDocument.createAttribute("failures");
			testSuiteFailures.setValue(testsFailed);
			testSuite.setAttributeNode(testSuiteFailures);

			Attr testSuiteTests = xmlDocument.createAttribute("tests");
			testSuiteTests.setValue(testsTotal);
			testSuite.setAttributeNode(testSuiteTests);

			Attr testSuiteSkipped = xmlDocument.createAttribute("skipped");
			testSuiteSkipped.setValue(testsSkipped);
			testSuite.setAttributeNode(testSuiteSkipped);

			Attr testSuiteTime = xmlDocument.createAttribute("time");
			testSuiteTime.setValue("" + duration);
			testSuite.setAttributeNode(testSuiteTime);

			Attr testSuiteNameAttr = xmlDocument.createAttribute("name");
			testSuiteNameAttr.setValue(testSuiteName);
			testSuite.setAttributeNode(testSuiteNameAttr);

			// testcase
			for (int i = 0; i < unitTestResults.getLength(); i++) {
				Node node = unitTestResults.item(i);
				Element testCase = xmlDocument.createElement("testcase");
				testSuite.appendChild(testCase);

				// Getting the className for this particular testcase
				String testName = getAttribute("testName", node);
				Attr testCaseClassName = xmlDocument
						.createAttribute("classname");
				for (int j = 0; j < testMethods.getLength(); j++) {
					Node testMethod = testMethods.item(j);
					String testMethodName = getAttribute("name", testMethod);
					if (testMethodName.equals(testName)) {
						String className = getAttribute("className", testMethod);
						testCaseClassName.setValue(className.split(",")[0]);
						break;
					}
				}
				testCase.setAttributeNode(testCaseClassName);

				Attr testCaseName = xmlDocument.createAttribute("name");
				testCaseName.setValue(testName);
				testCase.setAttributeNode(testCaseName);

				// Testcase time
				Attr testCaseTime = xmlDocument.createAttribute("time");
				String testCaseStartTime = getAttribute("startTime", node);
				String testCaseEndTime = getAttribute("endTime", node);
				long testDuration = getDuration(testCaseStartTime,
						testCaseEndTime);
				testCaseTime.setValue("" + testDuration);
				testCase.setAttributeNode(testCaseTime);

				// Outcome - Adding failure details
				String outcome = getAttribute("outcome", node);
				if (!outcome.equals("Passed")) {
					Element failure = xmlDocument.createElement("failure");
					testCase.appendChild(failure);

					Attr failureType = xmlDocument.createAttribute("type");
					failureType.setValue(outcome);
					failure.setAttributeNode(failureType);

					Node output = getNode("Output", node.getChildNodes());
					Node errorInfo = getNode("ErrorInfo",
							output.getChildNodes());
					Node messageNode = getNode("Message",
							errorInfo.getChildNodes());

					failure.appendChild(xmlDocument.createTextNode(messageNode
							.getTextContent()));
				}
			}

			/*
			 * Writing the data to XML file
			 */
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(xmlDocument);
			StreamResult result = new StreamResult(outputDirectory);
			transformer.transform(source, result);
			}
		} catch (ParserConfigurationException e) {
			throw new PhrescoException(e);
		} catch (SAXException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (TransformerException e) {
			throw new PhrescoException(e);
		} catch (ParseException e) {
			throw new PhrescoException(e);
		}
	}

	/*
	 * Gets a particular Node
	 */
	protected static Node getNode(String tagName, NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeName().equalsIgnoreCase(tagName)) {
				return node;
			}
		}

		return null;
	}

	/*
	 * Gets a Node attribute
	 */
	protected static String getAttribute(String attribute, Node node) {
		return node.getAttributes().getNamedItem(attribute).getNodeValue();
	}

	/*
	 * Gets the time duration in milliseconds
	 */
	protected static long getDuration(String startTimeString,
			String endTimeString) throws ParseException {
		// Extracting milliseconds till 3 decimal places
		startTimeString = startTimeString.substring(0,
				startTimeString.length() - 10);
		endTimeString = endTimeString.substring(0, endTimeString.length() - 10);

		SimpleDateFormat format = new SimpleDateFormat(dateFormatString);
		long startTime = format.parse(startTimeString).getTime();
		long finishTime = format.parse(endTimeString).getTime();
		return (finishTime - startTime);
	}

	private File[] getTestResultFiles(String path, String name) {
		File testDir = new File(path);
		if (testDir.isDirectory()) {
			FilenameFilter filter = new FileListFilter(name, "trx");
			return testDir.listFiles(filter);
		}
		return null;
	}

}