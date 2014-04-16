/**
 * java-phresco-plugin
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
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
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class Deploy implements PluginConstants {
	
	private static final String PROTOCOL_POSTFIX = "://";
	private static final String CQ5_PROFILE_ID = "-PautoInstallPackage";
	private static final String PASSWORD = "-Dvault.password";
	private static final String USER_NAME = "-Dvault.userId";
	private static final String TARGET_URL = "-Dvault.targetURL";
	private static final String PACKAGE_FILE ="-Dvault.file";
	private MavenProject project;
	private File baseDir;
	private String buildNumber;
	private File buildDir;
	private String environmentName;
	private File tempDir;
	private Log log;
	private PluginUtils pUtil;
	private String pomFile;
	private String dotPhrescoDirName;
    private File dotPhrescoDir;
	private String buildVersion;
    
	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		pomFile = project.getFile().getName();
		buildVersion = mavenProjectInfo.getBuildVersion();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        pUtil = new PluginUtils();
        dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        dotPhrescoDir = baseDir;
        if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        	dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
        }
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
			buildDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);
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
			List<com.photon.phresco.configuration.Configuration> configurations = pUtil.getConfiguration(dotPhrescoDir, environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
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
		String serverhost = configuration.getProperties().getProperty(Constants.SERVER_HOST);
		String serverport = configuration.getProperties().getProperty(Constants.SERVER_PORT);
		String serverprotocol = configuration.getProperties().getProperty(Constants.SERVER_PROTOCOL);
		String serverusername = configuration.getProperties().getProperty(Constants.SERVER_ADMIN_USERNAME);
		String serverpassword = configuration.getProperties().getProperty(Constants.SERVER_ADMIN_PASSWORD);
		String context = configuration.getProperties().getProperty(Constants.SERVER_CONTEXT);
		String certificatePath = configuration.getProperties().getProperty(Constants.CERTIFICATE);

		
		StringBuilder cq5Url = new StringBuilder();
		cq5Url.append(serverprotocol);
		cq5Url.append(PROTOCOL_POSTFIX);
		cq5Url.append(serverhost);
		cq5Url.append(COLON);
		cq5Url.append(serverport);
		cq5Url.append(FORWARD_SLASH);
		cq5Url.append(context);
		
		File cq5File = getCq5File();
		deployToServer(cq5File, cq5Url.toString(), serverusername, serverpassword, serverprotocol, certificatePath);
	}

	private File getCq5File() throws PhrescoException {
		try {
			String[] list = buildDir.list(new CQ5ZipFileNameFilter());
			if (list.length > 0) {
				String cq5Zip = list[0];
				File cq5ZipFile = new File(buildDir.getPath() + "/" + cq5Zip);
				return cq5ZipFile;
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return null;
	}
	
	private void deployToServer(File cq5File, String cq5Url, String username, String password, String serverprotocol, String certificatePath) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_CLEAN);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_INSTALL);
			sb.append(STR_SPACE);
			sb.append(CQ5_PROFILE_ID);
			sb.append(STR_SPACE);
			sb.append(TARGET_URL+"="+cq5Url);
			sb.append(STR_SPACE);
			sb.append(USER_NAME+"="+username);
			sb.append(STR_SPACE);
			sb.append(PASSWORD+"="+password);
			sb.append(STR_SPACE);
			sb.append(PACKAGE_FILE+"="+"\""+cq5File+"\"");
			sb.append(STR_SPACE);
			sb.append(SKIP_TESTS);

//			sb.append("org.apache.cq5:maven-cq5-plugin:install-file");
//			
//			sb.append(STR_SPACE);
//			sb.append("-Dcq5.file=");
//			sb.append(cq5File.getPath());
//			
//			sb.append(STR_SPACE);
//			sb.append("-Dcq5Url=");
//			sb.append(cq5Url);
//			
//			sb.append(STR_SPACE);
//			sb.append("-Duser=");
//			sb.append(user);
//			
//			sb.append(STR_SPACE);
//			sb.append("-Dpassword=");
//			sb.append(password);
			
			if(!Constants.POM_NAME.equals(pomFile)) {
				sb.append(STR_SPACE);
				sb.append(Constants.HYPHEN_F);
				sb.append(STR_SPACE);
				sb.append(pomFile);
			}
			if (serverprotocol.equals(HTTPS) && certificatePath != null) {
				File certificateFile = null;
				if (new File(baseDir.getPath(), certificatePath).exists()) {
					certificateFile = new File(certificatePath);
				} else {
					certificateFile = new File("../" + certificatePath);
				}
				sb.append(STR_SPACE);
				sb.append(JAVAX_TRUSTSTORE);
				sb.append("\""+certificateFile.getPath()+"\"");
			}
			sb.append(STR_SPACE);
			sb.append("-Dbuild.version=" + buildVersion);
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
				log.info(" Project is Deployed into " + cq5Url);
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

class QDJarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".Jar");
	}
}

class CQ5ZipFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".zip");
	}
}
