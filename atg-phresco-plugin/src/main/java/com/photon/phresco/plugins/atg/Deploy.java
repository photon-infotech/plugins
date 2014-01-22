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
package com.photon.phresco.plugins.atg;

import java.io.BufferedReader;
import java.io.File;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class Deploy implements PluginConstants, AtgConstants {
	
	private MavenProject project;
	private File baseDir;
	private String buildNumber;
	private File buildDir;
	private String environmentName;
	private File buildFile;
	private Log log;
	private PluginUtils pUtil;
	private String subModule = "";
	private File workingDirectory;
	private String dotPhrescoDirName;
	private String atgPath;
    private File dotPhrescoDir;
    private String atgServer;
    private String jbossDeployPath;
    private String defaultModules;
    private String otherModules;
	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        atgPath = configs.get(ATG_PATH);
        atgServer = configs.get(SERVER_NAME);
        jbossDeployPath = configs.get(JBOSS_DEPLOY_PATH);
        defaultModules = configs.get(DEFAULT_MODULES);
        otherModules = configs.get(OTHER_MODULES);
        pUtil = new PluginUtils();
        workingDirectory = new File(baseDir.getPath());
        if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
        	subModule = mavenProjectInfo.getModuleName();
        	workingDirectory = new File(baseDir.getPath() + File.separator + subModule);
        } 
        dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        dotPhrescoDir = workingDirectory;
        if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        	dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName + File.separatorChar + subModule);
        }
		try {
			init();
			extractBuild();
			deployToServer();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		
	}
	
	private void init() throws MojoExecutionException {
		try {
			BuildInfo buildInfo = pUtil.getBuildInfo(Integer.parseInt(buildNumber), workingDirectory.getPath());
			buildDir = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY);// build dir
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());// filename
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void extractBuild() throws MojoExecutionException {
		try {
			ArchiveUtil.extractArchive(buildFile.getPath(), atgPath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void deployToServer() throws MojoExecutionException, PhrescoException {
		try {
			BufferedReader bufferedReader = null;
			boolean errorParam = false;
			ApplicationInfo appInfo = pUtil.getAppInfo(dotPhrescoDir);
			String homeDirectory = atgPath + File.separator + ATG_HOME + File.separator + ATG_BIN;
			StringBuilder sb = new StringBuilder();
			sb.append(RUN_ASSEMBLER);
			sb.append(OVERWRITE);
			sb.append(ATG_SERVER);
			sb.append(atgServer);
			sb.append(WHITESPACE);
			sb.append(jbossDeployPath);
			sb.append(WHITESPACE);
			sb.append(MODULES);
			if(defaultModules != null) {
				String[] defaultModulesList = defaultModules.split(COMMA);
				for(String module : defaultModulesList) {
					sb.append(module);
					sb.append(WHITESPACE);
				}
			}
			if(otherModules != null) {
				String[] otherModulesList = otherModules.split(COMMA);
				for(String module : otherModulesList) {
					sb.append(module);
					sb.append(WHITESPACE);
				}
			}
			sb.append(appInfo.getAppDirName());
			bufferedReader = Utility.executeCommand(sb.toString(), homeDirectory);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				if (line.startsWith("[ERROR]")) {
					System.out.println(line); //do not use getLog() here as this line already contains the log type.
					errorParam = true;
				}
			}
			if (errorParam) {
				throw new MojoExecutionException("Remote Deploy Failed ");
			} else {
				log.info(
						" Project is deployed to the server");
			}		
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
