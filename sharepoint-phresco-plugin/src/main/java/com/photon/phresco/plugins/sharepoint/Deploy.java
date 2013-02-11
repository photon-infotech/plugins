package com.photon.phresco.plugins.sharepoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.StringUtils;

import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class Deploy implements PluginConstants {
	
	private File baseDir;
	private String buildNumber;
	private String environmentName;

	private File buildFile;
	private File buildDir;
	private File tempDir;
	private File temp;
	private File build;
	private Log log;
	
	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        
		try {
			init();
			extractBuild();
			deploy();
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
			BuildInfo buildInfo = pu.getBuildInfo(Integer.parseInt(buildNumber));
			log.info("Build Name " + buildInfo);
			
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			build = new File(baseDir.getPath() + "\\source" + "\\");
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());
			tempDir = new File(buildDir.getPath() + TEMP_DIR);
			tempDir.mkdirs();
			temp = new File(tempDir.getPath() + "\\" + baseDir.getName() + ".wsp");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void callUsage() throws MojoExecutionException {
		log.error("Invalid usage.");
		log.info("Usage of Deploy Goal");
		log.info(
				"mvn sharepoint:deploy -DbuildName=\"Name of the build\""
						+ " -DenvironmentName=\"Multivalued evnironment names\"");
		throw new MojoExecutionException("Invalid Usage. Please see the Usage of Deploy Goal");
	}

	private void extractBuild() throws MojoExecutionException {
		try {
			ArchiveUtil.extractArchive(buildFile.getPath(), tempDir.getPath(), ArchiveType.ZIP);
			FileUtils.copyFileToDirectory(temp, build);
			FileUtils.deleteDirectory(tempDir);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void deploy() throws PhrescoException {
		try {
			ConfigManager configManager = PhrescoFrameworkFactory.getConfigManager(new File(baseDir.getPath() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + Constants.CONFIGURATION_INFO_FILE));

			List<com.photon.phresco.configuration.Configuration> configurations = configManager.getConfigurations(environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration configuration : configurations) {
				String serverContext = configuration.getProperties().getProperty(Constants.SERVER_CONTEXT);
				String protocol = configuration.getProperties().getProperty(Constants.SERVER_PROTOCOL);
				String host = configuration.getProperties().getProperty(Constants.SERVER_HOST);
				String port = configuration.getProperties().getProperty(Constants.SERVER_PORT);
				String projectCode = baseDir.getName();
				restore(protocol, serverContext, host, port);
				addSolution(projectCode);
				deploysolution(protocol, serverContext, host, port, projectCode);
			}
		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}

	private void restore(String protocol, String serverContext, String host, String port)
	throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			File file = new File(build.getPath() + "\\phresco-pilot.dat");
			if (!file.exists()) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(SHAREPOINT_STSADM);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_O);
			sb.append(SHAREPOINT_RESTORE);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_URL);
			sb.append(STR_SPACE);
			sb.append(protocol);
			sb.append(SHAREPOINT_STR_COLON);
			sb.append(SHAREPOINT_STR_DOUBLESLASH);
			sb.append(host);
			sb.append(SHAREPOINT_STR_COLON);
			sb.append(port);
			sb.append(SHAREPOINT_STR_BACKSLASH);
			sb.append(serverContext);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_HYPEN);
			sb.append(SHAREPOINT_STR_OVERWRITE);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_HYPEN);
			sb.append(SHAREPOINT_STR_FILENAME);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_DOUBLEQUOTES + file + SHAREPOINT_STR_DOUBLEQUOTES);
			bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("[ERROR]")) {
					System.out.println(line); //do not use getLog() here as this line already contains the log type.
					errorParam = true;
				}
			}
			if (errorParam) {
				throw new MojoExecutionException("Restore Failed ...");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}  finally {
			Utility.closeStream(bufferedReader);
		}
	}

	private void addSolution(String ProjectCode) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(SHAREPOINT_STSADM);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_O);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_ADDSOLUTION);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_HYPEN);
			sb.append(SHAREPOINT_STR_FILENAME);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_DOUBLEQUOTES + baseDir.getPath() + "\\source" + "\\"
					+ ProjectCode + ".wsp" + SHAREPOINT_STR_DOUBLEQUOTES);
			File file = new File(baseDir.getPath() + "\\source" + "\\"
					+ ProjectCode + ".wsp");
			if (file.exists()) {
				bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					if (line.startsWith("[ERROR]")) {
						System.out.println(line); //do not use getLog() here as this line already contains the log type.
						errorParam = true;
					}
				}
				if (errorParam) {
					throw new MojoExecutionException("Adding of Solution Failed ...");
				}
			} else {
				log.error("File Not found Exception");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}

	private void deploysolution(String protocol, String serverContext, String host,
			String port, String projectCode) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(SHAREPOINT_STSADM);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_O);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_DEPLOYSOLUTION);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_HYPEN);
			sb.append(SHAREPOINT_STR_NAME);
			sb.append(STR_SPACE);
			sb.append(projectCode + ".wsp");
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_HYPEN);
			sb.append(SHAREPOINT_STR_URL);
			sb.append(STR_SPACE);
			sb.append(protocol);
			sb.append(SHAREPOINT_STR_COLON);
			sb.append(SHAREPOINT_STR_DOUBLESLASH);
			sb.append(host);
			sb.append(SHAREPOINT_STR_COLON);
			sb.append(port);
			sb.append(SHAREPOINT_STR_BACKSLASH);
			sb.append(serverContext);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_HYPEN);
			sb.append(SHAREPOINT_STR_IMMEDIATE);
			sb.append(STR_SPACE);
			sb.append(SHAREPOINT_STR_HYPEN);
			sb.append(SHAREPOINT_STR_ALLOWACDEP);
			bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("[ERROR]")) {
					errorParam = true;
				}
			}
			if (errorParam) {
				throw new MojoExecutionException("Deploying solution Failed ...");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}
}
