/*
 * ###
 * java-maven-plugin Maven Mojo
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
package com.photon.phresco.plugins.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.google.gson.Gson;
import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.configuration.ConfigurationInfo;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.plugin.commons.DatabaseUtil;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class Start implements PluginConstants {

	/**
	 * @parameter expression="${projectModule}" required="true"
	 */
	protected String projectModule;

	private String environmentName;
	private MavenProject project;
	private String serverPort;
	private String serverContext;
	private File baseDir;
	private String projectCode;
	private Log log;
	private String sourceDir;
	private boolean importSql;
	private String sqlPath;
	private PluginUtils pu;

	public void start(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		projectCode = mavenProjectInfo.getProjectCode();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		// port = configs.get(Constants.SERVER_PORT);
		// context = configs.get(Constants.SERVER_CONTEXT);
		importSql = Boolean.parseBoolean(configs.get(EXECUTE_SQL));
	    sqlPath = configs.get(FETCH_SQL);
	    pu = new PluginUtils();
		try {
			if (environmentName != null) {
				updateFinalName();
				configure();
				writeDatabaseDriverToConfigXml();
				storeEnvName();
			}
		    createDb();
			executePhase();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}

	private void updateFinalName() throws MojoExecutionException {
		try {
			List<com.photon.phresco.configuration.Configuration> configuration = getConfiguration(Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration serverConfiguration : configuration) {
				serverPort = serverConfiguration.getProperties().getProperty(Constants.SERVER_PORT);
				serverContext = serverConfiguration.getProperties().getProperty(Constants.SERVER_CONTEXT);
			}
			File pom = project.getFile();
			PomProcessor pomprocessor = new PomProcessor(pom);
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
		adaptSourceConfig();
	}

	private void storeEnvName() throws MojoExecutionException {
		ConfigurationInfo info = new ConfigurationInfo();
		info.setContext(serverContext);
		info.setEnvironmentName(environmentName);
		info.setModuleName("");// projectModule
		info.setServerPort(serverPort);
		Gson gson = new Gson();
		String envName = gson.toJson(info);
		FileOutputStream fos = null;
		File pomPath = new File(Utility.getProjectHome() + File.separator + projectCode + File.separator
				+ DOT_PHRESCO_FOLDER + File.separator + ENV_FILE);
		try {
			fos = new FileOutputStream(pomPath, false);
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

	private void createDb() throws MojoExecutionException, PhrescoException {
		DatabaseUtil util = new DatabaseUtil();
		try {
			util.fetchSqlConfiguration(sqlPath, importSql, baseDir, environmentName);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void adaptSourceConfig() throws MojoExecutionException {
		File wsConfigFile = new File(baseDir + sourceDir + FORWARD_SLASH +  CONFIG_FILE);
		File parentFile = wsConfigFile.getParentFile();
		String basedir = projectCode;
		if (parentFile.exists()) {
			pu.executeUtil(environmentName, basedir, wsConfigFile);
		}
	}

	private void executePhase() throws MojoExecutionException {
		FileOutputStream fos = null;
		try {
			File errorLog = new File(Utility.getProjectHome() + File.separator + projectCode + File.separator
					+ LOG_FILE_DIRECTORY + RUN_AGS_LOG_FILE);
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(JAVA_TOMCAT_RUN);
			sb.append(STR_SPACE);
			sb.append("-Dserver.port=");
			sb.append(serverPort);
			fos = new FileOutputStream(errorLog, false);
			// ProcessBuilder pb = new ProcessBuilder(BASH, "-c", sb.toString());
			Utility.executeStreamconsumer(sb.toString(), fos);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	public void writeDatabaseDriverToConfigXml() throws MojoExecutionException {
		DatabaseUtil.initDriverMap();
		try {
			File configFile = new File(baseDir.getPath() + sourceDir + File.separator + Constants.CONFIGURATION_INFO_FILE);
			DatabaseUtil dbutil = new DatabaseUtil();
			ConfigManager configManager = PhrescoFrameworkFactory.getConfigManager(configFile);
			List<String> envList = pu.csvToList(environmentName);
			for (String envName : envList) {
				List<com.photon.phresco.configuration.Configuration> configuration = configManager.getConfigurations(
						envName, Constants.SETTINGS_TEMPLATE_DB);
				if(CollectionUtils.isEmpty(configuration)) {
					return;
				}
				for (com.photon.phresco.configuration.Configuration config : configuration) {
					Properties properties = config.getProperties();
					String databaseType = config.getProperties().getProperty(Constants.DB_TYPE).toLowerCase();
					String dbDriver = dbutil.getDbDriver(databaseType);
					properties.setProperty(Constants.DB_DRIVER, dbDriver);
					config.setProperties(properties);
					configManager.createConfiguration(envName, config);
					configManager.deleteConfiguration(envName, config);
				}
				configManager.writeXml(new FileOutputStream(configFile));
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ConfigurationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private List<com.photon.phresco.configuration.Configuration> getConfiguration(String configType)
			throws PhrescoException, MojoExecutionException {
		try {
			ConfigManager configManager = PhrescoFrameworkFactory.getConfigManager(new File(baseDir.getPath()
					+ File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator
					+ Constants.CONFIGURATION_INFO_FILE));
			return configManager.getConfigurations(environmentName, configType);
		} catch (ConfigurationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private File getProjectHome() {
		File basePath = new File(Utility.getProjectHome() + File.separator + projectCode);
		return basePath;
	}
}