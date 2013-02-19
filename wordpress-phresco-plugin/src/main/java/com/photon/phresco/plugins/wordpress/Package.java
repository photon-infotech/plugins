package com.photon.phresco.plugins.wordpress;

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

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
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
	private String zipName;
	private Date currentDate;
	private Log log;
	private PluginPackageUtil util;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        project = mavenProjectInfo.getProject();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        util = new PluginPackageUtil();
        
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
			srcDir = new File(baseDir.getPath() + File.separator + WORDPRESS_SOURCE_DIR);
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			targetDir = new File(project.getBuild().getDirectory());
			if (!buildDir.exists()) {
				buildDir.mkdir();
				log.info("Build directory created..." + buildDir.getPath());
			}
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
			File[] listSourceFiles = srcDir.listFiles();
			for (File childFile : listSourceFiles) {
				if (childFile.isDirectory()) {
					FileUtils.copyDirectoryToDirectory(childFile, targetDir);
				} else {
					FileUtils.copyFileToDirectory(childFile, targetDir);
				}
			}
			File packageInfoFile = new File(baseDir.getPath() + File.separator + DOT_PHRESCO_FOLDER + File.separator + PHRESCO_PACKAGE_FILE);
			if(packageInfoFile.exists()) {
				PluginUtils.createBuildResources(packageInfoFile, baseDir, targetDir);
			}
			createPackage();

		} catch (IOException e) {
			isBuildSuccess = false;
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return isBuildSuccess;
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
				srcConfigFile  = new File(baseDir + configSourceDir);
			} else {
				srcConfigFile = new File(baseDir + sourceDir + FORWARD_SLASH +  CONFIG_FILE);
			}
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, baseDir.getPath(), srcConfigFile);
			pu.encryptConfigFile(srcConfigFile.getPath());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void createPackage() throws MojoExecutionException {
		try {
			zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			ArchiveUtil.createArchive(targetDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
	}
}
