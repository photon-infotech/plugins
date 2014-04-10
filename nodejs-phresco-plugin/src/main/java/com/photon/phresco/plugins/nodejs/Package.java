/**
 * nodejs-phresco-plugin
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
package com.photon.phresco.plugins.nodejs;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class Package implements PluginConstants {

	private MavenProject project;
	private File baseDir;
	private String environmentName;
	private String buildName;
	private String buildNumber;
	private int buildNo;

	private File srcDir;
	private File targetDir;
	private File buildDir;
	private File buildInfoFile;
	private int nextBuildNo;
	private Date currentDate;
	private String sourceDirectory = "/source";
	private Log log;
	private PluginPackageUtil util;
	private File directory;
	private String dotPhrescoDirName;
    private File dotPhrescoDir;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        util = new PluginPackageUtil();
        dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        dotPhrescoDir = baseDir;
        if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        	dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
        }
        PluginUtils.checkForConfigurations(dotPhrescoDir, environmentName);
        try {
			init();
			boolean buildStatus = build();
			writeBuildInfo(buildStatus);
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		
	}
	
	private void init() throws MojoExecutionException {
		try {
			File pom = project.getFile();
			PomProcessor pomProcessor = new PomProcessor(pom);
			String srcDirName = pomProcessor.getProperty(Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
			PluginUtils pluginUtils = new PluginUtils();
			ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
			String appDirName = appInfo.getAppDirName();
			directory = baseDir;
			if (StringUtils.isNotEmpty(srcDirName)) {
				directory = new File(Utility.getProjectHome() + File.separatorChar + appDirName + File.separatorChar + srcDirName);
			}
			srcDir = new File(directory.getPath() + File.separator + sourceDirectory);
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			if (!buildDir.exists()) {
				buildDir.mkdir();
				log.info("Build directory created..." + buildDir.getPath());
			}
			targetDir = new File(project.getBuild().getDirectory());
			buildInfoFile = new File(buildDir.getPath() + BUILD_INFO_FILE);
			nextBuildNo = util.generateNextBuildNo(buildInfoFile);
			currentDate = Calendar.getInstance().getTime();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {
			configure();
			File packageInfoFile = new File(dotPhrescoDir.getPath() + File.separator + DOT_PHRESCO_FOLDER + File.separator + PHRESCO_PACKAGE_FILE);
			File fileBuild = new File(targetDir.getPath()+ FORWARD_SLASH + PROJECT_FOLDER);
			if(!fileBuild.exists()){
				fileBuild.mkdir();
			}
			if(packageInfoFile.exists()) {
				FileUtils.copyDirectory(srcDir, fileBuild);
				PluginUtils.createBuildResources(packageInfoFile, baseDir, targetDir);
			} else {
				FileUtils.copyDirectory(srcDir, fileBuild);
			}
			createPackage();
		} catch (IOException e) {
			isBuildSuccess = false;
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}

		return isBuildSuccess;
	}
	
	private void createPackage() throws MojoExecutionException {
		String zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
		String zipFilePath = buildDir.getPath() + File.separator + zipName;
		try {
			ArchiveUtil.createArchive(targetDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void configure() throws MojoExecutionException {
		try {
			log.info("Configuring the project....");
			File pom = project.getFile();
		    PomProcessor pomProcessor = new PomProcessor(pom);
			String sourceDir = pomProcessor.getProperty(POM_PROP_KEY_SOURCE_DIR);
			String configSourceDir = pomProcessor.getProperty(POM_PROP_CONFIG_FILE);
			File srcConfigFile = null;
			if(StringUtils.isNotEmpty(configSourceDir)) {
				srcConfigFile  = new File(directory + configSourceDir);
			} else {
				srcConfigFile = new File(directory + sourceDir + FORWARD_SLASH +  CONFIG_FILE);
			}
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, dotPhrescoDir.getPath(), srcConfigFile);
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
	}
}
