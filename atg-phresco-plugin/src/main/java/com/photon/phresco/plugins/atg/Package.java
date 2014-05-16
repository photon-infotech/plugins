package com.photon.phresco.plugins.atg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class Package implements PluginConstants, AtgConstants {
	private MavenProject project;
	private File baseDir;
	private String environmentName;
	private String buildName;
	private String buildNumber;
	private int buildNo;
	private File buildDir;
	private File buildInfoFile;
	private File tempDir;
	private int nextBuildNo;
	private String zipName;
	private Date currentDate;
	private Log log;
	private PluginPackageUtil util;
	private PluginUtils pu;
	private String sourceDir;
	private StringBuilder builder;
	private String subModule = "";
	private File workingDirectory;
	private File pomFile;
    private String dotPhrescoDirName;
    private File dotPhrescoDir;
    private File srcDirectory;
    private File dotPhrescoRoot;
    private ApplicationInfo appInfoRoot;
    private List<String> depModuleList = new ArrayList<String>();
    private File targetDir;
    private String packaging;
    
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		
        project = mavenProjectInfo.getProject();
        packaging = project.getPackaging();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        util = new PluginPackageUtil();
        pu = new PluginUtils();
        builder = new StringBuilder();
        pomFile = project.getFile();
        workingDirectory = new File(baseDir.getPath());
        dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        dotPhrescoDir = baseDir;
        if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        	dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
        }
        dotPhrescoRoot = dotPhrescoDir;
        if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
        	subModule = mavenProjectInfo.getModuleName();
