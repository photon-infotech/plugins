package com.photon.phresco.plugins.seo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.configuration.ConfigReader;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.impl.ConfigManagerImpl;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class SeoTest implements SeoConstants {
	private File baseDir;
	private String environmentName;
	private String url;
	
	public void seoTest(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		try {
			Properties properties = null;
			baseDir = mavenProjectInfo.getBaseDir();
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			environmentName = configs.get(ENVIRONMENT_NAME);
			url = configs.get(URL);
			File configPath = new File(baseDir + File.separator + DOT_PHRESCO_FOLDER + File.separator  + CONFIG_FILE);
			if (!configPath.exists()) {
				throw new PhrescoException(CONFIG_FILE_NOT_FOUND_ERROR);
			}
			ConfigReader confReader = new ConfigReader(configPath);
			List<com.photon.phresco.configuration.Configuration> configurationList = confReader.getConfigurations(environmentName, SERVER);
			if (CollectionUtils.isNotEmpty(configurationList)) {
				com.photon.phresco.configuration.Configuration config = configurationList.get(0);
				if (config != null) {
					properties = config.getProperties();
				}
			}
			String Scriptpath = baseDir +File.separator  + TEST_FOLDER + File.separator + SEO;
			properties.put(HOST, url);
			updateScriptConfig(Scriptpath, environmentName, properties);
			runSeoTest(Scriptpath, log);
			
		} 
		catch (ConfigurationException e) {
			throw new PhrescoException(e);
		}
	}

	private void runSeoTest(String scriptpath, Log log) throws PhrescoException {
		BufferedReader reader = null;
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(MAVEN);
			builder.append(SPACE);
			builder.append("test");
			builder.append(SPACE);
			builder.append(MAVEN_PARAMETER);
			builder.append(MAVEN_TEST_FAILURE_IGNORE);
			builder.append(EQUAL);
			builder.append(TRUE);
			Commandline commandline = new Commandline(builder.toString());
			commandline.setWorkingDirectory(new File(scriptpath));
			String line;
			reader = Utility.executeCommand(builder.toString(), scriptpath);
			while ((line = reader.readLine()) != null) {
				log.info(line);
			}
		} catch (IOException e) {
			throw new PhrescoException(e);
			
		}
	}

	private void updateScriptConfig(String scriptpath, String environmentName, Properties properties) throws PhrescoException {
		try {
			StringBuffer path = new StringBuffer();
			path.append(scriptpath);
			path.append(File.separator);
			path.append(SRC_FOLDER);
			path.append(File.separator);
			path.append(MAIN_FOLDER);
			path.append(File.separator);
			path.append(RESOURCES_FOLDER);
			path.append(File.separator);
			path.append(CONFIG_FILE);
			File scriptEnvPath = new File(path.toString());
			if (!scriptEnvPath.exists()) {
				throw new PhrescoException(SEO_DIR_NOT_EXISTS);
			}
			ConfigReader reader = new ConfigReader(scriptEnvPath);
			com.photon.phresco.configuration.Configuration config = reader.getConfigurations(environmentName, SERVER).get(0);
			String oldConfigName = config.getName();
			config.setProperties(properties);

			ConfigManager manager =  new ConfigManagerImpl(new File(path.toString()));
			manager.updateConfiguration(environmentName, oldConfigName, config);
			
		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		}

	}	

}
