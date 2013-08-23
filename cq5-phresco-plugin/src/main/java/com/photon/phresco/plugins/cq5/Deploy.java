/**
 * java-phresco-plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.photon.phresco.plugins.cq5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.Element;

import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.DatabaseUtil;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Dependency;
import com.phresco.pom.model.Plugin;
import com.phresco.pom.util.PomProcessor;

public class Deploy implements PluginConstants {
	
	private static final String SLING_ARTIFACT_ID = "maven-sling-plugin";
	private static final String SLING_GROUP_ID = "org.apache.sling";
	private static final String PASSWORD = "password";
	private static final String USER = "user";
	private static final String SLING_URL = "slingUrl";
	private MavenProject project;
	private File baseDir;
	private String buildNumber;
	private File buildDir;
	private String environmentName;
	private File buildFile;
	private File tempDir;
	private String context;
	private Log log;
	private PluginUtils pUtil;
	private String pomFile;
	
	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		pomFile = project.getFile().getName();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        pUtil = new PluginUtils();
        
		try {
			init();
			deployToServer();
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		
	}
	
	private void init() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(buildNumber) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			BuildInfo buildInfo = pUtil.getBuildInfo(Integer.parseInt(buildNumber));
			log.info("Build Name " + buildInfo);
			buildDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());
			tempDir = new File(buildDir.getPath() + TEMP_DIR);
			tempDir.mkdirs();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void callUsage() throws MojoExecutionException {
		log.error("Invalid usage.");
		log.info("Usage of Deploy Goal");
		log.info("mvn phresco:deploy -DbuildName=\"Name of the build\"" + " -DenvironmentName=\"Multivalued evnironment names\"");
		throw new MojoExecutionException("Invalid Usage. Please see the Usage of Deploy Goal");
	}
	
	private void deployToServer() throws MojoExecutionException, PhrescoException {
		try {
			List<com.photon.phresco.configuration.Configuration> configurations = pUtil.getConfiguration(baseDir, environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration configuration : configurations) {
				deploy(configuration);
			}			
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}
	
	private void deploy(com.photon.phresco.configuration.Configuration configuration) throws MojoExecutionException, PhrescoException {
		if (configuration == null) {
			return;
		}
		String slingUrl = configuration.getProperties().getProperty(Constants.SLING_URL);
		String user = configuration.getProperties().getProperty(Constants.USER);
		String password = configuration.getProperties().getProperty(Constants.PASSWORD);
		
		configurationSlingUrl(slingUrl, user, password);
		deployToServer(slingUrl);
	}

	private void configurationSlingUrl(String slingUrl, String user, String password) throws PhrescoException {
		try {
			PomProcessor processor = new PomProcessor(project.getFile());
			Plugin plugin = processor.getPlugin(SLING_GROUP_ID, SLING_ARTIFACT_ID);
			com.phresco.pom.model.Plugin.Configuration slingConfiguration = plugin.getConfiguration();
			List<Element> elements = slingConfiguration.getAny();
			for (Element element : elements) {
				String tagName = element.getTagName();
				System.out.println("Tag name => " + tagName);
				if (SLING_URL.equals(tagName)) {
					element.setTextContent(slingUrl);
				}
				if (USER.equals(tagName)) {
					element.setTextContent(user);			
				}
				if (PASSWORD.equals(tagName)) {
					element.setTextContent(password);
				}
			}
			processor.save();
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void deployToServer(String slingUrl) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_CLEAN);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_INSTALL);
			sb.append(SKIP_TESTS);
			if(!Constants.POM_NAME.equals(pomFile)) {
				sb.append(STR_SPACE);
				sb.append(Constants.HYPHEN_F);
				sb.append(STR_SPACE);
				sb.append(pomFile);
			}

			System.out.println("Deploy command : " + sb.toString());
			bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("[ERROR]")) {
					System.out.println(line);
					errorParam = true;
				}
			}
			
			if (errorParam) {
				throw new MojoExecutionException("Deployment Failed ");
			} else {
				log.info(" Project is Deploying into " + slingUrl);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}  finally {
			Utility.closeStream(bufferedReader);
		}
	}
	
	public class PhrescoDirFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            return name.equals(DOT_PHRESCO_FOLDER);
        }
    }
	
	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}