/*
 * ###
 * sitecore-maven-plugin Maven Mojo
 * 
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ###
 */

package com.photon.phresco.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.model.BuildInfo;
import com.photon.phresco.model.SettingsInfo;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.PluginConstants;
import com.photon.phresco.util.PluginUtils;

/**
 * Goal which deploys the Sitecore webapp project
 * 
 * @goal deploy
 * 
 */
public class SiteCoreDeploy extends AbstractMojo implements PluginConstants {

	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * @parameter expression="${project.basedir}" required="true"
	 * @readonly
	 */
	protected File baseDir;

	/**
	 * @parameter expression="${buildNumber}" required="true"
	 */
	protected String buildNumber;

	/**
	 * @parameter expression="${environmentName}" required="true"
	 */
	protected String environmentName;

	private File buildDir;
	private File buildFile;
	private File targetDir;
	private String applicationName;
	private String siteName;
	private String serverport;
	private String serverprotocol;
	private String deploylocation;

	public void execute() throws MojoExecutionException {
		init();
		extractBuild();
		copyBinContent();
		listSites();
	}

	private void init() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(buildNumber) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}

			PluginUtils pu = new PluginUtils();
			BuildInfo buildInfo = pu.getBuildInfo(Integer.parseInt(buildNumber));
			getLog().info("Build Name " + buildInfo);

			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			targetDir = new File(project.getBuild().getDirectory());
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());
			getLog().info("buildFile path " + buildFile.getPath());

			List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_SERVER);
			for (SettingsInfo serverDetails : settingsInfos) {
				applicationName = serverDetails.getPropertyInfo(Constants.APPLICATION_NAME).getValue();
				siteName = serverDetails.getPropertyInfo(Constants.SITE_NAME).getValue();
				serverport = serverDetails.getPropertyInfo(Constants.SERVER_PORT).getValue();
				serverprotocol = serverDetails.getPropertyInfo(Constants.SERVER_PROTOCOL).getValue();
				deploylocation = serverDetails.getPropertyInfo(Constants.SITECORE_INST_PATH).getValue();

				break;
			}
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void callUsage() throws MojoExecutionException {
		getLog().error("Invalid usage.");
		getLog().info("Usage of Deploy Goal");
		getLog().info(
				"mvn dotnet:deploy -DbuildNumber=\"Number of the build\""
						+ " -DenvironmentName=\"Multivalued evnironment names\"");
		throw new MojoExecutionException("Invalid Usage. Please see the Usage of Deploy Goal");
	}

	private void copyBinContent() throws MojoExecutionException {
		File outputDir = new File(deploylocation + "/Website/bin") ;
		File inputDir = new File(targetDir + "/bin");
		copyDirectory(inputDir, outputDir);
	}

	private void copyDirectory(File sourceLocation, File targetLocation) throws MojoExecutionException {
		try {
			//TODO use this FileUtils.copyDirectory(srcDir, destDir); instead of below code
			if (sourceLocation.isDirectory()) {
				if (!targetLocation.exists()) {
					targetLocation.mkdir();
				}

				String[] children = sourceLocation.list();
				for (int i = 0; i < children.length; i++) {
					copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
				}
			} else {
				InputStream in = new FileInputStream(sourceLocation);
				OutputStream out = new FileOutputStream(targetLocation);

				// Copy the bits from instream to outstreams
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
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
			executeAddSite();
			executeAddApp();
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void executeAddSite() throws MojoExecutionException {
		BufferedReader in = null;
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
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("APPCMD add app /site.name:");
			sb.append(siteName);
			sb.append(STR_SPACE);
			sb.append("/path:/" + applicationName);
			sb.append(STR_SPACE);
			sb.append("/physicalPath:");
			sb.append("\"" + deploylocation + "/Website" + "\"");
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

	private List<SettingsInfo> getSettingsInfo(String configType) throws PhrescoException {
		ProjectAdministrator projAdmin = PhrescoFrameworkFactory.getProjectAdministrator();
		return projAdmin.getSettingsInfos(configType, baseDir.getName(), environmentName);
	}
}
