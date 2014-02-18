package com.photon.phresco.plugins.cpp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.FileUtil;
import com.photon.phresco.util.Utility;
import com.phresco.pom.util.PomProcessor;

public class Package
implements PluginConstants
{
	private String baseDir;
	private String workingDir;
	private String pomFileName;
	private MavenProject project;
	private String fileName;
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
	private String buildNumber;
	private PluginUtils pluginUtils;
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
	     pluginUtils = new PluginUtils();
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
			osName = pluginUtils.findPlatform();
			pomFileName = project.getFile().getName();
			File pomFile = new File(baseDir + File.separator + pomFileName);
			PomProcessor processor = new PomProcessor(pomFile);
			if (osName.contains(Constants.WINDOWS)) {
				fileName = processor.getProperty("phresco.windows.cpp.file.name");
				sb.append("tcc");
				if (StringUtils.isNotEmpty(fileName))
					sb.append(STR_SPACE).append(fileName).append(DOT).append("cpp");
			}
			if (osName.contains(FrameworkConstants.MAC)) {
				fileName = processor.getProperty("phresco.mac.cpp.file.name");
				sb.append("g++ -o").append(STR_SPACE).
				append(fileName).append(STR_SPACE).
				append(fileName).append(DOT).append("cpp"); 
			}
			clearExe();
			log.info("WorkingDir : " + workingDir);
			log.info("Command : " + sb.toString());
			Utility.executeStreamconsumer(sb.toString(), workingDir, "", "");
		}
		catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void clearExe () throws PhrescoException {
		File exeFile = new File(workingDir + File.separatorChar + fileName + ".EXE");
		File objFile = new File(workingDir + File.separatorChar + fileName + ".OBJ");
		if (exeFile.exists()) {
			FileUtil.delete(exeFile);
		}
		if (objFile.exists()) {
			FileUtil.delete(exeFile);
		}
	}
	
	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {
			log.info("Building the project...");
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
			File targetDir = new File(workingDir);
			String[] list = targetDir.list(new ExeFileNameFilter());
			if (list.length > 0) {
				File jarFile = new File(targetDir.getPath() + File.separator + list[0]);
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				FileUtils.copyFileToDirectory(jarFile, tempDir);
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
	
	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		int buildNo = 0;
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
	}
	
	class ExeFileNameFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.endsWith(".EXE");
		}
	}
}