/**
 * sharepoint-phresco-plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
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
package com.photon.phresco.plugins.sharepoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Utility;

public class Package implements PluginConstants {

	private File baseDir;
	private String environmentName;
	private String buildName;
	private String buildNumber;
	private int buildNo;

	private File wspDir;
	private File buildDir;
	private File buildInfoFile;
	private File tempDir;
	private int nextBuildNo;
	private Date currentDate;
	private String sourceDirectory = "/source";
	private Log log;
	private PluginPackageUtil util;
	private MavenProject project;
	private String pomFile;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        util = new PluginPackageUtil();
        pomFile = project.getFile().getName();
        
        PluginUtils.checkForConfigurations(baseDir, environmentName);
        try {
			init();
			executeExe();
			boolean buildStatus = build();
			writeBuildInfo(buildStatus);
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		
		
	}
	
	private void init() throws MojoExecutionException {
		try {
			unPackCabLib();
			replaceValue();
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			buildInfoFile = new File(buildDir.getPath() + BUILD_INFO_FILE);
			nextBuildNo = util.generateNextBuildNo(buildInfoFile);
			currentDate = Calendar.getInstance().getTime();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void unPackCabLib() throws MojoExecutionException  {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_CLEAN);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_VALDATE);
			if(!Constants.POM_NAME.equals(pomFile)) {
				sb.append(STR_SPACE);
				sb.append(Constants.HYPHEN_F);
				sb.append(STR_SPACE);
				sb.append(pomFile);
			}
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory(baseDir);
			Process process = cl.execute();
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void replaceValue() throws MojoExecutionException {
		SAXBuilder builder = new SAXBuilder();
		try {
			File xmlFile = new File(baseDir.getPath() + sourceDirectory + SHAREPOINT_WSP_CONFIG_FILE);
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			Element appSettings = rootNode.getChild(SHAREPOINT_APPSETTINGS, rootNode.getNamespace());
			if (appSettings != null) {
				List children = appSettings.getChildren(SHAREPOINT_ADD, appSettings.getNamespace());
				for (Object object : children) {
					Element dependent = (Element) object;
					String keyValue = dependent.getAttributeValue(SHAREPOINT_KEY);
					Attribute attribute = dependent.getAttribute(SHAREPOINT_VALUE);
					if (keyValue.equals(SHAREPOINT_SOLUTION_PATH)) {
						attribute.setValue(baseDir.getPath() + sourceDirectory);
					} else if (keyValue.equals(SHAREPOINT_OUTPUT_PATH)) {
						attribute.setValue(baseDir.getPath() + sourceDirectory);
					} else if (keyValue.equals(SHAREPOINT_WSPNAME)) {
						attribute.setValue(baseDir.getName() + ".wsp");
					}
				}
				saveFile(xmlFile, doc);
			}
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(SHAREPOINT_WSP_CONFIG_FILE +" is missing!");
		} catch (JDOMException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private void saveFile(File projectPath, Document doc) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(projectPath);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, writer);
		} finally {
			Utility.closeStream(writer);
		}
	}

	private void executeExe() throws MojoExecutionException {
		BufferedReader in = null;
		try {
			log.info("Executing ...");
			Commandline cl = new Commandline("WSPBuilder.exe");
			cl.setWorkingDirectory(baseDir.getPath() + sourceDirectory);
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(in);
		}
	}

	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {
			log.info("Building the project...");
			wspDir = new File(baseDir + sourceDirectory);
			createPackage();
		} catch (Exception e) {
			isBuildSuccess = false;
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return isBuildSuccess;
	}

	private void createPackage() throws MojoExecutionException {
		try {
			String context = baseDir.getName();
			String zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			String zipNameWithoutExt = zipName.substring(0, zipName.lastIndexOf('.'));
			File packageInfoFile = new File(baseDir.getPath() + File.separator + DOT_PHRESCO_FOLDER + File.separator + PHRESCO_PACKAGE_FILE);
			if(packageInfoFile.exists()) {
				copyWspToPackage(zipNameWithoutExt, context);
				PluginUtils.createBuildResources(packageInfoFile, baseDir, tempDir);
			} else {
				copyWspToPackage(zipNameWithoutExt, context);
			}
			ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void copyWspToPackage(String zipNameWithoutExt, String context) throws MojoExecutionException {
		try {
			String[] list = wspDir.list(new WarFileNameFilter());
			if (list.length > 0) {
				File warFile = new File(wspDir.getPath() + File.separator + list[0]);
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				File contextWarFile = new File(wspDir.getPath() + File.separator + context + ".wsp");
				warFile.renameTo(contextWarFile);
				FileUtils.copyFileToDirectory(contextWarFile, tempDir);
			} else {
				throw new MojoExecutionException("Compilation Failure...");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void writeBuildInfo(boolean isBuildSuccess) throws PhrescoException {
		try {
			util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

	class WarFileNameFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.endsWith(".wsp");
		}
	}
}
