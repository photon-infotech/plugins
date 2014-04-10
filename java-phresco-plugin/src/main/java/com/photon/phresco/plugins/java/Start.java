/**
 * java-phresco-plugin
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
package com.photon.phresco.plugins.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginConfigurationException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginNotFoundException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;

import com.google.gson.Gson;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.configuration.ConfigurationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.DatabaseUtil;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Build;
import com.phresco.pom.model.Model;
import com.phresco.pom.util.PomProcessor;


public class Start implements PluginConstants {

	private String environmentName;
	private MavenProject project;
	private String serverPort;
	private String serverContext;
	private File baseDir;
	private File workingDirectory;
	private String subModule = "";
	private Log log;
	private String sourceDir;
	private boolean importSql;
	private String sqlPath;
	private PluginUtils pu;
	private File pomFile;
	private String pomFileName;
	private String dotPhrescoDirName;
	private File dotPhrescoDir;
	private File srcDirectory;
	private ApplicationInfo appInfo;
	private MavenSession mavenSession;
    private BuildPluginManager pluginManager;
	 
	public void start(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		mavenSession = mavenProjectInfo.getMavenSession();
	    pluginManager = mavenProjectInfo.getPluginManager();
		pomFile = project.getFile();
		pomFileName = project.getFile().getName();
		pu = new PluginUtils();
		workingDirectory = baseDir;
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			subModule = mavenProjectInfo.getModuleName();
			workingDirectory = new File(baseDir + File.separator + subModule);
			pomFile = new File(workingDirectory.getPath() + File.separatorChar + pomFile.getName());
		}
		dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		dotPhrescoDir = baseDir;
		if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
			dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
		}
		dotPhrescoDir = new File(dotPhrescoDir.getPath() + File.separatorChar + subModule);
		srcDirectory = workingDirectory;
		File splitProjectDirectory = pu.getSplitProjectSrcDir(pomFile, dotPhrescoDir, subModule);
		if (splitProjectDirectory != null) {
			srcDirectory = splitProjectDirectory;
		}
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		importSql = Boolean.parseBoolean(configs.get(EXECUTE_SQL));
		sqlPath = configs.get(FETCH_SQL);
		PluginUtils.checkForConfigurations(dotPhrescoDir, environmentName);
		try {
			if (environmentName != null) {
				updateFinalName();
				configure();
				storeEnvName();
			}
			createDb();
			appInfo = pu.getAppInfo(dotPhrescoDir);
			if(project.getFile().getName().equals(appInfo.getPhrescoPomFile())) {
				installArtifact(baseDir, "");
			}
			executePhase();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}
	
	
	private void installArtifact(File currentDir, String module) throws PhrescoException {
		String srcDir = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
		if (StringUtils.isNotEmpty(srcDir)) {
    		String appDirName = appInfo.getAppDirName();
			currentDir = new File(Utility.getProjectHome() + File.separatorChar + appDirName + File.separatorChar + srcDir + File.separatorChar + module);
		}
		try { 
			File phrescoPom = pu.getPomFile(new File(dotPhrescoDir.getPath() + File.separatorChar + module), new File(baseDir.getPath() + File.separatorChar + module));
			PomProcessor phrescoPomProcessor = new PomProcessor(phrescoPom);
			File pomFile = new File(currentDir, phrescoPomProcessor.getProperty("source.pom"));
			PomProcessor processor = new PomProcessor(pomFile);
			String packagingSrcPOm = processor.getModel().getPackaging();
			if(StringUtils.isEmpty(packagingSrcPOm)) {
				packagingSrcPOm = "jar";
			}
			Plugin plugin = new Plugin();
			plugin.setGroupId("org.apache.maven.plugins");
			plugin.setArtifactId("maven-install-plugin");
			plugin.setVersion("2.4");
			PluginDescriptor pluginDescriptor = pluginManager.loadPlugin(plugin, project.getRemotePluginRepositories(),
					mavenSession.getRepositorySession());
			pluginDescriptor.setInheritedByDefault(true);
			MojoDescriptor mojoDescriptor = pluginDescriptor.getMojo("install-file");
			mojoDescriptor.setInheritedByDefault(true);
			Xpp3Dom configuration = new Xpp3Dom("configuration");
			Xpp3Dom packagingConf = new Xpp3Dom("packaging");
			packagingConf.setValue(packagingSrcPOm);
			configuration.addChild(packagingConf);
			Xpp3Dom artifactId = new Xpp3Dom("artifactId");
			artifactId.setValue(processor.getArtifactId());
			configuration.addChild(artifactId);
			Xpp3Dom groupId = new Xpp3Dom("groupId");
			groupId.setValue(processor.getGroupId());
			configuration.addChild(groupId);
			Xpp3Dom version = new Xpp3Dom("version");
			version.setValue(processor.getVersion());
			configuration.addChild(version);
			Xpp3Dom repositoryLayout = new Xpp3Dom("repositoryLayout");
			repositoryLayout.setValue("default");
			configuration.addChild(repositoryLayout);
			Xpp3Dom file = new Xpp3Dom("file");
			String finalName = "";
			String buildDir = "";
			if(phrescoPom.exists()) {
				finalName = phrescoPomProcessor.getFinalName();
				if(StringUtils.isEmpty(finalName)) {
					finalName = project.getBuild().getFinalName();
				}
				Model model = phrescoPomProcessor.getModel();
				Build build = model.getBuild();
				if(build != null) {
					buildDir = build.getDirectory();
				}
				if(StringUtils.isEmpty(buildDir)) {
					buildDir = project.getBuild().getDirectory();
				}
			}
			String fileConfig = "";
			StringBuilder fileString = new StringBuilder();
			if(StringUtils.isNotEmpty(module)) {
				fileString.append(module);
				fileString.append("/");
			}
			fileString.append(buildDir).append("/").append(finalName).append(".").append(packagingSrcPOm);
			fileConfig = fileString.toString();
			if("pom".equals(packagingSrcPOm)) {
				fileConfig = project.getProperties().getProperty("source.pom");
			}
			file.setValue(pomFile.getPath());
			configuration.addChild(file);
			configuration = Xpp3DomUtils.mergeXpp3Dom(configuration, convertPlexusConfiguration(mojoDescriptor.getMojoConfiguration()));
            MojoExecution exec = new MojoExecution(mojoDescriptor, configuration);
            pluginManager.executeMojo(mavenSession, exec);
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		} catch (PluginNotFoundException e) {
			throw new PhrescoException(e);
		} catch (PluginResolutionException e) {
			throw new PhrescoException(e);
		} catch (PluginDescriptorParsingException e) {
			throw new PhrescoException(e);
		} catch (InvalidPluginDescriptorException e) {
			throw new PhrescoException(e);
		} catch (MojoFailureException e) {
			throw new PhrescoException(e);
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		} catch (PluginConfigurationException e) {
			throw new PhrescoException(e);
		} catch (PluginManagerException e) {
			throw new PhrescoException(e);
		}
	}
	
	 private Xpp3Dom convertPlexusConfiguration(PlexusConfiguration config) {
		    Xpp3Dom xpp3DomElement = new Xpp3Dom(config.getName());
		    xpp3DomElement.setValue(config.getValue());
		    for (String name : config.getAttributeNames()) {
		      xpp3DomElement.setAttribute(name, config.getAttribute(name));
		    }
		    for (PlexusConfiguration child : config.getChildren()) {
		      xpp3DomElement.addChild(convertPlexusConfiguration(child));
		    }
		    return xpp3DomElement;
		  }
	

	private void updateFinalName() throws MojoExecutionException {
		try {
			List<com.photon.phresco.configuration.Configuration> configuration = pu.getConfiguration(dotPhrescoDir, environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			if(CollectionUtils.isEmpty(configuration)) {
				throw new PhrescoException("Configuration is Empty...");
			}
			for (com.photon.phresco.configuration.Configuration serverConfiguration : configuration) {
				serverPort = serverConfiguration.getProperties().getProperty(Constants.SERVER_PORT);
				serverContext = serverConfiguration.getProperties().getProperty(Constants.SERVER_CONTEXT);
			}
			PomProcessor pomprocessor = new PomProcessor(pomFile);
			sourceDir = pomprocessor.getProperty(POM_PROP_KEY_SOURCE_DIR);
			pomprocessor.setFinalName(serverContext);
			pomprocessor.save();
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void configure() throws MojoExecutionException {
		log.info("Configuring the project....");
		try {
			adaptSourceConfig();
			pu.writeDatabaseDriverToConfigXml(srcDirectory, sourceDir, environmentName);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void storeEnvName() throws MojoExecutionException {
		ConfigurationInfo info = new ConfigurationInfo();
		info.setContext(serverContext);
		info.setEnvironmentName(environmentName);
		info.setServerPort(serverPort);
		Gson gson = new Gson();
		String envName = gson.toJson(info);
		FileOutputStream fos = null;
		File runAgstSrcFile = new File(dotPhrescoDir + File.separator + DOT_PHRESCO_FOLDER + File.separator + ENV_FILE);
		try {
			fos = new FileOutputStream(runAgstSrcFile, false);
			fos.write(envName.getBytes());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				throw new MojoExecutionException(e.getMessage());
			}
		}
	}

	private void createDb() throws MojoExecutionException {
		DatabaseUtil util = new DatabaseUtil();
		try {
			util.fetchSqlConfiguration(sqlPath, importSql, srcDirectory, environmentName, dotPhrescoDir);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void adaptSourceConfig() throws MojoExecutionException {
		File wsConfigFile = new File(dotPhrescoDir + sourceDir + FORWARD_SLASH +  CONFIG_FILE);
		File parentFile = wsConfigFile.getParentFile();
		try {
			if (parentFile.exists()) {
				pu.executeUtil(environmentName, dotPhrescoDir.getPath(), wsConfigFile);
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void executePhase() throws MojoExecutionException {
		FileOutputStream fos = null;
		File errorLog = new File(workingDirectory + File.separator + LOG_FILE_DIRECTORY + RUN_AGS_LOG_FILE);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(JAVA_TOMCAT_RUN);
			sb.append(STR_SPACE);
			sb.append(SERVER_PORT);
			sb.append(serverPort);
			sb.append(STR_SPACE);
			sb.append(SERVER_ENV);
			sb.append(environmentName);
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomFileName);
			fos = new FileOutputStream(errorLog, false);
			Utility.executeStreamconsumerFOS(workingDirectory.toString(),sb.toString(), fos);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} 
	}
}