/**
 * java-phresco-plugin
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
package com.photon.phresco.plugins.cq5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.commons.model.TechnologyInfo;
import com.photon.phresco.configuration.Environment;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.impl.ConfigManagerImpl;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Assembly.FileSets.FileSet;
import com.photon.phresco.plugins.model.Assembly.FileSets.FileSet.Excludes;
import com.photon.phresco.plugins.model.Assembly.FileSets.FileSet.Includes;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.plugins.util.WarConfigProcessor;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.ProjectUtils;
import com.photon.phresco.util.TechnologyTypes;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class Package implements PluginConstants {
	
	private MavenProject project;
	private File baseDir;
	private String environmentName;
	private String moduleName;
	private String buildName;
	private String buildNumber;
	private int buildNo;
	private File targetDir;
	private File buildDir;
	private File buildInfoFile;
	private File tempDir;
	private int nextBuildNo;
	private String zipName;
	private Date currentDate;
	private String context;
	private Log log;
	private PluginPackageUtil util;
	private PluginUtils pu;
	private String sourceDir;
	private StringBuilder builder;
	private String pomName;
	private String packagingType;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        project = mavenProjectInfo.getProject();
        pomName = project.getFile().getName();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        util = new PluginPackageUtil();
        pu = new PluginUtils();
        builder = new StringBuilder();
        moduleName = configs.get(PROJECT_MODULE);
        PluginUtils.checkForConfigurations(baseDir, environmentName);
        packagingType = getPackagingType();
        try { 
			init();
			getMavenCommands(configuration); // -DskipTests cmd
			executeMvnPackage();
			boolean buildStatus = build();
			writeBuildInfo(buildStatus);
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}	
	}
	
	private String getPackagingType() throws PhrescoException {
		StringBuilder builder = new StringBuilder();
		builder.append(baseDir.getPath())
		.append(File.separatorChar);
		if(StringUtils.isNotEmpty(moduleName)) {
			builder.append(moduleName);
			builder.append(File.separatorChar);
		}
		builder.append(pomName);
		try {
			PomProcessor pomProcessor = new PomProcessor(new File(builder.toString()));
			return pomProcessor.getModel().getPackaging();
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}
	
	private void init() throws MojoExecutionException {
		try {
			buildDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
			if(StringUtils.isNotEmpty(moduleName)) {
				targetDir = new File(baseDir.getPath() + File.separator + moduleName + DO_NOT_CHECKIN_FOLDER + File.separator + TARGET);
			} else {
				targetDir = new File(project.getBuild().getDirectory());
			}
			baseDir = getProjectRoot(baseDir);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			buildInfoFile = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY + BUILD_INFO_FILE);
			File buildInfoDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
			if (!buildInfoDir.exists()) {
				buildInfoDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			nextBuildNo = util.generateNextBuildNo(buildInfoFile);
			currentDate = Calendar.getInstance().getTime();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private File getProjectRoot(File childDir) {
		File[] listFiles = childDir.listFiles(new PhrescoDirFilter());
		if (listFiles != null && listFiles.length > 0) {
			return childDir;
		}
		if (childDir.getParentFile() != null) {
			return getProjectRoot(childDir.getParentFile());
		}
		return null;
	}

	public class PhrescoDirFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.equals(DOT_PHRESCO_FOLDER);
		}
	}

	private void updateFinalName() throws MojoExecutionException {
		try {
			File pom = project.getFile();
			PomProcessor pomprocessor = new PomProcessor(pom);
			String envName = environmentName;
			List<String> envList = pu.csvToList(environmentName);

			if (environmentName.indexOf(',') > -1) { // multi-value
				envName = readDefaultEnv(envList);
			}
			List<com.photon.phresco.configuration.Configuration> configurations = pu.getConfiguration(baseDir, envName, Constants.SETTINGS_TEMPLATE_SERVER);
			if(CollectionUtils.isNotEmpty(configurations)) {
				for (com.photon.phresco.configuration.Configuration configuration : configurations) {
					context = configuration.getProperties().getProperty(Constants.SERVER_CONTEXT);
					break;
				}
			}
			sourceDir = pomprocessor.getProperty(POM_PROP_KEY_SOURCE_DIR);
			if (StringUtils.isEmpty(context)) {
				return;
			}
			pomprocessor.setFinalName(context);
			pomprocessor.save();
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	public String readDefaultEnv(List<String> envList) throws MojoExecutionException {
		boolean defaultEnv = false;
		String defaultEnvName = "";
		ConfigManager configManager = null;
		try {
			String customerId = pu.readCustomerId(baseDir);
			File settingsXml = new File(Utility.getProjectHome() + customerId + Constants.SETTINGS_XML);
			if (settingsXml.exists()) {
				configManager = new ConfigManagerImpl(new File(Utility.getProjectHome() + customerId
						+ Constants.SETTINGS_XML));
				List<Environment> settingsEnvironments = configManager.getEnvironments(envList);
				for (Environment environment : settingsEnvironments) {
					defaultEnv = environment.isDefaultEnv();
					if (defaultEnv) {
						defaultEnvName = environment.getName();
					}
				}
			}
			if (!defaultEnv) {
				configManager = new ConfigManagerImpl(new File(baseDir.getPath() + File.separator
						+ Constants.DOT_PHRESCO_FOLDER + File.separator + Constants.CONFIGURATION_INFO_FILE));
				List<Environment> configurationEnvironments = configManager.getEnvironments(envList);
				for (Environment configEnvironment : configurationEnvironments) {
					defaultEnv = configEnvironment.isDefaultEnv();
					if (defaultEnv) {
						defaultEnvName = configEnvironment.getName();
					}
				}
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ConfigurationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return defaultEnvName;
	}
	
	private void getMavenCommands(Configuration configuration) {
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if(parameter.getPluginParameter() != null && parameter.getMavenCommands() != null) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if(parameter.getValue().equals(mavenCommand.getKey())) {
						builder.append(mavenCommand.getValue());
						builder.append(STR_SPACE);
					}
				}
			}
		}
	}
	
	private void executeMvnPackage() throws MojoExecutionException, IOException {
		log.info("Packaging the project...");
		BufferedReader bufferedReader = null;
		StringBuilder sb = new StringBuilder();
		sb.append(MVN_CMD);
		sb.append(STR_SPACE);
		sb.append(MVN_PHASE_CLEAN);
		sb.append(STR_SPACE);
		sb.append(MVN_PHASE_PACKAGE);
		if(!Constants.POM_NAME.equals(pomName)) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomName);
		}
		sb.append(STR_SPACE);
		sb.append(builder.toString());
		String line ="";
		String processName = ManagementFactory.getRuntimeMXBean().getName();
		String[] split = processName.split("@");
		String processId = split[0].toString();
		Utility.writeProcessid(baseDir.getPath(), "package", processId);
		bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
		System.out.println("Build Command : " + sb.toString());
		while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line); //do not use getLog() here as this line already contains the log type.
			}
	}

	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {
			log.info("Building the project...");
			createPackage();
		} catch (Exception e) {
			isBuildSuccess = false;
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return isBuildSuccess;
	}

	private void createPackage() throws MojoExecutionException {
		if(StringUtils.isNotEmpty(moduleName) && JAR.equals(packagingType) || StringUtils.isEmpty(packagingType)) {
			return;
		}
		try {
			zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			String zipNameWithoutExt = zipName.substring(0, zipName.lastIndexOf('.'));
			copyJarToPackage(zipNameWithoutExt); // jar package
			ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void copyJarToPackage(String zipNameWithoutExt) throws MojoExecutionException {
		try {
			String[] list = targetDir.list(new JarFileNameFilter());
			if (list.length > 0) {
				File jarFile = new File(targetDir.getPath() + File.separator + list[0]);
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				FileUtils.copyFileToDirectory(jarFile, tempDir);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		if(StringUtils.isNotEmpty(moduleName) && JAR.equals(packagingType) || StringUtils.isEmpty(packagingType)) {
			return;
		}
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			if(tempDir != null && tempDir.exists()) {
				FileUtils.deleteDirectory(tempDir);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}

class JarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".jar");
	}

}
