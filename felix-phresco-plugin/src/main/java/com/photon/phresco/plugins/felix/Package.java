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
package com.photon.phresco.plugins.felix;

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
import com.photon.phresco.commons.model.ApplicationInfo;
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
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class Package implements PluginConstants {
	
	private MavenProject project;
	private File baseDir;
	private String environmentName;
	private String buildName;
	private String buildNumber;
	private int buildNo;
	private String mainClassName;
	private String jarName;
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
	private String packageType;
	private String packagingType;
	private String subModule = "";
	private File workingDirectory;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        project = mavenProjectInfo.getProject();
        pomName = getPomFile().getName();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        jarName = configs.get(JAR_NAME);
        mainClassName = configs.get(MAIN_CLASS_NAME);
        util = new PluginPackageUtil();
        pu = new PluginUtils();
        builder = new StringBuilder();
        packageType = configs.get("packageType");
        subModule = mavenProjectInfo.getModuleName();
        PluginUtils.checkForConfigurations(baseDir, environmentName);
        packagingType = getPackagingType();
        if (StringUtils.isNotEmpty(subModule)) {
        	workingDirectory = new File(baseDir.getPath() + File.separator + subModule);
        } else {
        	workingDirectory = new File(baseDir.getPath());
        }
        try { 
			init();
			if (environmentName != null) {
				//updateFinalName();
				configure();
			}
			
			getMavenCommands(configuration);
			executeMvnPackage();
			if (!"pom".equals(packagingType)) {
				boolean buildStatus = build();
				writeBuildInfo(buildStatus);
			}
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
		if(StringUtils.isNotEmpty(subModule)) {
			builder.append(subModule);
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
	
	private void setFileSetExcludes(WarConfigProcessor configProcessor, String FileSetId, List<String> exclues) throws PhrescoException {
		try {
			FileSet fileSet = configProcessor.getFileSet(FileSetId);
			if(fileSet.getExcludes() == null) {
				Excludes excludes = new Excludes();
				fileSet.setExcludes(excludes);
			}
			for (String exclue : exclues) {
				fileSet.getExcludes().getExclude().add(exclue);
			}
		} catch (JAXBException e) {
			throw new PhrescoException(e);
		} 
	}
	
	private void includeLibraryJsFiles(WarConfigProcessor configProcessor) throws PhrescoException {
		try {
			FileSet fileSet = configProcessor.getFileSet("includeLibJs");
			if (fileSet == null) {
				FileSet fs = new FileSet();
				fs.setId("includeLibJs");
				fs.setDirectory("src/main/webapp");
				fs.setOutputDirectory("/");
				
				List<String> inlcudeFiles = new ArrayList<String>();
				inlcudeFiles.add("/js/**/lib/**/*-min.js");
				inlcudeFiles.add("/js/**/lib/**/*.min.js");
				inlcudeFiles.add("/js/**/libs/**/*-min.js");
				inlcudeFiles.add("/js/**/libs/**/*.min.js");
				
				Includes includes = new Includes();
				fs.setIncludes(includes);
				
				for (String inlcudeFile : inlcudeFiles) {
					fs.getIncludes().getInclude().add(inlcudeFile);
				}
				configProcessor.createFileSet(fs);
			}
		} catch (JAXBException e) {
			throw new PhrescoException(e);
		} 
	}
	
	private void emptyFileSetExclude(WarConfigProcessor configProcessor, String FileSetId) throws PhrescoException {
		try {
			FileSet fileSet = configProcessor.getFileSet(FileSetId);
			fileSet.setExcludes(null);
		} catch (JAXBException e) {
			throw new PhrescoException(e);
		}
	}
	
	private void init() throws MojoExecutionException {
		try {
			buildDir = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY);
			if(StringUtils.isNotEmpty(subModule)) {
				targetDir = new File(baseDir.getPath() + File.separator + subModule + DO_NOT_CHECKIN_FOLDER + File.separator + TARGET);
			} else {
				targetDir = new File(project.getBuild().getDirectory());
			}
			baseDir = getProjectRoot(baseDir);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			buildInfoFile = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY + BUILD_INFO_FILE);
			File buildInfoDir = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY);
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
			if(isJarProject(project)) {
				context = jarName;
				updatemainClassName();
			} else {
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
	
	private boolean isJarProject(MavenProject project) throws PhrescoPomException, PhrescoException {
		boolean jarProject = true;
		List<String> modules = PluginUtils.getProjectModules(project);
		if(CollectionUtils.isEmpty(modules)) {
			if(project.getModel().getPackaging().equals(PACKAGING_TYPE_WAR)) {
				jarProject = false;
			}
		}
		if(CollectionUtils.isNotEmpty(modules)) {
			for (String mavenProject : modules) {
				File pomFile = new File(project.getBasedir(), File.separator
						+ mavenProject + File.separator + pomName);
				if (pomFile.exists()) {
					PomProcessor processor = new PomProcessor(pomFile);
					if (processor.getPackage().equals(PACKAGING_TYPE_WAR)) {
						jarProject = false;
						break;
					}
				}
			}
		}
		return jarProject;
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
	
	private void updatemainClassName() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(mainClassName)) {
				return;
			}
			File pom = project.getFile();
			List<Element> configList = new ArrayList<Element>();
			PomProcessor pomprocessor = new PomProcessor(pom);
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element archive = doc.createElement(JAVA_POM_ARCHIVE);
			Element manifest = doc.createElement(JAVA_POM_MANIFEST);
			Element addClasspath = doc.createElement(JAVA_POM_ADD_PATH);
			addClasspath.setTextContent("true");
			manifest.appendChild(addClasspath);
			Element mainClass = doc.createElement(JAVA_POM_MAINCLASS);
			mainClass.setTextContent(mainClassName);
			manifest.appendChild(addClasspath);
			manifest.appendChild(mainClass);
			archive.appendChild(manifest);
			configList.add(archive);

			pomprocessor.addConfiguration(JAR_PLUGIN_GROUPID, JAR_PLUGIN_ARTIFACT_ID, configList, false);
			pomprocessor.save();
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
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
		sb.append(MVN_PHASE_INSTALL);
		if(!Constants.POM_NAME.equals(pomName)) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomName);
		}
		sb.append(STR_SPACE);
		sb.append(builder.toString());
		if (StringUtils.isNotEmpty(subModule)) {
			sb.append(HYPHEN_AM_HYPHEN_PL + subModule);
		}
		String line ="";
		String processName = ManagementFactory.getRuntimeMXBean().getName();
		String[] split = processName.split("@");
		String processId = split[0].toString();
		Utility.writeProcessid(baseDir.getPath(), "package", processId);
		bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
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

	private void configure() throws MojoExecutionException {
		log.info("Configuring the project....");
		try {
			adaptSourceConfig();
			pu.writeDatabaseDriverToConfigXml(workingDirectory, sourceDir, environmentName);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void adaptSourceConfig() throws MojoExecutionException {
		PomProcessor processor  = null;
		File sourceConfigXML = null;
		try {
			processor = new PomProcessor(getPomFile());
			String configXml = processor.getProperty(POM_PROP_CONFIG_FILE);
			if(StringUtils.isNotEmpty(configXml)) {
				sourceConfigXML = new File(workingDirectory + configXml);
			} else {
				sourceConfigXML = new File(workingDirectory + sourceDir + FORWARD_SLASH +  CONFIG_FILE);
			}
			File parentFile = sourceConfigXML.getParentFile();
			if (parentFile.exists()) {
				pu.executeUtil(environmentName, workingDirectory.getPath(), sourceConfigXML);
			}
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private File getPomFile() throws PhrescoException {
		ApplicationInfo appInfo = pu.getAppInfo(workingDirectory);
		String pomFileName = Utility.getPomFileNameFromWorkingDirectory(appInfo, workingDirectory);
		File pom = new File(workingDirectory.getPath() + File.separator + pomFileName);
		
		return pom;
	}
	
	private void createPackage() throws MojoExecutionException {
		if(StringUtils.isEmpty(packagingType)) {
			return;
		}
		try {
			zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			String zipNameWithoutExt = zipName.substring(0, zipName.lastIndexOf('.'));
			File packageInfoFile = new File(workingDirectory.getPath() + File.separator + DOT_PHRESCO_FOLDER + File.separator + PHRESCO_PACKAGE_FILE);
			
			if ("war".equals(packagingType)) {
				if("zip".equals(packageType)) {
					copyZipToPackage(zipNameWithoutExt);
					return;
				} else {
					copyWarToPackage(zipNameWithoutExt);
				}
			} else {
				copyJarToPackage(zipNameWithoutExt);
			}
			PluginUtils.createBuildResources(packageInfoFile, workingDirectory, tempDir);
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

	private void copyWarToPackage(String zipNameWithoutExt) throws MojoExecutionException {
		try {
			String[] list = targetDir.list(new WarFileNameFilter());
			if (list.length > 0) {
				File warFile = new File(targetDir.getPath() + File.separator + list[0]);
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
//				File contextWarFile = new File(targetDir.getPath() + File.separator + context + ".war");
//				warFile.renameTo(contextWarFile);
				FileUtils.copyFileToDirectory(warFile, tempDir);
			} else {
				throw new MojoExecutionException(".war not found in " + targetDir.getPath());
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void copyZipToPackage(String zipNameWithoutExt) throws MojoExecutionException {
		try {
			String[] list = targetDir.list(new ZipFileNameFilter());
			if (list.length > 0) {
				File zipFile = new File(targetDir.getPath() + File.separator + list[0]);
				if(!buildDir.exists()) {
					buildDir.mkdirs();
				}
				FileUtils.copyFileToDirectory(zipFile, buildDir);
				File contextZipFile = new File(buildDir.getPath() + File.separator + zipNameWithoutExt + ".zip");
				File buildZipFile = new File(buildDir, zipFile.getName());
				buildZipFile.renameTo(contextZipFile);
			} else {
				throw new MojoExecutionException(".war not found in " + targetDir.getPath());
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		if(StringUtils.isEmpty(packagingType)) {
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

class WarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".war");
	}
}

class ZipFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".zip");
	}
}

class JarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".jar");
	}

}
