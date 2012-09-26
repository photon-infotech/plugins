package com.photon.phresco.plugins.wordpress;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;


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
	private List<BuildInfo> buildInfoList;
	private int nextBuildNo;
	private String zipName;
	private Date currentDate;
	private Log log;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        project = mavenProjectInfo.getProject();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(USER_BUILD_NUMBER);
        
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
			buildInfoList = new ArrayList<BuildInfo>(); // initialization
			srcDir = new File(baseDir.getPath() + File.separator + WORDPRESS_SOURCE_DIR);
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			targetDir = new File(project.getBuild().getDirectory());
			if (!buildDir.exists()) {
				buildDir.mkdir();
				log.info("Build directory created..." + buildDir.getPath());
			}
			buildInfoFile = new File(buildDir.getPath() + BUILD_INFO_FILE);
			nextBuildNo = generateNextBuildNo();
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
			File srcConfigFile = new File(baseDir + WORDPRESS_TEST_CONFIG_FILE);
			String basedir = baseDir.getName();
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, basedir, srcConfigFile);
			pu.encryptConfigFile(srcConfigFile.getPath());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void createPackage() throws MojoExecutionException {
		try {
			if (buildName != null) {
				zipName = buildName + ".zip";
			} else {
				if (buildNumber != null) {
					zipName = PROJECT_CODE + buildNumber + STR_UNDERSCORE + getTimeStampForBuildName(currentDate)
							+ ".zip";
				} else {
					zipName = PROJECT_CODE + nextBuildNo + STR_UNDERSCORE + getTimeStampForBuildName(currentDate)
							+ ".zip";
				}
			}
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			ArchiveUtil.createArchive(targetDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		try {
			if (buildNumber != null) {
				buildNo = Integer.parseInt(buildNumber);
			}
			PluginUtils pu = new PluginUtils();
			BuildInfo buildInfo = new BuildInfo();
			List<String> envList = pu.csvToList(environmentName);
			if (buildNo > 0) {
				buildInfo.setBuildNo(buildNo);
			} else {
				buildInfo.setBuildNo(nextBuildNo);
			}
			buildInfo.setTimeStamp(getTimeStampForDisplay(currentDate));
			if (isBuildSuccess) {
				buildInfo.setBuildStatus(SUCCESS);
			} else {
				buildInfo.setBuildStatus(FAILURE);
			}
			buildInfo.setBuildName(zipName);
			buildInfo.setEnvironments(envList);
			buildInfoList.add(buildInfo);
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(buildInfoFile);
			gson.toJson(buildInfoList, writer);

			writer.close();
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private String getTimeStampForDisplay(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
		return formatter.format(currentDate.getTime());
	}

	private String getTimeStampForBuildName(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
		return formatter.format(currentDate.getTime());
	}

	private int generateNextBuildNo() throws IOException {
		nextBuildNo = 1;
		if (!buildInfoFile.exists()) {
			return nextBuildNo;
		}

		BufferedReader read = new BufferedReader(new FileReader(buildInfoFile));
		String content = read.readLine();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<BuildInfo>>() {
		}.getType();
		buildInfoList = (List<BuildInfo>) gson.fromJson(content, listType);
		if (buildInfoList == null || buildInfoList.size() == 0) {
			return nextBuildNo;
		}
		int buildArray[] = new int[buildInfoList.size()];
		int count = 0;
		for (BuildInfo buildInfo : buildInfoList) {
			buildArray[count] = buildInfo.getBuildNo();
			count++;
		}

		Arrays.sort(buildArray); // sort to the array to find the max build no

		nextBuildNo = buildArray[buildArray.length - 1] + 1; // increment 1 to
																// the max in
																// the build
																// list
		return nextBuildNo;
	}

}
