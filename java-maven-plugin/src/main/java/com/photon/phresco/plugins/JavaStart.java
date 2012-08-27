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
package com.photon.phresco.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.google.gson.Gson;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.model.SettingsInfo;
import com.photon.phresco.plugin.commons.ConfigurationInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.util.PomProcessor;

/**
 * Goal which builds the Java WebApp
 * 
 * @goal start
 * 
 */
public class JavaStart extends AbstractMojo implements PluginConstants {

	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * @parameter expression="${project.basedir}" required="true"
	 * @readonly
	 */
	protected File baseDir;

	/**
	 * @parameter expression="${importSql}" required="true"
	 */
	protected boolean importSql;
	
	/**
	 * @parameter expression="${environmentName}" required="true"
	 */
	protected String environmentName;
	
	/**
	 * @parameter expression="${projectCode}" required="true"
	 */
	protected String projectCode;

	/**
	 * @parameter expression="${port}" required="true"
	 */
	protected String port;

	/**
	 * @parameter expression="${context}" required="true"
	 */
	protected String context;
	
	/**
	 * @parameter expression="${projectModule}" required="true"
	 */
	protected String projectModule;
	
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (environmentName != null) {
			updateFinalName();
			configure();
			storeEnvName();
		}
		createDb();
	    executePhase();
	}

	private void updateFinalName() throws MojoExecutionException {
		try {
			File pom = project.getFile();
			PomProcessor pomprocessor = new PomProcessor(pom);
			pomprocessor.setFinalName(context);
			pomprocessor.save();
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void configure() throws MojoExecutionException {
		getLog().info("Configuring the project....");
		adaptSourceConfig();
		adaptDbConfig();
	}
	
	private void storeEnvName() throws MojoExecutionException {
		ConfigurationInfo info = new ConfigurationInfo();
		info.setContext(context);
		info.setEnvironmentName(environmentName);
		info.setModuleName(projectModule);
		info.setServerPort(port);
		Gson gson = new Gson();
		String envName = gson.toJson(info);
		FileOutputStream fos = null;
		File pomPath = new File(Utility.getProjectHome() + File.separator + projectCode + File.separator + DOT_PHRESCO_FOLDER 
		    + File.separator + NODE_ENV_FILE);
		try {
			fos = new FileOutputStream(pomPath, false);
            fos.write(envName.getBytes());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}finally {
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
		PluginUtils util = new PluginUtils();
		try {
			if (importSql) {
				List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_DB);
				for (SettingsInfo databaseDetails : settingsInfos) {
					String databaseType = databaseDetails.getPropertyInfo(Constants.DB_TYPE).getValue();
					util.getSqlFilePath(databaseDetails,getProjectHome(), databaseType);
				}
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void adaptDbConfig() throws MojoExecutionException {
		File dbConfigFile = new File(baseDir + JAVA_CONFIG_FILE);
		File parentFile = dbConfigFile.getParentFile();
		String basedir = projectCode;
		if (parentFile.exists()) {
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, basedir, dbConfigFile);
		}
	}
	
	private void adaptSourceConfig() throws MojoExecutionException {
		File wsConfigFile = new File(getProjectHome()+ File.separator + CONFIG_FILE);
		File parentFile = wsConfigFile.getParentFile();
		String basedir = projectCode;
		if (parentFile.exists()) {
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, basedir, wsConfigFile);
		}
	}

	private void executePhase() throws MojoExecutionException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);			
			sb.append(STR_SPACE);
			sb.append(JAVA_TOMCAT_RUN);
			sb.append(STR_SPACE);
			sb.append("-Dserver.port=");
			sb.append(port);	
			ProcessBuilder pb = new ProcessBuilder(BASH, "-c", sb.toString());
			pb.directory(getProjectHome());
			Process process = pb.start();
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		getLog().info("Server started successfully...");
		getLog().info("Server running at http://localhost:" + port + "/" + context); 
}
	private List<SettingsInfo> getSettingsInfo(String configType) throws PhrescoException {
		ProjectAdministrator projAdmin = PhrescoFrameworkFactory.getProjectAdministrator();
		return projAdmin.getSettingsInfos(configType, projectCode, environmentName);
	}
	
	private File getProjectHome() {
		File basePath = new File(Utility.getProjectHome() + File.separator + projectCode);
		return basePath;
	}
}