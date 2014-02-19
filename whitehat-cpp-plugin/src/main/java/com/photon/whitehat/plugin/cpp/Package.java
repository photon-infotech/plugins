package com.photon.whitehat.plugin.cpp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Utility;

public class Package
implements PluginConstants
{
	private String baseDir;
	private String workingDir;
	
	private MavenProject project;
	
	private String osName;
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
	private String isDebug ;
	private String isRelease ;
	private String buildMode;
	private PluginPackageUtil util;
    public void pack(Configuration config, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.baseDir = mavenProjectInfo.getBaseDir().getPath();
		this.project = mavenProjectInfo.getProject();
		this.log = log;
		 
		 workingDir = baseDir + File.separator + PROJECT_FOLDER;
		
		 Map<String, String> configs = MojoUtil.getAllValues(config);
	     environmentName = configs.get(ENVIRONMENT_NAME);
	     buildName = configs.get(BUILD_NAME);
	     buildNumber = configs.get(BUILD_NUMBER);
	     isDebug = configs.get(RELEASE);
	     isRelease = configs.get(DEBUG);
	     
	     util = new PluginPackageUtil();
	     
	     try {
			init();
			executeCommand();
			boolean isBuildSuccess = build();
			writeBuildInfo(isBuildSuccess);
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}
	
	private void init() throws MojoExecutionException {
		try {
			File target = new File(targetDir);
			buildDir = new File(baseDir + PluginConstants.BUILD_DIRECTORY);
			buildInfoFile = new File(baseDir + PluginConstants.BUILD_DIRECTORY + BUILD_INFO_FILE);
			File buildInfoDir = new File(baseDir + PluginConstants.BUILD_DIRECTORY);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			
			if (!buildInfoDir.exists()) {
				buildInfoDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			if (!target.exists()) {
				target.mkdirs();
				log.info("target directory created..." + target.getPath());
			}
			
			if(isDebug.equalsIgnoreCase("true")){
				buildMode = "debug";
				 targetDir = workingDir + DEBUG_FOLDER_PATH;
			}
			if(isRelease.equalsIgnoreCase("true")){
				buildMode = "release";
		        targetDir = workingDir + RELEASE_FOLDER_PATH;
			}
			log.info("BuildMode ="+buildMode);
			log.info("targetDir ="+targetDir);
			
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
		    if (osName.contains(FrameworkConstants.MAC)) {
				
				sb.append("sh build.sh").append(STR_SPACE).append(buildMode);
		    }
		    clearAppFile();
			log.info("WorkingDir : " + baseDir);
			log.info("Command : " + sb.toString());
			Utility.executeStreamconsumer(sb.toString(), baseDir, "", "");
		}
		catch (Exception e) {
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
			zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			String zipNameWithoutExt = zipName.substring(0, zipName.lastIndexOf('.'));
			copyExeToPackage(zipNameWithoutExt);
		    ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}
	
	private void copyExeToPackage(String zipNameWithoutExt) throws MojoExecutionException {
		try {
			File exeFile=null;
			String[] list =null;
		    if(osName.contains(FrameworkConstants.MAC)){
				File appFileLocation = new File(targetDir);
				list = appFileLocation.list(new AppFileNameFilter());
			     if (list.length > 0) {
				     exeFile = new File(appFileLocation.getPath() + File.separator + list[0]);
				      tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				      tempDir.mkdir();
				      FileUtils.copyFileToDirectory(exeFile, tempDir);
			      }
			 }
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void cleanUp() throws MojoExecutionException {
		try {
			if(tempDir != null && tempDir.exists()) {
				FileUtils.deleteDirectory(tempDir);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void clearAppFile() throws MojoExecutionException, IOException {
		File appFile =new File(targetDir+File.separator +"Aviator.app");
		if (appFile.exists()){
			FileUtils.deleteQuietly(appFile);
		}
		
	}
	
	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		int buildNo = 0;
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
	}
	
	class AppFileNameFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.endsWith(".app");
		}
	}
	
	
}