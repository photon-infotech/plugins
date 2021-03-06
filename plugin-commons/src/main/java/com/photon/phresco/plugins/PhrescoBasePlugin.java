/**
 * Phresco Plugin Commons
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.photon.phresco.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.gson.Gson;
import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.commons.model.TechnologyInfo;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.model.ContextUrls;
import com.photon.phresco.framework.model.DbContextUrls;
import com.photon.phresco.framework.model.PerformanceDetails;
import com.photon.phresco.impl.ConfigManagerImpl;
import com.photon.phresco.plugin.commons.DatabaseUtil;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.AbstractPhrescoPlugin;
import com.photon.phresco.plugins.impl.CucumberReportGenerator;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.ProjectUtils;
import com.photon.phresco.util.TechnologyTypes;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Plugin;
import com.phresco.pom.util.PomProcessor;

public class PhrescoBasePlugin extends AbstractPhrescoPlugin implements PluginConstants {
	
	private String buildVersion;
	
	public Log log;

	public PhrescoBasePlugin(Log log) {
		this.log = log;
	}

	protected final Log getLog() {
		return log;
	}

	public ExecutionStatus runUnitTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		buildVersion = mavenProjectInfo.getBuildVersion();
		File baseDir = mavenProjectInfo.getBaseDir();
		MavenProject project = mavenProjectInfo.getProject();
		String reportDir = "";
		String subModule = "";
		String workingDirectory = "";
		PluginUtils pluginUtils = new PluginUtils();
		try {
			if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
				subModule = mavenProjectInfo.getModuleName();
			}
			String dotPhrescoDirName = project.getProperties().getProperty(
					Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
			File dotPhrescoDir = baseDir;
			if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
				dotPhrescoDir = new File(baseDir.getParent()
						+ File.separatorChar + dotPhrescoDirName);
			}
			if (StringUtils.isNotEmpty(dotPhrescoDirName)
					&& StringUtils.isNotEmpty(subModule)) {
				dotPhrescoDir = new File(baseDir.getParentFile().getPath()
						+ File.separatorChar + dotPhrescoDirName);
			}
			dotPhrescoDir = new File(dotPhrescoDir.getPath()
					+ File.separatorChar + subModule);
			File pomFile = pluginUtils.getPomFile(dotPhrescoDir, new File(
					baseDir.getPath() + File.separatorChar + subModule));
			project.setFile(pomFile);
			PomProcessor processor = new PomProcessor(pomFile);
			if (StringUtils.isNotEmpty(processor
					.getProperty(Constants.POM_PROP_KEY_UNITTEST_DIR))) {
				workingDirectory = processor
				.getProperty(Constants.POM_PROP_KEY_UNITTEST_DIR);
				ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
				String appDirName = appInfo.getAppDirName();
				String testDirName = processor
				.getProperty(Constants.POM_PROP_KEY_SPLIT_TEST_DIR);
				String srcDirName = processor
				.getProperty(Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
				if (StringUtils.isNotEmpty(testDirName)) {
					baseDir = new File(Utility.getProjectHome()
							+ File.separatorChar + appDirName
							+ File.separatorChar + testDirName);
				} else if (StringUtils.isNotEmpty(srcDirName)) {
					baseDir = new File(Utility.getProjectHome()
							+ File.separatorChar + appDirName
							+ File.separatorChar + srcDirName);
				}
			}
			baseDir = new File(baseDir.getPath() + File.separatorChar
					+ subModule);
			reportDir = processor
			.getProperty(Constants.POM_PROP_KEY_UNITTEST_RPT_DIR);
			workingDirectory = File.separator + workingDirectory;
			reportDir = File.separator + subModule + File.separator + reportDir;
			File reportLoc = new File(baseDir.getPath() + File.separatorChar
					+ reportDir);
			if (reportLoc.exists()) {
				pluginUtils.delete(reportLoc);
			}
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
		generateMavenCommand(mavenProjectInfo, baseDir.getPath()
				+ workingDirectory, UNIT);

		return new DefaultExecutionStatus();
	}

	public ExecutionStatus runComponentTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			buildVersion = mavenProjectInfo.getBuildVersion();
			String subModule = "";
			File baseDir = mavenProjectInfo.getBaseDir();
			MavenProject project = mavenProjectInfo.getProject();
			PluginUtils pluginUtils = new PluginUtils();
			File workingDirectory = baseDir;
			if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
				subModule = mavenProjectInfo.getModuleName();
				workingDirectory = new File(workingDirectory + File.separator
						+ subModule);
			}
			String dotPhrescoDirName = project.getProperties().getProperty(
					Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
			File dotPhrescoDir = baseDir;
			if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
				dotPhrescoDir = new File(baseDir.getParent()
						+ File.separatorChar + dotPhrescoDirName);
			}
			dotPhrescoDir = new File(dotPhrescoDir.getPath()
					+ File.separatorChar + subModule);
			File pomFile = pluginUtils.getPomFile(dotPhrescoDir,
					workingDirectory);
			PomProcessor processor = new PomProcessor(pomFile);
			String testDirName = processor
			.getProperty(Constants.POM_PROP_KEY_SPLIT_TEST_DIR);
			String srcDirName = processor
			.getProperty(Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
			ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
			String appDirName = appInfo.getAppDirName();
			File testDir = workingDirectory;
			if (StringUtils.isNotEmpty(testDirName)) {
				testDir = new File(Utility.getProjectHome()
						+ File.separatorChar + appDirName + File.separatorChar
						+ testDirName + File.separatorChar + subModule);
			} else if (StringUtils.isNotEmpty(srcDirName)) {
				testDir = new File(Utility.getProjectHome()
						+ File.separatorChar + appDirName + File.separatorChar
						+ srcDirName + File.separatorChar + subModule);
			}
			String componentTestDir = processor
			.getProperty(Constants.POM_PROP_KEY_COMPONENTTEST_DIR);
			if (StringUtils.isEmpty(componentTestDir)) {
				componentTestDir = "";
			}
			if (configuration != null) {
				Map<String, String> configs = MojoUtil
				.getAllValues(configuration);
				String environmentName = configs.get(ENVIRONMENT_NAME);
				String configXmlFile = processor
				.getProperty(Constants.POM_PROP_KEY_COMPONENTTEST_ADAPT_CONFIG);
				File configXMLFile = new File(testDir.getPath() + configXmlFile);
				pluginUtils.executeUtil(environmentName,
						dotPhrescoDir.getPath(), configXMLFile);
			}
			generateMavenCommand(mavenProjectInfo, testDir.getPath()
					+ componentTestDir, COMPONENT);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}

		return new DefaultExecutionStatus();
	}

	public ExecutionStatus runFunctionalTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			MavenProject project = mavenProjectInfo.getProject();
			String subModule = "";
			File baseDir = mavenProjectInfo.getBaseDir();
			PluginUtils pluginUtils = new PluginUtils();
			File workingDirectory = baseDir;
			if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
				subModule = mavenProjectInfo.getModuleName();
				workingDirectory = new File(workingDirectory + File.separator
						+ subModule);
			}
			File dotPhrescoDir = baseDir;
			String dotPhrescoDirName = project.getProperties().getProperty(
					Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
			if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
				dotPhrescoDir = new File(baseDir.getParent()
						+ File.separatorChar + dotPhrescoDirName);
			}
			if (StringUtils.isNotEmpty(dotPhrescoDirName)
					&& StringUtils.isNotEmpty(subModule)) {
				dotPhrescoDir = new File(dotPhrescoDir.getParentFile()
						.getPath() + File.separatorChar + subModule + File.separatorChar + dotPhrescoDirName);
			}
			
//			dotPhrescoDir = new File(dotPhrescoDir.getPath()
//					+ File.separatorChar + subModule);
			File pomFile = pluginUtils.getPomFile(dotPhrescoDir,
					workingDirectory);
			PomProcessor processor = new PomProcessor(pomFile);
			ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
			String appDirName = appInfo.getAppDirName();
			Map<String, String> configValues = MojoUtil
			.getAllValues(configuration);
			String seleniumToolType = processor
					.getProperty(Constants.POM_PROP_KEY_FUNCTEST_SELENIUM_TOOL);
			String environmentName = configValues.get(ENVIRONMENT_NAME);
			
			String testAgainst = configValues.get(TEST_AGAINST);
			String functionalTestDir = processor
			.getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
			String funTestDirName = processor
			.getProperty(Constants.POM_PROP_KEY_SPLIT_TEST_DIR);
			String srcDirName = processor
			.getProperty(Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
			String repDirName = processor
					.getProperty(Constants.POM_PROP_KEY_FUNCTEST_RPT_DIR);
			File funTestDir = workingDirectory;
			if (StringUtils.isNotEmpty(funTestDirName)) {
				funTestDir = new File(Utility.getProjectHome()
						+ File.separatorChar + appDirName + File.separatorChar
						+ funTestDirName + File.separatorChar + subModule);
			} else if (StringUtils.isNotEmpty(srcDirName)) {
				funTestDir = new File(Utility.getProjectHome()
						+ File.separatorChar + appDirName + File.separatorChar
						+ srcDirName + File.separatorChar + subModule);
			}
			if (StringUtils.isEmpty(functionalTestDir)) {
				functionalTestDir = "";
			}
			
			String jarLocation = "";
			if (BUILD.equals(testAgainst)) {
				environmentName = configValues.get(ENVIRONMENT_NAME);
				jarLocation = getJarLocation(workingDirectory.getPath());
			} else if (SERVER.equals(testAgainst)) {
				environmentName = configValues.get(ENVIRONMENT_NAME);
			} else if (JAR.equals(testAgainst)) {
				jarLocation = configValues.get(JAR_LOCATION);
			}
			if (StringUtils.isNotEmpty(jarLocation)) {
				setPropertyJarLocation(
						funTestDir.getPath() + functionalTestDir, jarLocation);
			}
			

			//to Run cucumber
			if (StringUtils.isNotEmpty((seleniumToolType))
					&& seleniumToolType.equals(CUCUMBER)) {
				if (repDirName.contains(PROJECT_BASDIR)) {
					repDirName = repDirName.replace(PROJECT_BASDIR, baseDir.getPath());
				}
				
				String mainClass=configValues.get(MAINCLASS);
				String runMode = configValues.get(RUNMODE);
				String runEnviroment = configValues.get(ENVIROIMENT);
				String vdiName=configValues.get(VDINAME);
				String breakPoint=configValues.get(BREAKPOINT);
				String targetOperatingSystem=configValues.get(TARGETOPERATINGSYSTEM);
				String scenarioTags=configValues.get(SCENARIOTAGS);
				StringBuilder builder = new StringBuilder();
				builder.append(TEST_COMMAND +" -DmainClass="+mainClass+" -DrunMode="+runMode+" -DrunEnviroment="+runEnviroment+" -DvdiName="+vdiName+" -DbreakPoint="+breakPoint+" -DtargetOperatingSystem="+targetOperatingSystem+" -DscenarioTags="+scenarioTags+"");
				Utility.executeStreamconsumer(builder.toString(), funTestDir+ File.separator + functionalTestDir, project.getBasedir().getPath(), "");
				CucumberReportGenerator cu=new CucumberReportGenerator();
				cu.generateReport(funTestDir+functionalTestDir+ File.separator +TARGET,repDirName);
				return new DefaultExecutionStatus();
			}
			
			
			String browserValue = configValues.get(BROWSER);
			String resolutionValue = configValues.get(RESOLUTION);
			String resultConfigFileDir = processor
			.getProperty(Constants.PHRESCO_FUNCTIONAL_TEST_ADAPT_DIR);
			if (StringUtils.isNotEmpty(resultConfigFileDir)) {
				File resultConfigXml = new File(funTestDir
						+ resultConfigFileDir);
				adaptTestConfig(resultConfigXml, environmentName, browserValue,
						resolutionValue, workingDirectory.getPath());
			}
			
			if (StringUtils.isNotEmpty((seleniumToolType))
					&& seleniumToolType.equals(CAPYBARA)) {
				StringBuilder builder = new StringBuilder();
				builder.append("cucumber -f junit -o target -f html -o target/cuke.html");
				Utility.executeStreamconsumer(builder.toString(), funTestDir
						+ File.separator + functionalTestDir, project
						.getBasedir().getPath(), "");
				return new DefaultExecutionStatus();
			}
			
			generateMavenCommand(mavenProjectInfo, funTestDir
					+ functionalTestDir, FUNCTIONAL);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}

		return new DefaultExecutionStatus();
	}

	private String getJarLocation(String basedir) {
		File jarFile = new File(basedir + DO_NOT_CHECKIN_FOLDER
				+ File.separator + TARGET);
		File[] listFiles = jarFile.listFiles();
		if (!ArrayUtils.isEmpty(listFiles)) {
			for (File file : listFiles) {
				if (file.getPath().endsWith(".jar")) {
					return file.getPath();
				}
			}
		}
		return "";
	}

	public ExecutionStatus runPerformanceTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			PluginUtils pluginUtils = new PluginUtils();
			MavenProject project = mavenProjectInfo.getProject();
			String subModule = "";
			File baseDir = project.getBasedir();
			File workingDir = baseDir;
			if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
				subModule = mavenProjectInfo.getModuleName();
				workingDir = new File(workingDir + File.separator + subModule);
			}
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String testBasis = configs.get(TEST_BASIS);
			String customTestAgainst = configs.get(CUSTOM_TEST_AGAINST);
			String testAgainstType = configs.get(TEST_AGAINST);
			String testName = configs.get(KEY_TEST_NAME);
			String environmentName = configs.get(ENVIRONMENT_NAME);
			String configurationsName = configs.get(KEY_CONFIGURATION);
			String rampUpPeriod = configs.get(KEY_RAMP_UP_PERIOD);
			String authManager = configs.get(KEY_AUTH_MANAGER);
			String authorizationUrl = configs.get(KEY_AUTHORIZATION_URL);
			String authorizationUserName = configs
			.get(KEY_AUTHORIZATION_USER_NAME);
			String authorizationPassword = configs
			.get(KEY_AUTHORIZATION_PASSWORD);
			String authorizationDomain = configs.get(KEY_AUTHORIZATION_DOMAIN);
			String authorizationRealm = configs.get(KEY_AUTHORIZATION_REALM);
			String jmxs = configs.get(AVAILABLE_JMX);

			String performanceAgainst = "";
			if (StringUtils.isNotEmpty(testBasis)
					&& CUSTOMISE.equals(testBasis)) {
				performanceAgainst = customTestAgainst;
			} else {
				performanceAgainst = testAgainstType;
			}
			int noOfUsers = 1;
			int loopCount = 1;
			File dotPhrescoDir = baseDir;
			String dotPhrescoDirName = project.getProperties().getProperty(
					Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
			if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
				dotPhrescoDir = new File(baseDir.getParent()
						+ File.separatorChar + dotPhrescoDirName);
			}
			if (StringUtils.isNotEmpty(dotPhrescoDirName)
					&& StringUtils.isNotEmpty(subModule)) {
				dotPhrescoDir = new File(dotPhrescoDir.getParentFile()
						.getPath() + File.separatorChar + dotPhrescoDirName);
			}
			dotPhrescoDir = new File(dotPhrescoDir.getPath()
					+ File.separatorChar + subModule);
			File pomFile = pluginUtils.getPomFile(dotPhrescoDir, workingDir);
			PomProcessor processor = new PomProcessor(pomFile);
			ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
			String appDirName = appInfo.getAppDirName();
			String perfTestDirName = processor
			.getProperty(Constants.POM_PROP_KEY_SPLIT_TEST_DIR);
			String srcDirName = project.getProperties().getProperty(
					Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
			File perfTestDir = workingDir;
			if (StringUtils.isNotEmpty(perfTestDirName)) {
				perfTestDir = new File(Utility.getProjectHome()
						+ File.separatorChar + appDirName + File.separatorChar
						+ perfTestDirName + File.separatorChar + subModule);
			} else if (StringUtils.isNotEmpty(srcDirName)) {
				perfTestDir = new File(Utility.getProjectHome()
						+ File.separatorChar + appDirName + File.separatorChar
						+ srcDirName + File.separatorChar + subModule);
			}
			String performanceTestDir = processor
			.getProperty(Constants.POM_PROP_KEY_PERFORMANCETEST_DIR)
			+ File.separator + performanceAgainst;
			if (StringUtils.isNotEmpty(performanceTestDir)) {
				StringBuilder testPomPath = new StringBuilder(
						perfTestDir.getPath()).append(performanceTestDir)
						.append(File.separator).append(POM_XML);
				File testPomFile = new File(testPomPath.toString());
				PomProcessor pomProcessor = new PomProcessor(testPomFile);
				Plugin plugin = pomProcessor.getPlugin(COM_LAZERYCODE_JMETER,
						JMETER_MAVEN_PLUGIN);

				DocumentBuilderFactory dbfac = DocumentBuilderFactory
				.newInstance();
				DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				com.phresco.pom.model.Plugin.Configuration jmeterConfiguration = plugin
				.getConfiguration();
				// If test basis is customise
				if (StringUtils.isNotEmpty(testBasis)
						&& CUSTOMISE.equals(testBasis)) {
					List<String> jmxFiles = Arrays.asList(jmxs
							.split(CSV_PATTERN));
					checkForSameJmxDirectory(jmxFiles);
					if (testPomFile.exists()) {
						List<Element> configList = jmxUploadPluginConfiguration(
								doc, jmeterConfiguration, jmxFiles);
						pomProcessor.addConfiguration(COM_LAZERYCODE_JMETER,
								JMETER_MAVEN_PLUGIN, configList);
						pomProcessor.save();
					}
				} else {
					com.photon.phresco.configuration.Configuration config = null;
					List<com.photon.phresco.configuration.Configuration> configurations = pluginUtils
					.getConfiguration(dotPhrescoDir, environmentName,
							testAgainstType);
					for (com.photon.phresco.configuration.Configuration conf : configurations) {
						if (conf.getName().equals(configurationsName)) {
							config = conf;
							break;
						}
					}
					List<Element> configList = testAgainstParameterPluginConfiguration(
							doc, jmeterConfiguration, testName);
					pomProcessor.addConfiguration(COM_LAZERYCODE_JMETER,
							JMETER_MAVEN_PLUGIN, configList);
					pomProcessor.save();
					String testConfigFilePath = perfTestDir.getPath()
					+ File.separator + performanceTestDir
					+ File.separator + TESTS_FOLDER;
					pluginUtils.adaptTestConfig(testConfigFilePath
							+ File.separator, config, "performanceTest");
					String jsonFile = perfTestDir.getPath() + File.separator
					+ performanceTestDir + File.separator
					+ Constants.FOLDER_JSON + File.separator + testName
					+ Constants.DOT_JSON;
					BufferedReader bufferedReader = new BufferedReader(
							new FileReader(jsonFile));
					Gson gson = new Gson();
					PerformanceDetails fromJson = gson.fromJson(bufferedReader,
							PerformanceDetails.class);
					List<ContextUrls> contextUrls = fromJson.getContextUrls();
					List<DbContextUrls> dbContextUrls = fromJson
					.getDbContextUrls();
					if (TEST_AGAINST_DB.equalsIgnoreCase(testAgainstType)) {
						String host = config.getProperties().getProperty(
								Constants.DB_HOST);
						String port = config.getProperties().getProperty(
								Constants.DB_PORT);
						String dbname = config.getProperties().getProperty(
								Constants.DB_NAME);
						String type = config.getProperties().getProperty(
								Constants.DB_TYPE);
						String userName = config.getProperties().getProperty(
								Constants.DB_USERNAME);
						String passWord = config.getProperties().getProperty(
								Constants.DB_PASSWORD);
						String dbUrl = "";
						String driver = "";
						if (type.equalsIgnoreCase(Constants.MYSQL_DB)) {
							dbUrl = "jdbc:mysql://" + host + ":" + port + "/"
							+ dbname;
						} else if (type.equalsIgnoreCase(Constants.ORACLE_DB)) {
							dbUrl = "jdbc:oracle:thin:@" + host + ":" + port
							+ ":" + dbname;
						} else if (type.equalsIgnoreCase(Constants.MSSQL_DB)) {
							dbUrl = "jdbc:sqlserver://" + host + ":" + port
							+ ";databaseName=" + dbname;
						} else if (type.equalsIgnoreCase(Constants.DB2_DB)) {
							dbUrl = "jdbc:db2://" + host + ":" + port + "/"
							+ dbname;
						}
						DatabaseUtil.initDriverMap();
						DatabaseUtil du = new DatabaseUtil();
						driver = du.getDbDriver(type.toLowerCase());
						pluginUtils.adaptDBPerformanceJmx(testConfigFilePath,
								dbContextUrls, configurationsName, noOfUsers,
								Integer.parseInt(rampUpPeriod), loopCount,
								dbUrl, driver, userName, passWord);
					} else {
						pluginUtils.adaptPerformanceJmx(testConfigFilePath,
								contextUrls, noOfUsers,
								Integer.parseInt(rampUpPeriod), loopCount,
								Boolean.parseBoolean(authManager),
								authorizationUrl, authorizationUserName,
								authorizationPassword, authorizationDomain,
								authorizationRealm);
					}
				}
			}
			generateMavenCommand(mavenProjectInfo, perfTestDir.getPath()
					+ performanceTestDir, PERFORMACE);

		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return new DefaultExecutionStatus();
	}

	private void checkForSameJmxDirectory(List<String> jmxFiles)
	throws PhrescoException {
		if (CollectionUtils.isNotEmpty(jmxFiles)) {
			String dir = jmxFiles.get(0).split(SEP)[0];
			for (String jmxFile : jmxFiles) {
				if (!dir.equals(jmxFile.split(SEP)[0])) {
					throw new PhrescoException(
					"Selected jmx files exists in different location. Please select files from same location");
				}
			}
		}
	}

	private List<Element> jmxUploadPluginConfiguration(Document doc,
			com.phresco.pom.model.Plugin.Configuration configuration,
			List<String> jmxFiles) {

		List<Element> configList = configuration.getAny();
		List<Element> newList = new ArrayList<Element>();
		for (Element element : configList) {
			if (TEST_FILES_DIRECTORY.equals(element.getTagName())) {
				element.setTextContent(jmxFiles.get(0).split(SEP)[0]);
				newList.add(element);
			} else if (!TEST_FILES_INCLUDED.equals(element.getTagName())
					&& !RESULT_FILES_NAME
					.equalsIgnoreCase(element.getTagName())) {
				newList.add(element);
			}
		}
		createResultFilesName(doc, jmxFiles, newList);
		createTestFilesIncludes(doc, jmxFiles, newList);

		return newList;
	}

	private List<Element> testAgainstParameterPluginConfiguration(Document doc,
			com.phresco.pom.model.Plugin.Configuration configuration,
			String resultName) {

		List<Element> configList = configuration.getAny();
		List<Element> newList = new ArrayList<Element>();
		for (Element element : configList) {
			if (TEST_FILES_DIRECTORY.equals(element.getTagName())) {
				element.setTextContent(TESTS_SLASH);
				newList.add(element);
			} else if (!TEST_FILES_INCLUDED.equals(element.getTagName())
					&& !RESULT_FILES_NAME
					.equalsIgnoreCase(element.getTagName())) {
				newList.add(element);
			}
		}

		Element resultFilesNameElement = doc.createElement(RESULT_FILES_NAME);
		appendChildElement(doc, resultFilesNameElement, RESULT_NAME, resultName);
		newList.add(resultFilesNameElement);

		Element testFilesIncludedElement = doc
		.createElement(TEST_FILES_INCLUDED);
		appendChildElement(doc, testFilesIncludedElement, JMETER_TEST_FILE,
				PHRESCO_FRAME_WORK_TEST_PLAN_JMX);
		newList.add(testFilesIncludedElement);

		return newList;
	}

	private void createTestFilesIncludes(Document doc, List<String> jmxFiles,
			List<Element> newList) {
		Element testFilesIncludedElement = doc
		.createElement(TEST_FILES_INCLUDED);
		for (String jmxFile : jmxFiles) {
			appendChildElement(doc, testFilesIncludedElement, JMETER_TEST_FILE,
					jmxFile.split(SEP)[1]);
		}
		newList.add(testFilesIncludedElement);
	}

	private void createResultFilesName(Document doc, List<String> jmxFiles,
			List<Element> newList) {
		Element resultFilesNameElement = doc.createElement(RESULT_FILES_NAME);
		for (String jmxFile : jmxFiles) {
			int lastDot = jmxFile.split(SEP)[1].lastIndexOf(DOT);
			String resultName = jmxFile.split(SEP)[1].substring(0, lastDot);
			appendChildElement(doc, resultFilesNameElement, RESULT_NAME,
					resultName);
		}
		newList.add(resultFilesNameElement);
	}

	private Element appendChildElement(Document doc, Element parent,
			String elementName, String textContent) {
		Element childElement = createElement(doc, elementName, textContent);
		parent.appendChild(childElement);
		return childElement;
	}

	private Element createElement(Document doc, String elementName,
			String textContent) {
		Element element = doc.createElement(elementName);
		if (StringUtils.isNotEmpty(textContent)) {
			element.setTextContent(textContent);
		}

		return element;
	}

	public ExecutionStatus runLoadTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			PluginUtils pluginUtils = new PluginUtils();
			MavenProject project = mavenProjectInfo.getProject();
			String subModule = "";
			File baseDir = project.getBasedir();
			File workingDir = baseDir;
			if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
				subModule = mavenProjectInfo.getModuleName();
				workingDir = new File(workingDir + File.separator + subModule);
			}
			File dotPhrescoDir = baseDir;
			String dotPhrescoDirName = project.getProperties()
			.getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
			if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
				dotPhrescoDir = new File(baseDir.getParent()
						+ File.separatorChar + dotPhrescoDirName);
			}
			if (StringUtils.isNotEmpty(dotPhrescoDirName)
					&& StringUtils.isNotEmpty(subModule)) {
				dotPhrescoDir = new File(dotPhrescoDir.getParentFile()
						.getPath() + File.separatorChar + dotPhrescoDirName);
			}
			dotPhrescoDir = new File(dotPhrescoDir.getPath()
					+ File.separatorChar + subModule);
			File pomFile = pluginUtils.getPomFile(dotPhrescoDir, workingDir);
			PomProcessor processor = new PomProcessor(pomFile);
			ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
			String appDirName = appInfo.getAppDirName();
			String loadTestDirName = processor
			.getProperty(Constants.POM_PROP_KEY_SPLIT_TEST_DIR);
			String srcDirName = processor
			.getProperty(Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
			File loadTestDir = workingDir;
			if (StringUtils.isNotEmpty(loadTestDirName)) {
				loadTestDir = new File(Utility.getProjectHome()
						+ File.separatorChar + appDirName + File.separatorChar
						+ loadTestDirName + File.separatorChar + subModule);
			} else if (StringUtils.isNotEmpty(srcDirName)) {
				loadTestDir = new File(Utility.getProjectHome()
						+ File.separatorChar + appDirName + File.separatorChar
						+ srcDirName + File.separatorChar + subModule);
			}
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String testBasis = configs.get(TEST_BASIS);
			String customTestAgainst = configs.get(CUSTOM_TEST_AGAINST);
			String testAgainstType = configs.get(TEST_AGAINST);
			String testName = configs.get(KEY_TEST_NAME);
			String environmentName = configs.get(ENVIRONMENT_NAME);
			String type = configs.get(KEY_CONFIGURATION);
			String noOfUsers = configs.get(KEY_NO_OF_USERS);
			String rampUpPeriod = configs.get(KEY_RAMP_UP_PERIOD);
			String loopCount = configs.get(KEY_LOOP_COUNT);
			String jmxs = configs.get(AVAILABLE_JMX);
			String authManager = configs.get(KEY_AUTH_MANAGER);
			String authorizationUrl = configs.get(KEY_AUTHORIZATION_URL);
			String authorizationUserName = configs
			.get(KEY_AUTHORIZATION_USER_NAME);
			String authorizationPassword = configs
			.get(KEY_AUTHORIZATION_PASSWORD);
			String authorizationDomain = configs.get(KEY_AUTHORIZATION_DOMAIN);
			String authorizationRealm = configs.get(KEY_AUTHORIZATION_REALM);

			String loadTestAgainst = "";
			if (StringUtils.isNotEmpty(testBasis)
					&& CUSTOMISE.equals(testBasis)) {
				loadTestAgainst = customTestAgainst;
			} else {
				loadTestAgainst = testAgainstType;
			}
			String loadTestDirProp = processor
			.getProperty(Constants.POM_PROP_KEY_LOADTEST_DIR);
			if (StringUtils.isNotEmpty(loadTestDirProp)) {
				loadTestDirProp = loadTestDirProp + File.separator
				+ loadTestAgainst;
				StringBuilder testPomPath = new StringBuilder(
						loadTestDir.getPath()).append(loadTestDirProp)
						.append(File.separator).append(POM_XML);
				File testPomFile = new File(testPomPath.toString());
				PomProcessor pomProcessor = new PomProcessor(testPomFile);
				Plugin plugin = pomProcessor.getPlugin(COM_LAZERYCODE_JMETER,
						JMETER_MAVEN_PLUGIN);

				DocumentBuilderFactory dbfac = DocumentBuilderFactory
				.newInstance();
				DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				com.phresco.pom.model.Plugin.Configuration jmeterConfiguration = plugin
				.getConfiguration();
				// If test basis is customise
				if (StringUtils.isNotEmpty(testBasis)
						&& CUSTOMISE.equals(testBasis)) {
					List<String> jmxFiles = Arrays.asList(jmxs
							.split(CSV_PATTERN));
					if (testPomFile.exists()) {
						List<Element> configList = jmxUploadPluginConfiguration(
								doc, jmeterConfiguration, jmxFiles);
						pomProcessor.addConfiguration(COM_LAZERYCODE_JMETER,
								JMETER_MAVEN_PLUGIN, configList);
						pomProcessor.save();
					}
				} else {
					com.photon.phresco.configuration.Configuration config = null;
					List<com.photon.phresco.configuration.Configuration> configurations = pluginUtils
					.getConfiguration(dotPhrescoDir, environmentName,
							testAgainstType);
					for (com.photon.phresco.configuration.Configuration conf : configurations) {
						if (conf.getName().equals(type)) {
							config = conf;
							break;
						}
					}
					List<Element> configList = testAgainstParameterPluginConfiguration(
							doc, jmeterConfiguration, testName);
					pomProcessor.addConfiguration(COM_LAZERYCODE_JMETER,
							JMETER_MAVEN_PLUGIN, configList);
					pomProcessor.save();
					String testConfigFilePath = loadTestDir.getPath()
					+ File.separator + loadTestDirProp + File.separator
					+ TESTS_FOLDER;
					pluginUtils.adaptTestConfig(testConfigFilePath
							+ File.separator, config, "loadTest");
					String jsonFile = loadTestDir.getPath() + File.separator
					+ loadTestDirProp + File.separator
					+ Constants.FOLDER_JSON + File.separator + testName
					+ Constants.DOT_JSON;
					BufferedReader bufferedReader = new BufferedReader(
							new FileReader(jsonFile));
					Gson gson = new Gson();
					PerformanceDetails fromJson = gson.fromJson(bufferedReader,
							PerformanceDetails.class);
					List<ContextUrls> contextUrls = fromJson.getContextUrls();

					pluginUtils.adaptLoadJmx(testConfigFilePath
							+ File.separator, Integer.parseInt(noOfUsers),
							Integer.parseInt(rampUpPeriod),
							Integer.parseInt(loopCount),
							Boolean.parseBoolean(authManager),
							authorizationUrl, authorizationUserName,
							authorizationPassword, authorizationDomain,
							authorizationRealm, contextUrls);
				}
			}
			generateMavenCommand(mavenProjectInfo, loadTestDir.getPath()
					+ File.separator + loadTestDirProp, LOAD);

		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return new DefaultExecutionStatus();
	}

	public ExecutionStatus runIntegrationTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		String workingDirectory = mavenProjectInfo.getBaseDir().getPath();
		StringBuilder sb = new StringBuilder();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String environment = configs.get("environment");
		sb.append(TEST_COMMAND).append(STR_SPACE)
		.append("-Denvironment=" + environment);
		Utility.executeStreamconsumer(sb.toString(), workingDirectory,
				workingDirectory, "");
		return new DefaultExecutionStatus();
	}

	public ExecutionStatus validate(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
		buildVersion = mavenProjectInfo.getBuildVersion();
		StringBuilder sb = new StringBuilder();
		sb.append(SONAR_COMMAND);
		sb = sb.append(mavenProjectInfo.getSonarParams().toString());
		Map<String, String> config = MojoUtil.getAllValues(configuration);
		String subModule = "";
		MavenProject project = mavenProjectInfo.getProject();
		File workingDirectory = project.getBasedir();
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			subModule = mavenProjectInfo.getModuleName();
			workingDirectory = new File(workingDirectory + File.separator
					+ subModule);
		}
		String dotPhrescoDirName = project.getProperties().getProperty(
				Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		File baseDir = project.getBasedir();
		File dotPhrescoDir = baseDir;
		if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
			dotPhrescoDir = new File(baseDir.getParent() + File.separator
					+ dotPhrescoDirName + File.separatorChar + subModule);
		} else if(StringUtils.isNotEmpty(subModule)) {
			dotPhrescoDir = new File(dotPhrescoDir.getPath() + File.separatorChar + subModule);
		}
		String jarLocation = getJarLocation(workingDirectory.getPath());
		String funTestDirName = project.getProperties().getProperty(
				Constants.POM_PROP_KEY_SPLIT_TEST_DIR);
		String srcDirName = project.getProperties().getProperty(
				Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
		File funTestDir = workingDirectory;
		PluginUtils pluginUtils = new PluginUtils();
		ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
		String appDirName = appInfo.getAppDirName();
		if (StringUtils.isNotEmpty(funTestDirName)) {
			funTestDir = new File(Utility.getProjectHome() + File.separatorChar
					+ appDirName + File.separatorChar + funTestDirName
					+ File.separatorChar + subModule);
		} else if (StringUtils.isNotEmpty(srcDirName)) {
			funTestDir = new File(Utility.getProjectHome() + File.separatorChar
					+ appDirName + File.separatorChar + srcDirName
					+ File.separatorChar + subModule);
		}
		File pomFile = pluginUtils.getPomFile(dotPhrescoDir, workingDirectory);
		String value = config.get(SONAR);
		if(!value.equalsIgnoreCase(FUNCTIONAL)) {
		List<Parameter> parameters = configuration.getParameters().getParameter();
		String branchName = PluginUtils.getSonarBranchName(parameters);
		if(StringUtils.isNotEmpty(branchName)) {
			sb.append(STR_SPACE).append("-Dsonar.branch=").append(branchName).append(appInfo.getId());
		}
		for (Parameter parameter : parameters) {
			if (parameter.getPluginParameter() != null
					&& parameter.getPluginParameter().equals(PLUGIN_PARAMETER)
					&& parameter.getMavenCommands() != null) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if (parameter.getValue().equals(mavenCommand.getKey())) {
						sb.append(STR_SPACE);
						sb.append(mavenCommand.getValue());
					}
				}
			}
		}	
	}
		if (value.equals(FUNCTIONAL)) {
			sb.delete(0, sb.length());
			workingDirectory = new File(funTestDir.getPath()
					+ getFunctionalDir(workingDirectory, pomFile.getName()));
			if (StringUtils.isNotEmpty(jarLocation)) {
				setPropertyJarLocation(workingDirectory.getPath(), jarLocation);
			}
			sb.append(SONAR_COMMAND).append(STR_SPACE).append(SKIP_TESTS)
			.append(STR_SPACE).append("-Dsonar.branch=functional").append(appInfo.getId());
			sb = sb.append(mavenProjectInfo.getSonarParams().toString());
		}
		
		File workingFile = new File(workingDirectory + File.separator
				+ pomFile.getName());
		if (workingFile.exists() && !value.equals(FUNCTIONAL)) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomFile.getName());
		}
		if(org.apache.commons.lang.StringUtils.isNotEmpty(buildVersion)) {
			sb.append(STR_SPACE);
			sb.append("-Dpackage.version=" + buildVersion);
		}
		boolean status = Utility.executeStreamconsumer(sb.toString(),
				workingDirectory.getPath(), workingDirectory.getPath(),
				"");
		if (!status) {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
			}
			} catch (MojoExecutionException e) {
				throw new PhrescoException(e);
			}
		return new DefaultExecutionStatus();
	}

	private String getFunctionalDir(File workingDirectory, String pomFile) {
		String value = "";
		try {
			PomProcessor processor = new PomProcessor(new File(
					workingDirectory.getPath() + File.separatorChar + pomFile));
			value = processor.getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
		} catch (Exception e) {
		}
		return value;
	}

	private void generateMavenCommand(MavenProjectInfo mavenProjectInfo,
			String workingDirectory, String actionType) throws PhrescoException {
		StringBuilder sb = new StringBuilder();
		String pomFile = mavenProjectInfo.getProject().getFile().getName();
		File workingFile = new File(workingDirectory + File.separator + pomFile);
		sb.append(TEST_COMMAND);
		if (workingFile.exists()) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomFile);
		}
		if(org.apache.commons.lang.StringUtils.isNotEmpty(buildVersion)) {
			sb.append(STR_SPACE);
			sb.append("-Dpackage.version=" + buildVersion);
		}
		File baseDir = mavenProjectInfo.getBaseDir();
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			baseDir = new File(baseDir + File.separator
					+ mavenProjectInfo.getModuleName());
		}
		boolean status = Utility.executeStreamconsumer(sb.toString(),
				workingDirectory, baseDir.getPath(), "");
		if (!status) {
			throw new PhrescoException(Constants.MOJO_ERROR_MESSAGE);
		}
	}

	public void adaptTestConfig(File resultConfigXml, String envName,
			String browser, String resolution, String baseDir)
	throws PhrescoException {
		PluginUtils pu = new PluginUtils();
		List<com.photon.phresco.configuration.Configuration> configurations = pu.getConfiguration(new File(baseDir), envName,
				Constants.SETTINGS_TEMPLATE_SERVER);
		List<com.photon.phresco.configuration.Configuration> webserviceConfigurations = pu.getConfiguration(new File(baseDir), envName,
				Constants.SETTINGS_TEMPLATE_WEBSERVICE);
		if (CollectionUtils.isNotEmpty(webserviceConfigurations)) {
			configurations.addAll(webserviceConfigurations);
		}
		String techId = getTechId(new File(baseDir));
		if (CollectionUtils.isNotEmpty(configurations)) {
			for (com.photon.phresco.configuration.Configuration configuration : configurations) {
				updateTestConfiguration(configuration, browser,
						resultConfigXml, resolution);
			}
		} else if (CollectionUtils.isEmpty(configurations)
				&& !techId.equals(TechnologyTypes.JAVA_STANDALONE)) {
			throw new PhrescoException("Configuration Not found...");
		}
	}

	private String getTechId(File baseDir) throws PhrescoException {
		try {
			ProjectUtils projectutils = new ProjectUtils();
			ProjectInfo projectInfo = projectutils.getProjectInfo(baseDir);
			TechnologyInfo applicationInfo = projectInfo.getAppInfos().get(0)
			.getTechInfo();
			return applicationInfo.getId();
		} catch (PhrescoException e) {
			throw new PhrescoException(e);
		}
	}

	private void setPropertyJarLocation(String functionalTestDir,
			String systemPath) throws PhrescoException {
		try {
			if (StringUtils.isNotEmpty(functionalTestDir)) {
				PomProcessor processor = new PomProcessor(
						new File(functionalTestDir + File.separator
								+ Constants.POM_NAME));
				processor.setProperty(
						Constants.POM_PROP_KEY_JAVA_STAND_ALONE_JAR_PATH,
						systemPath);
				processor.save();
			}
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}

	private void updateTestConfiguration(
			com.photon.phresco.configuration.Configuration configuration,
			String browser, File resultConfigXml, String resolution)
	throws PhrescoException {
		try {
			ConfigManager configManager = new ConfigManagerImpl(resultConfigXml);
			Element createConfigElement = configManager
			.createConfigElement(configuration);
			DocumentBuilderFactory factory = DocumentBuilderFactory
			.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(resultConfigXml);

			Node configNode = getNode("environment", document);
			Node node = getNode("environment/" + configuration.getType(),
					document);
			Node browserNode = getNode("environment/Browser", document);
			Node resolutionNode = getNode("environment/Resolution", document);

			if (node != null) {
				configNode.removeChild(node);
			}

			if (browserNode != null) {
				browserNode.setTextContent(browser);
			} else {
				Element browserEle = document.createElement("Browser");
				browserEle.setTextContent(browser);
				configNode.appendChild(browserEle);
			}
			if (resolution != null) {
				if (resolutionNode != null) {
					resolutionNode.setTextContent(resolution);
				} else {
					Element resolutiontag = document
					.createElement("Resolution");
					resolutiontag.setTextContent(resolution);
					configNode.appendChild(resolutiontag);
				}
			}
			Node importNode = document.importNode(createConfigElement,
					Boolean.TRUE);
			configNode.appendChild(importNode);
			writeXml(new FileOutputStream(resultConfigXml), document);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PhrescoException("Configuration Not found...");
		}
	}

	private Node getNode(String xpath, Document document)
	throws XPathExpressionException {
		XPathExpression xPathExpression = getXPath().compile(xpath);
		return (Node) xPathExpression.evaluate(document, XPathConstants.NODE);
	}

	private XPath getXPath() {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		return xPathFactory.newXPath();
	}

	protected void writeXml(OutputStream fos, Document document)
	throws PhrescoException, TransformerException, IOException {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
			"yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			Source src = new DOMSource(document);
			Result res = new StreamResult(fos);
			transformer.transform(src, res);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	public void writePhrescoBuildXml(
			com.photon.phresco.plugins.model.Mojos.Mojo.Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			StringBuilder sb = new StringBuilder(mavenProjectInfo.getBaseDir()
					.toString()).append(File.separator);
			if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
				sb.append(mavenProjectInfo.getModuleName()).append(
						File.separator);
			}
			sb.append(".phresco").append(File.separator)
			.append("phresco-build.xml");
			File configFile = new File(sb.toString());
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String value = configs.get("packageFileBrowse");
			if (StringUtils.isNotEmpty(value)) {
				Map<String, List<String>> directoriesMap = new HashMap<String, List<String>>();
				Map<String, List<String>> filesMap = new HashMap<String, List<String>>();
				getBuildConfigMap(value, directoriesMap, filesMap,
						mavenProjectInfo);
				writeToBuildConfigXml(configFile, directoriesMap, filesMap);
			} else {
				configFile.delete();
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	/**
	 * To get the file/folders map based on the selection
	 * 
	 * @param csvTargetFolder
	 * @param directoriesMap
	 * @param filesMap
	 * @throws PhrescoException
	 */
	private void getBuildConfigMap(String csvTargetFolder,
			Map<String, List<String>> directoriesMap,
			Map<String, List<String>> filesMap,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			String[] sepSplits = csvTargetFolder.split(SEP);
			for (String sepSplit : sepSplits) {
				String[] fileSepSplits = sepSplit.split("#FILESEP#");
				String targetFolder = fileSepSplits[0];
				String fileOrFolder = fileSepSplits[1];
				if (new File(mavenProjectInfo.getBaseDir().toString()
						+ fileOrFolder).isDirectory()) {
					if (directoriesMap.containsKey(targetFolder)) {
						List<String> list = new ArrayList<String>();
						list.addAll(directoriesMap.get(targetFolder));
						list.add(fileOrFolder);
						directoriesMap.put(targetFolder, list);
					} else {
						directoriesMap.put(targetFolder,
								Collections.singletonList(fileOrFolder));
					}
				} else {
					List<String> list = new ArrayList<String>();
					if (filesMap.containsKey(targetFolder)) {
						list.addAll(filesMap.get(targetFolder));
					}
					String[] split = fileOrFolder.split(",");
					list.addAll(Arrays.asList(split));
					filesMap.put(targetFolder, list);
				}
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	/**
	 * To write the map values into build-config.xml
	 * 
	 * @param configFile
	 * @param directoriesMap
	 * @param filesMap
	 * @throws PhrescoException
	 */
	private void writeToBuildConfigXml(File configFile,
			Map<String, List<String>> directoriesMap,
			Map<String, List<String>> filesMap) throws PhrescoException {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
			.newInstance();
			domFactory.setNamespaceAware(false);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element rootElement = document.createElement("build");
			Element directoriesElement = document.createElement("directories");
			if (MapUtils.isNotEmpty(directoriesMap)) {
				Set<String> keySet = directoriesMap.keySet();
				for (String key : keySet) {
					List<String> list = directoriesMap.get(key);
					for (String string : list) {
						Element directoryElement = document
						.createElement("directory");
						if (StringUtils.isNotEmpty(key)) {
							directoryElement.setAttribute("toDirectory", key);
						}
						directoryElement.setTextContent(string);
						directoriesElement.appendChild(directoryElement);
					}
				}
			}
			rootElement.appendChild(directoriesElement);

			Element filesElement = document.createElement("files");
			if (MapUtils.isNotEmpty(filesMap)) {
				Set<String> keySet = filesMap.keySet();
				for (String key : keySet) {
					List<String> list = filesMap.get(key);
					for (String string : list) {
						Element fileElement = document.createElement("file");
						if (StringUtils.isNotEmpty(key)) {
							fileElement.setAttribute("toDirectory", key);
						}
						fileElement.setTextContent(string);
						filesElement.appendChild(fileElement);
					}
				}
			}
			rootElement.appendChild(filesElement);
			document.appendChild(rootElement);

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
			"yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			Source src = new DOMSource(document);
			Result res = new StreamResult(new FileOutputStream(configFile));
			transformer.transform(src, res);
		} catch (DOMException e) {
			throw new PhrescoException(e);
		} catch (TransformerConfigurationException e) {
			throw new PhrescoException(e);
		} catch (IllegalArgumentException e) {
			throw new PhrescoException(e);
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		} catch (ParserConfigurationException e) {
			throw new PhrescoException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new PhrescoException(e);
		} catch (TransformerException e) {
			throw new PhrescoException(e);
		}
	}
}
