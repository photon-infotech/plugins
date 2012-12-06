/*
 * ###
 * sitecore-maven-plugin Maven Mojo
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
package com.photon.phresco.plugins.sitecore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

/**
 * @author suresh_ma
 *
 */
public class Package implements PluginConstants {

	private MavenProject project;
	private File baseDir;
	private String environmentName;
	private String buildName;
	private String buildNumber;

	private File buildDir;
	private File targetDir;
	protected int buildNo;
	private File srcDir;
	private File buildInfoFile;
	private int nextBuildNo;
	private String zipName;
	private Date currentDate;
	private Log log;
	private PluginPackageUtil util;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        project = mavenProjectInfo.getProject();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
		util = new PluginPackageUtil();
		
        try {
			init();
			addRootPathToCsFile();
			boolean buildStatus = build();
			writeBuildInfo(buildStatus);
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		
	}
	private void init() throws MojoExecutionException {
		try {
			
			srcDir = new File(baseDir.getPath() + File.separator + "source/src");
			buildDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			buildInfoFile = new File(buildDir.getPath() + PluginConstants.BUILD_INFO_FILE);
			targetDir = new File(project.getBuild().getDirectory());
			nextBuildNo = util.generateNextBuildNo(buildInfoFile);
			currentDate = Calendar.getInstance().getTime();
		} catch (Exception e) {
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void addRootPathToCsFile() throws MojoExecutionException {
		try {
			String deploylocation = null;
			String siteName = null;
			ConfigManager configManager = PhrescoFrameworkFactory.getConfigManager(new File(baseDir.getPath() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + CONFIG_FILE));
			List<com.photon.phresco.configuration.Configuration> configurations = configManager.getConfigurations(environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration configuration : configurations) {
				siteName = configuration.getProperties().getProperty(Constants.SITE_NAME);
				deploylocation = configuration.getProperties().getProperty(Constants.SERVER_DEPLOY_DIR);
				break;
			}
			File siteConfigFile = new File(baseDir.getPath() + "/source/src/App_Config/Include/SiteDefinition.config");
			if (!siteConfigFile.exists()) {
				return;
			}
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(false);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(siteConfigFile);
			NodeList environmentList = doc.getElementsByTagName("site");
			for (int i = 0; i < environmentList.getLength(); i++) {
				Element environment = (Element) environmentList.item(i);
				environment.setAttribute("name", siteName);
				environment.setAttribute("rootPath", deploylocation);

				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(siteConfigFile.toURI().getPath());
				transformer.transform(source, result);
			}
		} catch (DOMException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}  catch (TransformerException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ConfigurationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	
	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {
			executeMSBuildCmd();
			executeASPCompilerCmd();
			createPackage();
		} catch (Exception e) {
			isBuildSuccess = false;
			log.error(e);
		}
		return isBuildSuccess;
	}
	
	private void executeMSBuildCmd() throws MojoExecutionException {
		BufferedReader in = null;
		try {
			String[] list = srcDir.list(new CSFileNameFilter());
			StringBuilder sb = new StringBuilder();
			sb.append("msbuild.exe");
			sb.append(STR_SPACE);
			sb.append(list[0]);
			sb.append(STR_SPACE);
			sb.append("/t:rebuild");
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory(srcDir.getPath());
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line); // do not use getLog() here as this line already contains the log type.
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(in);
		}
	}

	private void executeASPCompilerCmd()throws MojoExecutionException {
		BufferedReader in = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("aspnet_compiler -v / -p ");
			sb.append("\"" + srcDir.getPath() + "\"");
			sb.append(STR_SPACE);
			sb.append("-u");
			sb.append(STR_SPACE);
			sb.append("\"" + targetDir.getPath() + "\"");
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory(srcDir.getPath());
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line); // do not use getLog() here as this line already contains the log type.
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(in);
		}
	}

	private void createPackage() throws MojoExecutionException {
		try {
			zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			ArchiveUtil.createArchive(targetDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}
	
	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {

		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
	}
}

class CSFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".csproj");
	}
}