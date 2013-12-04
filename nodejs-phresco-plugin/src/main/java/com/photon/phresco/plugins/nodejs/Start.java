/**
 * nodejs-phresco-plugin
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
package com.photon.phresco.plugins.nodejs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.google.gson.Gson;
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
import com.phresco.pom.util.PomProcessor;

public class Start implements PluginConstants {
	private File baseDir;
	private MavenProject project;
	private boolean importSql;
	private String environmentName;
	private Log log;
	private String sqlPath;
	private PluginUtils pUtil;
	private String dotPhrescoDirName;
	private File dotPhrescoDir;
	private File srcDirectory;
	private File pomFile;

	public void start(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		pomFile = project.getFile();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		importSql = Boolean.parseBoolean(configs.get(EXECUTE_SQL));
	    sqlPath = configs.get(FETCH_SQL);
	    pUtil = new PluginUtils();
	    dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		dotPhrescoDir = baseDir;
		if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
			dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
		}
		srcDirectory = baseDir;
		File splitProjectDirectory = pUtil.getSplitProjectDirectory(pomFile, dotPhrescoDir, "");
		if (splitProjectDirectory != null) {
			srcDirectory = splitProjectDirectory;
		}
	    PluginUtils.checkForConfigurations(dotPhrescoDir, environmentName);
		try {
			configure();
			storeEnvName();
			createDb();
			startNodeJS();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}

	private void configure() throws MojoExecutionException {
		try {
			log.info("Configuring the project....");
			PomProcessor pomProcessor = new PomProcessor(pomFile);
			String sourceDir = pomProcessor.getProperty(POM_PROP_KEY_SOURCE_DIR);
			File ConfigFile = new File(srcDirectory + sourceDir + FORWARD_SLASH +  CONFIG_FILE);
			pUtil.executeUtil(environmentName, dotPhrescoDir.getPath(), ConfigFile);
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void createDb() throws MojoExecutionException {
		DatabaseUtil util = new DatabaseUtil();
		try {
			util.fetchSqlConfiguration(sqlPath, importSql, srcDirectory, environmentName, dotPhrescoDir);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void startNodeJS() throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		InputStreamReader isr = null;
		FileWriter fileWriter = null;
		String serverhost = null;
		int serverport = 0;
		String serverProtocol = null;
		String serverContext = null;
		try {
			boolean tempConnectionAlive = false;
			StringBuilder sb = new StringBuilder();
			sb.append(NODE_CMD);
			sb.append(STR_SPACE);
			sb.append(NODE_SERVER_FILE);
			sb.append(STR_SPACE);
			sb.append(environmentName);
			bufferedReader = Utility.executeCommand(sb.toString(), srcDirectory.getPath() + File.separator + PROJECT_FOLDER);
			File file = new File(baseDir.getPath() + LOG_FILE_DIRECTORY);
			if (!file.exists()) {
				file.mkdirs();
			}
			fileWriter = new FileWriter(baseDir.getPath() + LOG_FILE_DIRECTORY + RUN_AGS_LOG_FILE, false);
			LogWriter logWriter = new LogWriter();
			List<com.photon.phresco.configuration.Configuration> configurations = pUtil.getConfiguration(dotPhrescoDir, environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			if(CollectionUtils.isEmpty(configurations)) {
				throw new PhrescoException("Configuration is Empty...");
			}
			for (com.photon.phresco.configuration.Configuration serverConfiguration : configurations) {
				serverhost = serverConfiguration.getProperties().getProperty(Constants.SERVER_HOST);
				serverport = Integer.parseInt(serverConfiguration.getProperties().getProperty(Constants.SERVER_PORT));
				serverProtocol = serverConfiguration.getProperties().getProperty(Constants.SERVER_PROTOCOL);
				serverContext = serverConfiguration.getProperties().getProperty(Constants.SERVER_CONTEXT);
			}

			tempConnectionAlive = isConnectionAlive(serverProtocol, serverhost, serverport);
			if (tempConnectionAlive) {
				log.info("server started");
				log.info("Server running at " + serverProtocol + "://" + serverhost + ":" + serverport + "/"
						+ serverContext);
			} else {
				throw new PhrescoException("Server startup failed");
			}
			logWriter.writeLog(bufferedReader, fileWriter);
		} catch (Exception e) {
			log.error("Server startup failed");
			throw new MojoExecutionException(e.getMessage());
		} finally {
			Utility.closeStream(bufferedReader);
			Utility.closeStream(isr);
			Utility.closeStream(fileWriter);
		}
	}

	private void storeEnvName() throws MojoExecutionException {
		File file = new File(dotPhrescoDir.getPath() + File.separator + DOT_PHRESCO_FOLDER + File.separator + ENV_FILE);
		ConfigurationInfo info = new ConfigurationInfo();
		info.setEnvironmentName(environmentName);
		Gson gson = new Gson();
		String envName = gson.toJson(info);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, false);
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

	private static boolean isConnectionAlive(String protocol, String host, int port) {
		boolean isAlive = true;
		try {
			Thread.sleep(3000);
			URL url = new URL(protocol, host, port, "");
			URLConnection connection = url.openConnection();
			connection.connect();
		} catch (Exception e) {
			isAlive = false;
		}
		return isAlive;
	}

}