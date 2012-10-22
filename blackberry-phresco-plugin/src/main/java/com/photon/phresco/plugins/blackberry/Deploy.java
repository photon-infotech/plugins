/*
 * ###
 * windows-phone-maven-plugin Maven Mojo
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
package com.photon.phresco.plugins.blackberry;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Utility;
/**
 * Goal which deploys the Java WebApp to a server
 * 
 * @goal deploy
 * 
 */
public class Deploy implements PluginConstants {

	private File baseDir;
	private String buildNumber;
	private String environmentName;
	
	private File buildFile;
	private File tempDir;
	private File buildDir;
	private BuildInfo buildInfo;
	private Log log;
	
	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildNumber = configs.get(USER_BUILD_NUMBER);
		try {
			init();
			extractBuild();
			renameFiles();
			deployBuild();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}

	private void init() throws MojoExecutionException {
		try {

			if (StringUtils.isEmpty(buildNumber) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			
			PluginUtils pu = new PluginUtils();
			buildInfo = pu.getBuildInfo(Integer.parseInt(buildNumber));
			
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());
			tempDir = new File(buildDir.getPath() + File.separator + buildFile.getName().substring(0, buildFile.getName().length() - 4));
			tempDir.mkdirs();
			
		} catch (Exception e) {
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void extractBuild() throws MojoExecutionException {
		try {
			ArchiveUtil.extractArchive(buildFile.getPath(), tempDir.getPath(), ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		} 
	}
	
	private void callUsage() throws MojoExecutionException {
		log.error("Invalid usage.");
		log.info("Usage of Deploy Goal");
		log.info(
				"mvn blackberry:deploy -DbuildNumber=\"Build Number\""
						+ " -DenvironmentName=\"Multivalued evnironment names\"");
		throw new MojoExecutionException("Invalid Usage. Please see the Usage of Deploy Goal");
	}
	
	

	private void deployBuild() throws MojoExecutionException {
		BufferedReader in = null;
		try {
			log.info("Deploying project ...");
			tempDir = new File(tempDir.getPath() + File.separator + BB_STANDARD_INSTALL);
			
			// Get .cod file from the extracted contents
			File[] codFile = tempDir.listFiles(new FilenameFilter() { 
				public boolean accept(File dir, String name) { 
					return name.endsWith(".cod");
				}
			});
			
			// javaloader -usb load <FileName>.cod
			ProcessBuilder pb;
			pb = new ProcessBuilder(BB_JAVA_LOADER_HOME);
			// Include errors in output
			pb.redirectErrorStream(true);
			pb.command().add(BB_USB);
			pb.command().add(BB_LOAD);
			pb.command().add(codFile[0].getName());
			log.info("Deploy command: " + pb.command());
			
			Process child;
			try {
				pb.directory(tempDir);
				child = pb.start();
				InputStream is = new BufferedInputStream(child.getInputStream());
				int singleByte = 0;
				while ((singleByte = is.read()) != -1) {
				}
			} catch (IOException e) {
				log.error(e);
				throw new MojoExecutionException(e.getMessage(), e);
			}
			
		}  finally {
			Utility.closeStream(in);
		}
	}
	
	private void renameFiles() throws MojoExecutionException {
		try {
			File stdInstallDir = new File(tempDir.getPath() + File.separator + BB_STANDARD_INSTALL);
			String bName = buildFile.getName().substring(0, buildFile.getName().length() - 4);
			renameFileNames(stdInstallDir, bName, true);
			
			File OTAInstallDir = new File(tempDir.getPath() + File.separator + BB_OTA_INSTALL);
			renameFileNames(OTAInstallDir, bName, true);
			
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} 
	}
	
	private void renameFileNames(File dir, String bName, boolean replaceFlag) throws MojoExecutionException {
		try {
		
			// Rename all the files in working dir with buildname
			File[] fileList = dir.listFiles(new FilenameFilter() { 
				public boolean accept(File dir, String name) { 
					return name.toLowerCase().startsWith("temp");
				}
			});
			
			for (int i = 0; i < fileList.length; i++) {
			    File oldFile = fileList[i];
			    String replace = fileList[i].getName().replace(fileList[i].getName().substring(0, 4), bName);
				File newFile = new File(dir.getAbsolutePath() + File.separator + replace);
				boolean isFileRenamed = false;
				if (replaceFlag)
					isFileRenamed = oldFile.renameTo(newFile);
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} 
	}
	
	
}