//        	warConfigFile = new File(dotPhrescoDir.getPath() + File.separator + subModule + File.separator + DOT_PHRESCO_FOLDER + File.separator + WAR_CONFIG_FILE);
        	workingDirectory = new File(baseDir.getPath() + File.separator + subModule);
        	dotPhrescoDir = new File(dotPhrescoDir.getPath() + File.separatorChar + subModule);
        	pomFile = pu.getPomFile(dotPhrescoDir, workingDirectory);
        } 
        File splitProjectDirectory = pu.getSplitProjectSrcDir(pomFile, dotPhrescoDir, subModule);
    	srcDirectory = workingDirectory;
    	if (splitProjectDirectory != null) {
    		srcDirectory = splitProjectDirectory;
    	}
        initAppInfoRoot();
        PluginUtils.checkForConfigurations(dotPhrescoDir, environmentName);
        try { 
			init();
			if (environmentName != null) {
				configure();
			}
			getMavenCommands(configuration);
			executeMvnPackage();
			boolean buildStatus = build();
			writeBuildInfo(buildStatus);
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}	
	}
	
	private void initAppInfoRoot() throws PhrescoException {
		appInfoRoot = pu.getAppInfo(dotPhrescoRoot);
	}
	
	private void init() throws MojoExecutionException {
		try {
			targetDir = new File(project.getBuild().getDirectory());
			buildDir = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY);
			buildInfoFile = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY + BUILD_INFO_FILE);
			File buildInfoDir = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY);
			dotPhrescoDir = getProjectRoot(dotPhrescoDir);
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
	
	private File getProjectRoot(File childDir) {
		File[] listFiles = childDir.listFiles(new PhrescoDirFilter());
		if (listFiles != null && listFiles.length > 0) {
			return childDir;
		}
		if (childDir.getParentFile() != null) {
			return getProjectRoot(childDir.getParentFile());
		}
		return null;
	}

	public class PhrescoDirFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.equals(DOT_PHRESCO_FOLDER);
		}
	}
	
	private void configure() throws MojoExecutionException {
		log.info("Configuring the project....");
		try {
			adaptSourceConfig();
			pu.writeDatabaseDriverToConfigXml(srcDirectory, sourceDir, environmentName);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void adaptSourceConfig() throws MojoExecutionException {
		PomProcessor processor  = null;
		File sourceConfigXML = null;
		try {
			processor = new PomProcessor(pomFile);
			String configXml = processor.getProperty(POM_PROP_CONFIG_FILE);
			if(StringUtils.isNotEmpty(configXml)) {
				sourceConfigXML = new File(srcDirectory + configXml);
			} else {
				sourceConfigXML = new File(srcDirectory + sourceDir + FORWARD_SLASH +  CONFIG_FILE);
			}
			File parentFile = sourceConfigXML.getParentFile();
			if (parentFile.exists()) {
				pu.executeUtil(environmentName, dotPhrescoDir.getPath(), sourceConfigXML);
			}
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private void getMavenCommands(Configuration configuration) {
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if(parameter.getPluginParameter() != null && parameter.getMavenCommands() != null) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if(parameter.getValue().equals(mavenCommand.getKey())) {
						builder.append(mavenCommand.getValue());
						builder.append(STR_SPACE);
					}
				}
			}
		}
	}
	
	private void executeMvnPackage() throws MojoExecutionException, IOException, PhrescoException {
		log.info("Packaging the project...");
		StringBuilder sb = new StringBuilder();
		sb.append(MVN_CMD);
		sb.append(STR_SPACE);
		sb.append(MVN_PHASE_CLEAN);
		sb.append(STR_SPACE);
		sb.append("antrun:run");
		sb.append(STR_SPACE);
		sb.append("-Dtarget.name=package");
		sb.append(STR_SPACE);
		sb.append(builder.toString());
		String processName = ManagementFactory.getRuntimeMXBean().getName();
		String[] split = processName.split("@");
		String processId = split[0].toString();
		Utility.writeProcessid(workingDirectory.getPath(), Constants.KILLPROCESS_BUILD, processId);
		String baseCommand = sb.toString();
		System.out.println("COMMAND IS    " + baseCommand);
		executeCommand(baseCommand, baseDir,"");
	}
	
	
	private void executeCommand(String command, File workDir, String module) throws IOException, PhrescoException {
		System.out.println("I AM EXECUTING *******************" + workDir.getPath());
		String line ="";
		BufferedReader bufferedReader = Utility.executeCommand(command, workDir.toString());
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line); //do not use getLog() here as this line already contains the log type.
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
			tempDir = new File(buildDir.getPath() + File.separator + TEMP);
			String zipNameWithoutExt = zipName.substring(0, zipName.lastIndexOf('.'));
			System.out.println("EXTENSION IS  " + project.getPackaging());
			if(packaging.equals("jar")) {
				copyJarToPackage(zipNameWithoutExt);
			}
			if(packaging.equals("war")) {
				copyWarToPackage(zipNameWithoutExt);
			}
			if(packaging.equals("pom")) {
				copyEarToPackage(zipNameWithoutExt);
			}
			ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}
	
	private void copyEarToPackage(String zipNameWithoutExt) throws MojoExecutionException {
		tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
		tempDir.mkdir();
		System.out.println("TARGET DIR IS    " + targetDir.getPath());
		File[] listFiles = targetDir.listFiles();
		if(listFiles.length > 0) {
			for (File file : listFiles) {
				if(file.getName().endsWith("ear")) {
					try {
						FileUtils.copyDirectoryToDirectory(file, tempDir);
					} catch (IOException e) {
						throw new MojoExecutionException(e.getMessage());
					}
				}
			}
		}
	}

	private void copyWarToPackage(String zipNameWithoutExt) throws MojoExecutionException {
		tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
		tempDir.mkdir();
		File j2eeApps = new File(baseDir, "j2ee-apps");
		File[] listFiles = j2eeApps.listFiles();
		if(listFiles.length > 0) {
			for (File file : listFiles) {
				if(file.isDirectory()) {
					try {
						FileUtils.copyDirectoryToDirectory(file, tempDir);
					} catch (IOException e) {
						throw new MojoExecutionException(e.getMessage());
					}
				}
			}
		}
	}

	private void copyJarToPackage(String zipNameWithoutExt) throws MojoExecutionException {
		try {
			File classesJarDir = new File(targetDir, "build/classes");
			String[] list = classesJarDir.list(new JarFileNameFilter());
			if (list.length > 0) {
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				for (String string : list) {
					File jarFile = new File(classesJarDir, string);
					FileUtils.copyFileToDirectory(jarFile, tempDir);
				}
			}
			File configJarDir = new File(targetDir, "build/config");
			String[] configlist = configJarDir.list(new JarFileNameFilter());
			if (configlist.length > 0) {
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				for (String string : configlist) {
					File jarFile = new File(configJarDir, string);
					FileUtils.copyFileToDirectory(jarFile, tempDir);
				}
			} 
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
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
}

class JarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".jar");
	}

}