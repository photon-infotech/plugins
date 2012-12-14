/*
 * ###
 * Phresco Commons
 * 
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ###
 */
package com.photon.phresco.plugin.commons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.configuration.ConfigReader;
import com.photon.phresco.configuration.ConfigWriter;
import com.photon.phresco.configuration.Configuration;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.filter.FileListFilter;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.HubConfiguration;
import com.photon.phresco.util.NodeConfiguration;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class PluginUtils {
	
	private String host = null;
	private String port = null;
	private String protocol = null;
	private String serverContext = null;

	public void executeUtil(String environmentType, String basedir, File sourceConfigXML) {
		try {
			File currentDirectory = new File(".");
			File configXML = new File(currentDirectory + File.separator + 
			PluginConstants.DOT_PHRESCO_FOLDER + File.separator + PluginConstants.CONFIG_FILE);
			File settingsXML = new File(Utility.getProjectHome() + PluginConstants.SETTINGS_FILE);
			
			ConfigReader reader = new ConfigReader(configXML);
			ConfigWriter writer = new ConfigWriter(reader, true);
			writer.saveXml(sourceConfigXML, environmentType);
			if (settingsXML.exists()) {
				ConfigReader srcReaderToAppend = new ConfigReader(sourceConfigXML);
				
				ConfigReader globalReader = new ConfigReader(settingsXML);
				ConfigWriter globalWriter = new ConfigWriter(globalReader, true);
				globalWriter.saveXml(srcReaderToAppend, environmentType);
			}
		} catch (Exception e) {
			//FIXME : log the errors
		}
	}

	public List<String> csvToList(String csvString) {
		List<String> envs = new ArrayList<String>();
		if (StringUtils.isNotEmpty(csvString)) {
			String[] temp = csvString.split(",");
			for (int i = 0; i < temp.length; i++) {
				envs.add(temp[i]);
			}
		}
		return envs;
	}
	
	public void encode(File configFile) throws PhrescoException {
		try {
			String fileToString = FileUtils.fileRead(configFile);
			String content = Base64.encodeBase64String(fileToString.getBytes());
//			FileUtils.fileWrite(configFile, content);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
	}
	public void encryptConfigFile(String fileName) throws PhrescoException {
		InputStream inputStream = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			inputStream = new FileInputStream(new File(fileName));
			Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
			StringWriter stw = new StringWriter();
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.transform(new DOMSource(doc), new StreamResult(stw));
			EncryptString encryptstring = new EncryptString();
			encryptstring.Crypto("D4:6E:AC:3F:F0:BE");
			String encryptXmlString = encryptstring.encrypt(stw.toString());
			writeXml(encryptXmlString, fileName);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}  finally {
			Utility.closeStream(inputStream);
		}
	}
	
	private void writeXml(String encrStr, String fileName) throws PhrescoException  {
		DataOutputStream dos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName);
			dos = new DataOutputStream(fos);
			dos.writeBytes(encrStr);
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			Utility.closeStream(dos);
			Utility.closeStream(fos);
		}
	}
	
	public void setDefaultEnvironment(String environmentName, File sourceConfigXML) throws PhrescoException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(false);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(sourceConfigXML);
			NodeList environmentList = doc.getElementsByTagName("environment");
			for (int i = 0; i < environmentList.getLength(); i++) {
				Element environment = (Element) environmentList.item(i);
				String envName = environment.getAttribute("name");
				String[] envs = environmentName.split(",");
				for (String envsName : envs) {
					if (envsName.equals(envName)) {
						environment.setAttribute("default", "true");
						// write the content into xml file
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult(sourceConfigXML.toURI().getPath());
						transformer.transform(source, result);
					}
				}
			}

		} catch (ParserConfigurationException e) {
			throw new PhrescoException(e);
		} catch (TransformerException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (SAXException e) {
			throw new PhrescoException(e);
		}
	}

	public BuildInfo getBuildInfo(int buildNumber) throws MojoExecutionException {
		File currentDirectory = new File(".");
		File buildInfoFile = new File(currentDirectory.getPath() + PluginConstants.BUILD_DIRECTORY + PluginConstants.BUILD_INFO_FILE);
		if (!buildInfoFile.exists()) {
			throw new MojoExecutionException("Build info is not available!");
		}
		try {
			List<BuildInfo> buildInfos = getBuildInfo(buildInfoFile);
			
			 if (CollectionUtils.isEmpty(buildInfos)) {
				 throw new MojoExecutionException("Build info is empty!");
			 }

			 for (BuildInfo buildInfo : buildInfos) {
				 if (buildInfo.getBuildNo() == buildNumber) {
					 return buildInfo;
				 }
			 }

			 throw new MojoExecutionException("Build info is empty!");
		} catch (Exception e) {
			throw new MojoExecutionException(e.getLocalizedMessage());
		}
	}
	
	 public List<BuildInfo> getBuildInfo(File path) throws IOException {
		 if (!path.exists()) {
			 return new ArrayList<BuildInfo>(1);
		 }

		 BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
		 Gson gson = new Gson();
		 Type type = new TypeToken<List<BuildInfo>>(){}.getType();

		 List<BuildInfo> buildInfos = gson.fromJson(bufferedReader, type);
		 Collections.sort(buildInfos, new BuildInfoComparator());
		 bufferedReader.close();

		 return buildInfos;
	 }
	 
	 public void changeTestName(String performancePath, String testName) throws Exception {
			File buildPathXml = new File(performancePath + PluginConstants.BUILD_XML_FILE);
			Document document = getDocument(buildPathXml);
			String fileNameNode = "project/target[@name='init']/property[@name='jmeter.result.file']";
			NodeList nodelist = org.apache.xpath.XPathAPI.selectNodeList(document, fileNameNode);
			if (nodelist != null && nodelist.getLength() > 0) {
				Node stringProp = nodelist.item(0);
				NamedNodeMap attributes = stringProp.getAttributes();
				Node valueAttr = attributes.getNamedItem("value");
				String valueAttrTxt = valueAttr.getTextContent();
				valueAttr.setTextContent(valueAttrTxt.substring(0, valueAttrTxt.indexOf("/") + 1).concat(testName + ".xml"));
			}
			saveDocument(buildPathXml, document);
		}
	 
	 private Document getDocument(File file) throws PhrescoException {
			try {
				InputStream fis = null;
				DocumentBuilder builder = null;
				try {
				    fis = new FileInputStream(file);
				    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
				    domFactory.setNamespaceAware(false);
				    builder = domFactory.newDocumentBuilder();
				    Document doc = builder.parse(fis);
				    return doc;
				    
				} finally {
					if(fis != null) {
						fis.close();
				    }
				}
			} catch (FileNotFoundException e) {
				throw new PhrescoException(e);
			} catch (ParserConfigurationException e) {
				throw new PhrescoException(e);
			} catch (SAXException e) {
				throw new PhrescoException(e);
			} catch (IOException e) {
				throw new PhrescoException(e);
			}
		}
	 
	 public void adaptTestConfig(String testDirPath, Configuration configuration) throws PhrescoException {
			FileWriter out = null;
			try {
				File configFile = new File(testDirPath + PluginConstants.LOAD_TEST_CONFIG_FILE);
				if (!configFile.exists()) {
					return;
				}
				out = new FileWriter(configFile);

				getConfiguration(configuration);

				out.write(host + Constants.COMMA);
				out.write(port + Constants.COMMA);
				out.write(protocol + Constants.COMMA);
				out.write(serverContext);
				out.flush();
			} catch (IOException e) {
				throw new PhrescoException(e);
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
		}
	 
	 public void getConfiguration(Configuration configuration) {
			String type = configuration.getType();
			if (type.equalsIgnoreCase("Server")) {
				host = configuration.getProperties().getProperty(Constants.SERVER_HOST);
				port = configuration.getProperties().getProperty(Constants.SERVER_PORT);
				protocol = configuration.getProperties().getProperty(Constants.SERVER_PROTOCOL);
				serverContext = configuration.getProperties().getProperty(Constants.SERVER_CONTEXT);
			}

			if (type.equalsIgnoreCase("WebService")) {
				host = configuration.getProperties().getProperty(Constants.WEB_SERVICE_HOST);
				port = configuration.getProperties().getProperty(Constants.WEB_SERVICE_PORT);
				protocol = configuration.getProperties().getProperty(Constants.WEB_SERVICE_PROTOCOL);
				serverContext = configuration.getProperties().getProperty(Constants.WEB_SERVICE_CONTEXT);
			}

			if (type.equalsIgnoreCase("Database")) {
				host = configuration.getProperties().getProperty(Constants.DB_HOST);
				port = configuration.getProperties().getProperty(Constants.DB_PORT);
				protocol = configuration.getProperties().getProperty(Constants.DB_PROTOCOL);
				serverContext = "/";
			}
		}
	 
	 public void adaptLoadJmx(String jmxFileLocation, int noOfUsers, int rampUpPeriod, int loopCount, Map<String, String> headersMap) throws Exception {
	        File jmxFile = null;
	        File jmxDir = new File(jmxFileLocation);
	        if(jmxDir.isDirectory()){
	            FilenameFilter filter = new FileListFilter("", "jmx");
	            File[] jmxFiles = jmxDir.listFiles(filter);
	            jmxFile = jmxFiles[0];
	        }
	        Document document = getDocument(jmxFile);
	        appendThreadProperties(document, noOfUsers, rampUpPeriod, loopCount);
	        if (MapUtils.isNotEmpty(headersMap)) {
	        	NodeList hashTree = org.apache.xpath.XPathAPI.selectNodeList(document, "jmeterTestPlan/hashTree/hashTree/hashTree");
				NodeList headerMngrNodelist = org.apache.xpath.XPathAPI.selectNodeList(document, "jmeterTestPlan/hashTree/hashTree/hashTree/HeaderManager/collectionProp");
				if (headerMngrNodelist != null && headerMngrNodelist.getLength() > 0) {
					createHeaderElementProp(document, headersMap, headerMngrNodelist.item(0));
				} else {
					Node appendHeaderManager = appendHeaderManager(document, headersMap);
					hashTree.item(0).appendChild(appendHeaderManager);
					hashTree.item(0).appendChild(document.createElement("hashTree"));
				}
			}
	        saveDocument(jmxFile, document);
	    }
	 
	 private static void appendThreadProperties(Document document, int noOfUsers, int rampUpPeriod, int loopCount) throws Exception {
			String loopNode = "jmeterTestPlan/hashTree/hashTree/ThreadGroup/*/stringProp[@name='LoopController.loops']";
			String threadNode = "jmeterTestPlan/hashTree/hashTree/ThreadGroup/stringProp[@name='ThreadGroup.num_threads']";
			String rampNode = "jmeterTestPlan/hashTree/hashTree/ThreadGroup/stringProp[@name='ThreadGroup.ramp_time']";
			appendTextContent(document, loopNode, ""+loopCount);
			appendTextContent(document, threadNode, ""+noOfUsers);
			appendTextContent(document, rampNode, ""+rampUpPeriod);
		}
	 
	 private static void appendTextContent(Document document, String element, String textContent) throws Exception {
			NodeList nodelist = org.apache.xpath.XPathAPI.selectNodeList(document, element);

			for (int i = 0; i < nodelist.getLength(); i++) {
				Node stringProp = nodelist.item(i);
				stringProp.setTextContent(textContent);
			}
		}
	 
	 private static void createHeaderElementProp(Document document,
			 Map<String, String> headersMap, Node collectionProp) {
		 for (Map.Entry<String, String> entry : headersMap.entrySet()) {
			 Node subElementProp = document.createElement("elementProp");
			 NamedNodeMap subElementAttributes = subElementProp.getAttributes();
			 subElementAttributes.setNamedItem(createAttribute(document, "name", ""));
			 subElementAttributes.setNamedItem(createAttribute(document, "elementType", "Header"));
			 collectionProp.appendChild(subElementProp);
			 appendTypeProp(document, subElementProp, "stringProp", "Header.name", entry.getKey());
			 appendTypeProp(document, subElementProp, "stringProp", "Header.value", entry.getValue());
		 }
	 }

	 private static Attr createAttribute(Document document, String attrName, String attrValue) {
		 Attr attr = document.createAttribute(attrName);
		 attr.setValue(attrValue);
		 return attr;
	 }

	 private static void appendTypeProp(Document document, Node parentProp, String tag, String nameAttr, String textContent) {
		 Node typeProp = document.createElement(tag);
		 NamedNodeMap attributes = typeProp.getAttributes();
		 attributes.setNamedItem(createAttribute(document, "name", nameAttr));
		 typeProp.setTextContent(textContent);
		 parentProp.appendChild(typeProp);
	 }
	 
	 private static Node appendHeaderManager(Document document, Map<String, String> headersMap) {
		 Node headerManager = document.createElement("HeaderManager");
		 NamedNodeMap attributes = headerManager.getAttributes();
		 attributes.setNamedItem(createAttribute(document, "guiclass", "HeaderPanel"));
		 attributes.setNamedItem(createAttribute(document, "testclass", "HeaderManager"));
		 attributes.setNamedItem(createAttribute(document, "testname", "HTTP Header Manager"));
		 attributes.setNamedItem(createAttribute(document, "enabled", "true"));
		 appendHeaderManagerCollectionProp(document, headerManager, headersMap);
		 return headerManager;
	 }
	 
	 private static void appendHeaderManagerCollectionProp(Document document, Node elementProp, Map<String, String> headersMap) {
			Node collectionProp = document.createElement("collectionProp");
			NamedNodeMap attributes = collectionProp.getAttributes();
			attributes.setNamedItem(createAttribute(document, "name", "HeaderManager.headers"));
			createHeaderElementProp(document, headersMap, collectionProp);
			elementProp.setTextContent(null);
			elementProp.appendChild(collectionProp);
		}
	 
	 private static void saveDocument(File file, Document doc) throws PhrescoException {

			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
				String content = writer.toString();
				FileWriter fileWriter = new FileWriter(file);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(content);
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (TransformerConfigurationException e) {
				throw new PhrescoException(e);
			} catch (IllegalArgumentException e) {
				throw new PhrescoException(e);
			} catch (TransformerFactoryConfigurationError e) {
				throw new PhrescoException(e);
			} catch (TransformerException e) {
				throw new PhrescoException(e);
			} catch (IOException e) {
				throw new PhrescoException(e);
			}
		}
	 
	public void updateHubConfigInfo(File baseDir, String funcDir, HubConfiguration hubConfig)	throws PhrescoException {
		BufferedWriter out = null;
		FileWriter fileWriter = null;
		try {
			File hubConfigFile = new File(baseDir + funcDir + File.separator + Constants.HUB_CONFIG_JSON);
			Gson gson = new Gson();
			String infoJSON = gson.toJson(hubConfig);
			fileWriter = new FileWriter(hubConfigFile);
			out = new BufferedWriter(fileWriter);
			out.write(infoJSON);
		} catch (UnknownHostException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			Utility.closeWriter(out);
			Utility.closeStream(fileWriter);
		}
	}

	public void updateNodeConfigInfo(File baseDir, String funcDir, NodeConfiguration nodeConfiguration) throws PhrescoException {
		BufferedWriter out = null;
		FileWriter fileWriter = null;
		try {
			File hubConfigFile = new File(baseDir + funcDir + File.separator + Constants.NODE_CONFIG_JSON);
			Gson gson = new Gson();
			String infoJSON = gson.toJson(nodeConfiguration);
			fileWriter = new FileWriter(hubConfigFile);
			out = new BufferedWriter(fileWriter);
			out.write(infoJSON);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			Utility.closeWriter(out);
			Utility.closeStream(fileWriter);
		}
	}

	public void startNode(File baseDir) throws PhrescoException {
		FileOutputStream fos = null;
		try {
			File LogDir = new File(baseDir + File.separator + Constants.DO_NOT_CHECKIN_DIRY + File.separator + Constants.LOG_DIRECTORY);
			if (!LogDir.exists()) {
				LogDir.mkdirs();
			}
			File pomPath = new File(baseDir + File.separator + Constants.POM_NAME);
            PomProcessor processor = new PomProcessor(pomPath);
            String functionalTestDir = processor.getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
            
			File logFile  = new File(LogDir + Constants.SLASH + Constants.NODE_LOG);
			StringBuilder sb = new StringBuilder()
			.append("java -Dwebdriver.chrome.driver=")
			.append(functionalTestDir.substring(1, functionalTestDir.length()))
			.append("/chromedriver/chromedriver.exe -jar ")
			.append(functionalTestDir.substring(1, functionalTestDir.length()))
			.append("/lib/selenium-server-standalone-2.26.0.jar")
			.append(" -role node -nodeConfig ")
			.append(functionalTestDir.substring(1, functionalTestDir.length()))
	        .append("/nodeconfig.json");
			fos = new FileOutputStream(logFile, false);
			Utility.executeStreamconsumer(sb.toString(), fos);
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		} catch (PhrescoPomException e) {
		    throw new PhrescoException(e);
        }
	}
	
	public void startHub(File baseDir) throws PhrescoException {
	    FileOutputStream fos = null;
	    try {
	        File pomPath = new File(baseDir + File.separator + Constants.POM_NAME);
            PomProcessor processor = new PomProcessor(pomPath);
            String functionalTestDir = processor.getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
            StringBuilder builder = new StringBuilder();
            builder.append(baseDir)
            .append(functionalTestDir);
            
	        executeValidatePhase(builder.toString());
	        File LogDir = new File(baseDir + File.separator + Constants.DO_NOT_CHECKIN_DIRY + File.separator + Constants.LOG_DIRECTORY);
	        if (!LogDir.exists()) {
	            LogDir.mkdirs();
	        }
	        File logFile  = new File(LogDir + Constants.SLASH + Constants.HUB_LOG);
	        StringBuilder sb = new StringBuilder()
	        .append("java -jar ")
	        .append(functionalTestDir.substring(1, functionalTestDir.length()))
	        .append("/lib/selenium-server-standalone-2.26.0.jar")
	        .append(" -role hub -hubConfig ")
	        .append(functionalTestDir.substring(1, functionalTestDir.length()))
	        .append("/hubconfig.json");
	        fos = new FileOutputStream(logFile, false);
	        Utility.executeStreamconsumer(sb.toString(), fos);
	    } catch (FileNotFoundException e) {
	        throw new PhrescoException(e);
	    } catch (PhrescoPomException e) {
	        throw new PhrescoException(e);
        }
	}
	
	private void executeValidatePhase(String workingDir) throws PhrescoException {
	    try {
	        BufferedReader breader = Utility.executeCommand("mvn validate", workingDir);
	        String line = null;
	        while ((line = breader.readLine()) != null) {
	            if (line.startsWith("[ERROR]")) {
	                System.out.println(line);
	            }
	        }
	    } catch (IOException e) {
	        throw new PhrescoException(e);
	    }
	}
	
	public void stopServer(String portNo, File baseDir) throws PhrescoException {
		if (System.getProperty(Constants.OS_NAME).startsWith(Constants.WINDOWS_PLATFORM)) {
			stopJavaServerInWindows("netstat -ao | findstr " + portNo + " | findstr LISTENING", baseDir);
		} else if (System.getProperty(Constants.OS_NAME).startsWith("Mac")) {
			stopJavaServer("lsof -i tcp:" + portNo + " | awk '{print $2}'", baseDir);
		} else {
			stopJavaServer("fuser " + portNo + "/tcp " + "|" + "awk '{print $1}'", baseDir);
		}
	}

	private void stopJavaServerInWindows(String command, File baseDir) throws PhrescoException {
		BufferedReader bufferedReader = null;
		try {
			String pid = "";
			bufferedReader = Utility.executeCommand(command, baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				pid = line.substring(line.length() - 4, line.length());
			}
			Runtime.getRuntime().exec("cmd /X /C taskkill /F /PID " + pid);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}

	private void stopJavaServer(String command, File baseDir) throws PhrescoException {
		BufferedReader bufferedReader = null;
		try {
			String pid = "";
			bufferedReader = Utility.executeCommand(command, baseDir.getPath());
			String line = null;
			int count = 1;
			while ((line = bufferedReader.readLine()) != null) {
				if (count == 2) {
					pid = line.trim();
				}
				count++;
			}
			Runtime.getRuntime().exec(Constants.JAVA_UNIX_PROCESS_KILL_CMD + pid);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}
}
