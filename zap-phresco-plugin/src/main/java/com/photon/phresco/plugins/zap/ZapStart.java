package com.photon.phresco.plugins.zap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.configuration.ConfigReader;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;


public class ZapStart implements ZapConstants{
	private File baseDir;
	private String environmentName;
	private String zapDirectory;
	private String port;
	private Log log;

	public void start(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		File configPath = new File(baseDir + File.separator + DOT_PHRESCO_FOLDER + File.separator  + CONFIG_FILE);
		if (!configPath.exists()) {
			throw new PhrescoException(CONFIG_FILE_NOT_FOUND_ERROR);
		}
		try {
			ConfigReader reader = new ConfigReader(configPath);
			List<com.photon.phresco.configuration.Configuration> configurationList = reader.getConfigurations(environmentName, SERVER);
			if (CollectionUtils.isNotEmpty(configurationList)) {
				com.photon.phresco.configuration.Configuration config = configurationList.get(0);
				if (config != null) {
					Properties properties = config.getProperties();
					zapDirectory = (String) properties.get(ZAPDIR);
					port = (String) properties.get(PORT);
					validateZapDirectory(zapDirectory);
					startDaemonProcess(baseDir.getPath(), port, zapDirectory);
				}
			}
		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		}
	}	

	private void validateZapDirectory(String zapDirectory) throws PhrescoException {
		try {
			File targetDir = new File(zapDirectory);
			File ZapFile = new File(targetDir + File.separator + ZAP_JAR);
			if (!ZapFile.exists()) {
				throw new PhrescoException(INVALID_ZAP_DIR);
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	private void startDaemonProcess(String path, String port, String zapDirectory) throws PhrescoException {
		BufferedReader reader = null;
		String workingDir = path;
		StringBuilder builder = new StringBuilder();
		builder.append(MAVEN);
		builder.append(SPACE);
		builder.append(ANT_RUN);
		builder.append(SPACE);
		builder.append(START_ZAP_TARGET);
		builder.append(SPACE);
		builder.append(HYPEN_F);
		builder.append(SPACE);
		builder.append(PHRESCO_POM);
		builder.append(SPACE);
		builder.append(MAVEN_PARAMETER);
		builder.append(ZAPDIR);
		builder.append(EQUAL);
		builder.append('"');
		builder.append(zapDirectory);
		builder.append('"');
		builder.append(SPACE);
		builder.append(MAVEN_PARAMETER);
		builder.append(ZAP_PORT);
		builder.append(EQUAL);
		builder.append(port);
		builder.append(SPACE);
		builder.append(ZAP_PROFILE);
		log.info(builder.toString());
		Commandline commandline = new Commandline(builder.toString());
		commandline.setWorkingDirectory(path);
		try {
			String line;
			reader = Utility.executeCommand(builder.toString(), workingDir);
			while ((line = reader.readLine()) != null) {
				log.info(line);
				if (line.contains("BUILD SUCCESS")) {
					log.info("Zap Started");	
				}
			}
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
		}
	}


}
