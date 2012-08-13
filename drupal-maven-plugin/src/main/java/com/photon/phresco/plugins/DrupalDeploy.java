/*
 * ###
 * drupal-maven-plugin Maven Mojo
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.model.SettingsInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

/**
 * Goal which deploys the Drupal project
 * 
 * @goal deploy
 * 
 */
public class DrupalDeploy extends AbstractMojo implements PluginConstants {

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
	 * Build file name to deploy
	 * 
	 * @parameter expression="${buildName}" required="true"
	 */
	protected String buildName;

	/**
	 * @parameter expression="${environmentName}" required="true"
	 */
	protected String environmentName;
	
	/**
	 * @parameter expression="${importSql}" required="true"
	 */
	protected boolean importSql;
	
	private File binariesDir;
	private File buildDir;
	private File buildFile;
	private File tempDir;
	
	public void execute() throws MojoExecutionException {
		init();
		createDb();
		packDrupal();
		extractBuild();
		deploy();
		cleanUp();
	}

	private void init() throws MojoExecutionException  {
		try {
			if (StringUtils.isEmpty(buildName) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			buildFile = new File(buildDir.getPath() + File.separator + buildName);
			binariesDir = new File(baseDir.getPath() + BINARIES_DIR);
			
			String context = "";
			List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_SERVER);
			for (SettingsInfo serverDetails : settingsInfos) {
				context = serverDetails.getPropertyInfo(Constants.SERVER_CONTEXT).getValue();
				break;
			}
			tempDir = new File(buildDir.getPath() + TEMP_DIR + File.separator + context);
			tempDir.mkdirs();
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}


	private void callUsage() throws MojoExecutionException {
		getLog().error("Invalid usage.");
		getLog().info("Usage of Deploy Goal");
		getLog().info(
				"mvn drupal:deploy -DbuildName=\"Name of the build\""
				+ " -DenvironmentName=\"Multivalued evnironment names\""
				+ " -DimportSql=\"Does the deployment needs to import sql(TRUE/FALSE?)\"");
		throw new MojoExecutionException(
				"Invalid Usage. Please see the Usage of Deploy Goal");
	}
	
	private void createDb() throws MojoExecutionException {
		PluginUtils util = new PluginUtils();
		try {
			if (importSql) {
				List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_DB);
				for (SettingsInfo databaseDetails : settingsInfos) {
					String databaseType = databaseDetails.getPropertyInfo(Constants.DB_TYPE).getValue();
					util.getSqlFilePath(databaseDetails,baseDir, databaseType);
				}
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	
	private void packDrupal() throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			//fetching drupal binary from repo
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_INITIALIZE);

			bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line); // do not use getLog() here as this line already contains the log type.
				if (line.startsWith("[ERROR]")) {
					errorParam = true;
				}
			}
			if (errorParam) {
				throw new MojoExecutionException("Drupal Binary Download Failed ");
			}
			
			//packing drupal binary to build
			File drupalBinary = null;
			File[] listFiles = binariesDir.listFiles();
			for (File file : listFiles) {
				if (file.isDirectory()) {
					drupalBinary = new File(binariesDir + "/drupal");
					file.renameTo(drupalBinary);
				}
			}
			if (!drupalBinary.exists()) {
				throw new MojoExecutionException("Drupal binary not found");
			}
			if (drupalBinary != null) {
				FileUtils.copyDirectoryStructure(drupalBinary, tempDir);
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}
	
	private void extractBuild() throws MojoExecutionException {
		try {
			ArchiveUtil.extractArchive(buildFile.getPath(), tempDir.getPath() + "/sites", ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void deploy() throws MojoExecutionException {
		String deployLocation = "";
		try {
			List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_SERVER);
			for (SettingsInfo serverDetails : settingsInfos) {
				deployLocation = serverDetails.getPropertyInfo(Constants.SERVER_DEPLOY_DIR).getValue();
				break;
			}
			File deployDir = new File(deployLocation);
			if (!deployDir.exists()) {
				throw new MojoExecutionException(" Deploy Directory" + deployLocation + " Does Not Exists ");
			}
			getLog().info("Project is deploying into " + deployLocation);
			FileUtils.copyDirectoryStructure(tempDir.getParentFile(), deployDir);
			getLog().info("Project is deployed successfully");
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private List<SettingsInfo> getSettingsInfo(String configType) throws PhrescoException {
		ProjectAdministrator projAdmin = PhrescoFrameworkFactory.getProjectAdministrator();
		return projAdmin.getSettingsInfos(configType, baseDir.getName(), environmentName);
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir.getParentFile());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
