/**
 * php-phresco-plugin
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
package com.photon.phresco.plugins.php;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.DatabaseUtil;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;

public class Deploy implements PluginConstants {
	
	private File baseDir;
	private String buildNumber;
	private String environmentName;
	private boolean importSql;
	private File buildDir;
	private File buildFile;
	private File tempDir;
	private Log log;
	private String sqlPath;
    private PluginUtils pUtil;
    private String dotPhrescoDirName;
    private File dotPhrescoDir;
    private File srcDirectory;
    private MavenProject project;
	private File pomFile;
	
    public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException, JSONException {
    	this.log = log;
    	baseDir = mavenProjectInfo.getBaseDir();
    	project = mavenProjectInfo.getProject();
    	pomFile = project.getFile();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        importSql = Boolean.parseBoolean(configs.get(EXECUTE_SQL));
        sqlPath = configs.get(FETCH_SQL);
        pUtil = new PluginUtils();
        dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        dotPhrescoDir = baseDir;
        if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        	dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
        }
        srcDirectory = baseDir;
        File splitProjectDirectory = pUtil.getSplitProjectSrcDir(pomFile, dotPhrescoDir, "");
        if (splitProjectDirectory != null) {
        	srcDirectory = splitProjectDirectory;
        }
    	try {  
			init();
			createDb();
			extractBuild();
			deploy();
			cleanUp();
		} catch (MojoExecutionException e) {
			 throw new PhrescoException(e);
		}
	}
	private void init() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(buildNumber) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			BuildInfo buildInfo = pUtil.getBuildInfo(Integer.parseInt(buildNumber));
			log.info("Build Name " + buildInfo);
			
			buildDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void callUsage() throws MojoExecutionException {
		log.error("Invalid usage.");
		log.info("Usage of Deploy Goal");
		log.info(
				"mvn php:deploy -DbuildName=\"Name of the build\""
						+ " -DenvironmentName=\"Multivalued evnironment names\""
						+ " -DimportSql=\"Does the deployment needs to import sql(TRUE/FALSE?)\"");
		throw new MojoExecutionException(
				"Invalid Usage. Please see the Usage of Deploy Goal");
	}

	private void createDb() throws MojoExecutionException, PhrescoException {
		DatabaseUtil util = new DatabaseUtil();
		try {
			util.fetchSqlConfiguration(sqlPath, importSql, srcDirectory, environmentName, dotPhrescoDir);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void extractBuild() throws MojoExecutionException {
		try {
			String context = "";
			List<com.photon.phresco.configuration.Configuration> configuration = pUtil.getConfiguration(dotPhrescoDir, environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration config : configuration) {
				context = config.getProperties().getProperty(Constants.SERVER_CONTEXT);
				break;
			}		
			tempDir = new File(buildDir.getPath() + TEMP_DIR + File.separator + context);
			tempDir.mkdirs();
			ArchiveUtil.extractArchive(buildFile.getPath(), tempDir.getPath(),
					ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}
	
	private void deploy() throws MojoExecutionException {
		String deployLocation = "";
		try {
			List<com.photon.phresco.configuration.Configuration> configuration = pUtil.getConfiguration(dotPhrescoDir, environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration config : configuration) {
				deployLocation = config.getProperties().getProperty(Constants.SERVER_DEPLOY_DIR);
				break;
			}		
			File deployDir = new File(deployLocation);
			if (!deployDir.exists()) {
				throw new MojoExecutionException("Deploy Directory" + deployLocation + " Does Not Exists ");
			}
			log.info("Project is deploying into " + deployLocation);
			FileUtils.copyDirectoryStructure(tempDir.getParentFile(), deployDir);
			log.info("Project is deployed successfully");
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir.getParentFile());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
