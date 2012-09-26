package com.photon.phresco.plugins.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.Project;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.model.SettingsInfo;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.TechnologyTypes;
import com.photon.phresco.util.Utility;
import com.phresco.pom.util.PomProcessor;

public class Package implements PluginConstants{
	
	private MavenProject project;
	private File baseDir;
	private String environmentName;
	private String moduleName;
	private String buildName;
	private String buildNumber;
	private int buildNo;
	private String mainClassName;
	private String jarName;
	private File targetDir;
	private File buildDir;
	private File buildInfoFile;
	private File tempDir;
	private List<BuildInfo> buildInfoList;
	private int nextBuildNo;
	private String zipName;
	private Date currentDate;
	private String context;
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
			if (environmentName != null) {
				updateFinalName();
				configure();
			}
			executeMvnPackage();
			boolean buildStatus = build();
			writeBuildInfo(buildStatus);
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}	
		
	}
	
	private void init() throws MojoExecutionException {
		try {
			buildInfoList = new ArrayList<BuildInfo>(); // initialization
			buildDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
			targetDir = new File(project.getBuild().getDirectory());
			baseDir = getProjectRoot(baseDir);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			buildInfoFile = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY + BUILD_INFO_FILE);
			File buildInfoDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
			if (!buildInfoDir.exists()) {
				buildInfoDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			nextBuildNo = generateNextBuildNo();
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

	private void updateFinalName() throws MojoExecutionException {
		try {
			ProjectAdministrator projAdmin = PhrescoFrameworkFactory.getProjectAdministrator();
			Project currentProject = projAdmin.getProjectByWorkspace(baseDir);
			String techId = currentProject.getApplicationInfo().getTechInfo().getVersion();
			if (!techId.equals(TechnologyTypes.JAVA_STANDALONE)) {
				String envName = environmentName;
				if (environmentName.indexOf(',') > -1) { // multi-value
					envName = projAdmin.getDefaultEnvName(baseDir.getName());
				}
				List<SettingsInfo> settingsInfos = projAdmin.getSettingsInfos(Constants.SETTINGS_TEMPLATE_SERVER,
						currentProject.getApplicationInfo().getCode(), envName);
				for (SettingsInfo settingsInfo : settingsInfos) {
					context = settingsInfo.getPropertyInfo(Constants.SERVER_CONTEXT).getValue();
					break;
				}
			} else {
				context = jarName;
				updatemainClassName();
			}
			if (StringUtils.isEmpty(context)) {
				return;
			}
			File pom = project.getFile();
			PomProcessor pomprocessor = new PomProcessor(pom);
			pomprocessor.setFinalName(context);
			pomprocessor.save();
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void updatemainClassName() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(mainClassName)) {
				return;
			}
			File pom = project.getFile();
			List<Element> configList = new ArrayList<Element>();
			PomProcessor pomprocessor = new PomProcessor(pom);
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element archive = doc.createElement(JAVA_POM_ARCHIVE);
			Element manifest = doc.createElement(JAVA_POM_MANIFEST);
			Element addClasspath = doc.createElement(JAVA_POM_ADD_PATH);
			addClasspath.setTextContent("true");
			manifest.appendChild(addClasspath);
			Element mainClass = doc.createElement(JAVA_POM_MAINCLASS);
			mainClass.setTextContent(mainClassName);
			manifest.appendChild(addClasspath);
			manifest.appendChild(mainClass);
			archive.appendChild(manifest);
			configList.add(archive);

			pomprocessor.addConfiguration(JAR_PLUGIN_GROUPID, JAR_PLUGIN_ARTIFACT_ID, configList, false);
			pomprocessor.save();
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void executeMvnPackage() throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			log.info("Packaging the project...");
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_CLEAN);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_PACKAGE);
			sb.append(STR_SPACE);
			sb.append(SKIP_TESTS);

			bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("[ERROR]")) {
					errorParam = true;
				}  
			}
			if (errorParam) {
				throw new MojoExecutionException("Compilation Failure ... ");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(bufferedReader);
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

	private void configure() throws MojoExecutionException {
		log.info("Configuring the project....");
		adaptDbConfig();
		adaptSourceConfig();
	}

	private void adaptSourceConfig() throws MojoExecutionException {
		String basedir = baseDir.getName();
		String modulePath = "";
		if (moduleName != null) {
			modulePath = File.separatorChar + moduleName;
		}
		File sourceConfigXML = new File(baseDir + modulePath + JAVA_WEBAPP_CONFIG_FILE);
		File parentFile = sourceConfigXML.getParentFile();
		if (parentFile.exists()) {
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, basedir, sourceConfigXML);
		}
	}

	private void adaptDbConfig() {
		String basedir = baseDir.getName();
		String modulePath = "";
		if (moduleName != null) {
			modulePath = File.separatorChar + moduleName;
		}
		File sourceConfigXML = new File(baseDir + modulePath + JAVA_CONFIG_FILE);
		File parentFile = sourceConfigXML.getParentFile();
		if (parentFile.exists()) {
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, basedir, sourceConfigXML);
		}
	}

	private void createPackage() throws MojoExecutionException, IOException {
		try {
			if (buildName != null) {
				zipName = buildName + DOT_ZIP;
			} else {
				if (buildNumber != null) {
					zipName = PROJECT_CODE + buildNumber + STR_UNDERSCORE + getTimeStampForBuildName(currentDate)
							+ DOT_ZIP;
				} else {
					zipName = PROJECT_CODE + nextBuildNo + STR_UNDERSCORE + getTimeStampForBuildName(currentDate)
							+ DOT_ZIP;
				}
			}
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			String zipNameWithoutExt = zipName.substring(0, zipName.lastIndexOf('.'));
			ProjectAdministrator projectAdministrator = PhrescoFrameworkFactory.getProjectAdministrator();
			Project currentProject = projectAdministrator.getProjectByWorkspace(baseDir);
			String techId = currentProject.getApplicationInfo().getTechInfo().getVersion();
			if (techId.equals(TechnologyTypes.JAVA_STANDALONE)) {
				copyJarToPackage(zipNameWithoutExt);
			} else {
				copyWarToPackage(zipNameWithoutExt, context);
			}
			ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void copyJarToPackage(String zipNameWithoutExt) throws IOException {
		String[] list = targetDir.list(new JarFileNameFilter());
		if (list.length > 0) {
			File jarFile = new File(targetDir.getPath() + File.separator + list[0]);
			tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
			tempDir.mkdir();
			FileUtils.copyFileToDirectory(jarFile, tempDir);
		}
	}

	private void copyWarToPackage(String zipNameWithoutExt, String context) throws MojoExecutionException {
		try {
			String[] list = targetDir.list(new WarFileNameFilter());
			if (list.length > 0) {
				File warFile = new File(targetDir.getPath() + File.separator + list[0]);
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				File contextWarFile = new File(targetDir.getPath() + File.separator + context + ".war");
				warFile.renameTo(contextWarFile);
				FileUtils.copyFileToDirectory(contextWarFile, tempDir);
			} else {
				throw new MojoExecutionException(context + ".war not found in " + targetDir.getPath());
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
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
			buildInfo.setContext(context);
			buildInfo.setEnvironments(envList);
			if (StringUtils.isNotEmpty(moduleName)) {
				buildInfo.setModuleName(moduleName);
			}
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
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_STAMP_FOR_DISPLAY);
		return formatter.format(currentDate.getTime());
	}

	private String getTimeStampForBuildName(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_STAMP_FOR_BUILD_NAME);
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
		// sort to the array to find the max build no
		Arrays.sort(buildArray);

		// increment 1 to the max in the build list
		nextBuildNo = buildArray[buildArray.length - 1] + 1;
		return nextBuildNo;
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}

class WarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".war");
	}
}

class JarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".jar");
	}

}
