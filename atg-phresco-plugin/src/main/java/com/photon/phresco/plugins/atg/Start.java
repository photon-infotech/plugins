/**
 * atg-phresco-plugin
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
package com.photon.phresco.plugins.atg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

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
import com.phresco.pom.util.PomProcessor;


public class Start implements PluginConstants, AtgConstants {

	private String environmentName;
	private MavenProject project;
	private String serverPort;
	private String serverContext;
	private File baseDir;
	private File workingDirectory;
	private String subModule = "";
	private PluginUtils pu;
	private String dotPhrescoDirName;
	private File dotPhrescoDir;
	private String atgPath;
	private String atgServer;
	private String jbossServer;
	private String defaultModules;
    private String otherModules;
	public void start(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		pu = new PluginUtils();
		workingDirectory = baseDir;
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			subModule = mavenProjectInfo.getModuleName();
			workingDirectory = new File(baseDir + File.separator + subModule);
		}
		dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		dotPhrescoDir = baseDir;
		if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
			dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
		}
		dotPhrescoDir = new File(dotPhrescoDir.getPath() + File.separatorChar + subModule);
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		atgPath = configs.get(ATG_PATH);
		atgServer = configs.get(ATG_SERVER_NAME);
		jbossServer = configs.get(JBOSS_SERVER_NAME);
		defaultModules = configs.get(DEFAULT_MODULES);
        otherModules = configs.get(OTHER_MODULES);
		PluginUtils.checkForConfigurations(dotPhrescoDir, environmentName);
		try {
			if (environmentName != null) {
				updateFinalName();
				storeEnvName();
			}
			executePhase();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
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
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
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

	private void executePhase() throws MojoExecutionException {
		FileOutputStream fos = null;
		File errorLog = new File(workingDirectory + File.separator + LOG_FILE_DIRECTORY + RUN_AGS_LOG_FILE);
		try {
			String homeDirectory = atgPath + File.separator + ATG_HOME + File.separator + ATG_BIN;
			ApplicationInfo appInfo = pu.getAppInfo(dotPhrescoDir);
			StringBuilder sb = new StringBuilder();
			sb.append(START_DYNAMO_ON_JBOSS);
			sb.append(WHITESPACE);
			sb.append(atgServer);
			sb.append(WHITESPACE);
			sb.append(SERVER_FLAG);
			sb.append(jbossServer);
			sb.append(WHITESPACE);
			sb.append(ADD_FLAG);
			sb.append(RUN_IN_PLACE);
			sb.append(WHITESPACE);
			sb.append(MODULES);
			if(defaultModules != null) {
				String[] defaultModulesList = defaultModules.split(COMMA);
				for(String module : defaultModulesList) {
					sb.append(module);
					sb.append(WHITESPACE);
				}
			}
			if(otherModules != null) {
				String[] otherModulesList = otherModules.split(COMMA);
				for(String module : otherModulesList) {
					sb.append(module);
					sb.append(WHITESPACE);
				}
			}
			sb.append(appInfo.getAppDirName());
			fos = new FileOutputStream(errorLog, false);
			Utility.executeStreamconsumerFOS(homeDirectory.toString(),sb.toString(), fos);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} 
	}
}