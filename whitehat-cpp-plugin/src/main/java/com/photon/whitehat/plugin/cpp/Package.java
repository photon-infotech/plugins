package com.photon.whitehat.plugin.cpp;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Utility;
import com.photon.whitehat.plugin.cpp.utils.Util;

public class Package implements PluginConstants {
	private String baseDir;
	private String workingDir;
	private MavenProject project;
	private Log log;
	private String environmentName;
	private File buildDir;
	private File buildInfoFile;
	private File tempDir;
	private int nextBuildNo;
	private String zipName;
	private Date currentDate;
	private String buildName;
	private String targetDir;
	private String buildNumber;
	private String isDebug;
	private String isRelease;
	private String buildMode;
	private PluginPackageUtil util;

	public void pack(Configuration config, MavenProjectInfo mavenProjectInfo,
			Log log) throws PhrescoException {
		this.baseDir = mavenProjectInfo.getBaseDir().getPath();
		this.project = mavenProjectInfo.getProject();
		this.log = log;

		workingDir = baseDir + File.separator + "src";

		Map<String, String> configs = MojoUtil.getAllValues(config);
		environmentName = configs.get(ENVIRONMENT_NAME);
		buildName = configs.get(BUILD_NAME);
		buildNumber = configs.get(BUILD_NUMBER);
		isDebug = configs.get(DEBUG);
		isRelease = configs.get(RELEASE);
		util = new PluginPackageUtil();

		try {

			init();
			executeCommand();
			boolean isBuildSuccess = build();
			writeBuildInfo(isBuildSuccess);

		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}

	private void init() throws MojoExecutionException {
		try {

			buildDir = new File(baseDir + PluginConstants.BUILD_DIRECTORY);
			buildInfoFile = new File(baseDir + PluginConstants.BUILD_DIRECTORY
					+ BUILD_INFO_FILE);
			File buildInfoDir = new File(baseDir
					+ PluginConstants.BUILD_DIRECTORY);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}

			if (!buildInfoDir.exists()) {
				buildInfoDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}

			if (isDebug.equalsIgnoreCase("true")) {
				buildMode = "debug";
				targetDir = workingDir + DEBUG_FOLDER_PATH;
			}
			if (isRelease.equalsIgnoreCase("true")) {
				buildMode = "release";
				targetDir = workingDir + RELEASE_FOLDER_PATH;
			}
			log.info("BuildMode =" + buildMode);
			File target = new File(targetDir);

			if (!target.exists()) {
				target.mkdirs();
				log.info("target directory created..." + target.getPath());
			}

			clearAppFile();
			nextBuildNo = util.generateNextBuildNo(buildInfoFile);
			currentDate = Calendar.getInstance().getTime();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void executeCommand() throws PhrescoException {
		try {

			StringBuilder sb = new StringBuilder();
			log.info("Project is Building...");
			sb.append("sh build.sh").append(STR_SPACE).append(buildMode);
			log.info("WorkingDir : " + baseDir);
			log.info("Command    : " + sb.toString());
			Utility.executeStreamconsumer(sb.toString(), baseDir, "", "");
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {

			createPackage();
		} catch (Exception e) {
			isBuildSuccess = false;
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return isBuildSuccess;
	}

	private void createPackage() throws MojoExecutionException {
		try {
			zipName = util.createPackage(buildName, buildNumber, nextBuildNo,
					currentDate);
			System.out.println(buildName + " " + buildNumber + " "
					+ nextBuildNo + " " + currentDate);
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			String zipNameWithoutExt = zipName.substring(0,
					zipName.lastIndexOf('.'));
			copyExeToPackage(zipNameWithoutExt);
			ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath,
					ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void copyExeToPackage(String zipNameWithoutExt)
			throws MojoExecutionException {
		try {

			File exeFile = new File(targetDir + File.separator + "Aviator.app");
			if (exeFile.exists()) {
				tempDir = new File(buildDir.getPath() + File.separator
						+ zipNameWithoutExt);
                tempDir.mkdir();
				File appDir = new File(tempDir + File.separator + "Aviator.app");
				appDir.mkdir();
				Util.copyFolder(exeFile, appDir);
			} else {
				log.error("Aviator.app is not generated....");
			}

		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void clearAppFile() throws MojoExecutionException, IOException {

		File appFile = new File(targetDir + File.separator + "Aviator.app");
		if (appFile.exists()) {
			FileUtils.deleteQuietly(appFile);
		}

	}

	private void writeBuildInfo(boolean isBuildSuccess)
			throws MojoExecutionException {
		int buildNo = 0;
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber,
				nextBuildNo, environmentName, buildNo, currentDate,
				buildInfoFile);
	}

}