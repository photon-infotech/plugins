package com.photon.phresco.plugins.php;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.model.SettingsInfo;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;

public class Deploy implements PluginConstants {
	
	private File baseDir;
	private String buildName;
	private String environmentName;
	private String projectCode;
	private boolean importSql;
	private File buildDir;
	private File buildFile;
	private File tempDir;
	private Log log;
	
    public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
    	this.log = log;
    	baseDir = mavenProjectInfo.getBaseDir();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        projectCode = mavenProjectInfo.getProjectCode();
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
			if (StringUtils.isEmpty(buildName) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			buildDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
			buildFile = new File(buildDir.getPath() + File.separator + buildName);
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

	private void createDb() throws MojoExecutionException {
		PluginUtils util = new PluginUtils();
		try {
			if (importSql) {
				List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_DB);
				for (SettingsInfo databaseDetails : settingsInfos) {
					String databaseType = databaseDetails.getPropertyInfo(Constants.DB_TYPE).getValue();
					util.getSqlFilePath(databaseDetails,baseDir, databaseType);
				}
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void extractBuild() throws MojoExecutionException {
		try {
			String context = "";
			List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_SERVER);
			for (SettingsInfo serverDetails : settingsInfos) {
				context = serverDetails.getPropertyInfo(Constants.SERVER_CONTEXT).getValue();
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
			List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_SERVER);
			for (SettingsInfo serverDetails : settingsInfos) {
				deployLocation = serverDetails.getPropertyInfo(Constants.SERVER_DEPLOY_DIR).getValue();
				break;
			}		
			File deployDir = new File(deployLocation);
			if (!deployDir.exists()) {
				throw new MojoExecutionException(" Deploy Directory" + deployLocation + " Does Not Exists ");
			}
			log.info("Project is deploying into " + deployLocation);
			FileUtils.copyDirectoryStructure(tempDir.getParentFile(), deployDir);
			log.info("Project is deployed successfully");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private List<SettingsInfo> getSettingsInfo(String configType) throws PhrescoException {
		ProjectAdministrator projAdmin = PhrescoFrameworkFactory.getProjectAdministrator();
		return projAdmin.getSettingsInfos(configType, baseDir.getName(), environmentName);
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir.getParentFile());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
