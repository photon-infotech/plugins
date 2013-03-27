/**
 * dotnet-phresco-plugin
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
package com.photon.phresco.plugins.dotnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

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

public class Deploy implements PluginConstants {
	
	private MavenProject project;
	private File baseDir;
	private String buildNumber;
	private String environmentName;

	private File buildDir;
	private File buildFile;
	private File targetDir;
	private String applicationName;
	private String siteName;
	private String serverport;
	private String serverprotocol;
	private String deploylocation;
	private Log log;
	private PluginUtils pUtil;
	
	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        pUtil = new PluginUtils();
        
        try {
			init();
			extractBuild();
			listSites();
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

			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			targetDir = new File(project.getBuild().getDirectory());
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());

			List<com.photon.phresco.configuration.Configuration> configurations = pUtil.getConfiguration(baseDir, environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration configuration : configurations) {
				applicationName = configuration.getProperties().getProperty(Constants.APPLICATION_NAME);
				siteName = configuration.getProperties().getProperty(Constants.SITE_NAME);
				serverport = configuration.getProperties().getProperty(Constants.SERVER_PORT);
				serverprotocol = configuration.getProperties().getProperty(Constants.SERVER_PROTOCOL);
				deploylocation = configuration.getProperties().getProperty(Constants.SERVER_DEPLOY_DIR);
				break;
			}
		} catch (Exception e) {
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void callUsage() throws MojoExecutionException {
		log.error("Invalid usage.");
		log.info("Usage of Deploy Goal");
		log.info(
				"mvn dotnet:deploy -DbuildNumber=\"Number of the build\""
						+ " -DenvironmentName=\"Multivalued evnironment names\"");
		throw new MojoExecutionException("Invalid Usage. Please see the Usage of Deploy Goal");
	}

	private void listSites() throws MojoExecutionException {
		BufferedReader in = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("APPCMD list sites");
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory("C:/Windows/System32/inetsrv");
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains(siteName)) {
					listApp();
				} 
			}
			executeAddSite();
			executeAddApp();
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void listApp() throws MojoExecutionException {
		BufferedReader in = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("APPCMD  list app /site.name:");
			sb.append(siteName);
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory("C:/Windows/System32/inetsrv");
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains(applicationName)) {
					throw new MojoExecutionException(
							"Application Already Exists in Site . Please configure new Application or delete the already existing one ");
				}
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void executeAddSite() throws MojoExecutionException {
		BufferedReader in = null;
		log.info("Creation of Site executed...");
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("APPCMD add site /name:");
			sb.append(siteName);
			sb.append(STR_SPACE);
			sb.append("/bindings:");
			sb.append(serverprotocol + "/*:" + serverport + ":");
			sb.append(STR_SPACE);
			sb.append("/physicalPath:");
			sb.append("\"" + deploylocation + "\"");
			Commandline cl = new Commandline(sb.toString());

			cl.setWorkingDirectory("C:/Windows/System32/inetsrv");
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line); // do not use getLog() here as this line already contains the log type.
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void extractBuild() throws MojoExecutionException {
		try {
			ArchiveUtil.extractArchive(buildFile.getPath(), targetDir.getPath(), ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void executeAddApp() throws MojoExecutionException {
		BufferedReader in = null;
		log.info("Creation of Application executed...");
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("APPCMD add app /site.name:");
			sb.append(siteName);
			sb.append(STR_SPACE);
			sb.append("/path:/" + applicationName);
			sb.append(STR_SPACE);
			sb.append("/physicalPath:");
			sb.append("\"" + targetDir.getPath() + "\"");
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory("C:/Windows/System32/inetsrv");
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line); // do not use getLog() here as this line already contains the log type.
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
