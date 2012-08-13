/*
 * ###
 * windows-phone-maven-plugin Maven Mojo
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.WP8PackageInfo;

/**
 * Goal which deploys the Java WebApp to a server
 * 
 * @goal wp8deploy
 * 
 */
public class WP8Deploy extends AbstractMojo implements PluginConstants {

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
	 * Build file name to deploy
	 * 
	 * @parameter expression="${buildName}" required="true"
	 */
	protected String buildName;

	/**
	 * @parameter expression="${environmentName}" required="true"
	 */
	protected String environmentName;
					 

	private File buildFile;
	private File tempDir;
	private File buildDir;
	private File temp;
	
	private String sourceDirectory = "\\source";
	private File rootDir;
	private File[] solutionFile;
	private String projectRootFolder;
	private WP8PackageInfo packageInfo;
	private File[] manifestFile;
	

	public void execute() throws MojoExecutionException {
		init();
		extractBuild();
		deploy();
	}

	private void init() throws MojoExecutionException {
		try {

			if (StringUtils.isEmpty(buildName) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			
			getSolutionFile();
			
			// create package info class object, to read the info from Package.appxmanifest file
			packageInfo = new WP8PackageInfo(rootDir);
			
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			buildFile = new File(buildDir.getPath() + File.separator + buildName);
			tempDir = new File(buildDir.getPath() + File.separator + buildFile.getName().substring(0, buildFile.getName().length() - 4));
			tempDir.mkdirs();
			temp = new File(tempDir.getPath());	
			
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void callUsage() throws MojoExecutionException {
		getLog().error("Invalid usage.");
		getLog().info("Usage of Deploy Goal");
		getLog().info(
				"mvn windows-phone:wp8deploy -DbuildName=\"Name of the build\""
						+ " -DenvironmentName=\"Multivalued evnironment names\"");
		throw new MojoExecutionException("Invalid Usage. Please see the Usage of Deploy Goal");
	}

	private void getSolutionFile() throws MojoExecutionException {
		try {
			// Get .sln file from the source folder
			File sourceDir = new File(baseDir.getPath() + sourceDirectory);
			solutionFile = sourceDir.listFiles(new FilenameFilter() { 
				public boolean accept(File dir, String name) { 
					return name.endsWith(".sln");
				}
			});
			
			projectRootFolder = solutionFile[0].getName().substring(0, solutionFile[0].getName().length() - 4);
			
			// Get the source/<ProjectRoot> folder
			rootDir = new File(baseDir.getPath() + sourceDirectory + WINDOWS_STR_BACKSLASH + projectRootFolder);
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void extractBuild() throws MojoExecutionException {
		try {
			ArchiveUtil.extractArchive(buildFile.getPath(), tempDir.getPath(), ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		} 
	}

	private void deploy() throws MojoExecutionException {
		BufferedReader in = null;
		try {
			getLog().info("Deploying project ...");
			
			if (packageAlreadyInstalled()) {
				System.out.println("Package already installed... Uninstalling.");
				uninstallExistingPackage();
			}
			// Get .ps1 file from the extracted contents
			File[] ps1File = tempDir.listFiles(new FilenameFilter() { 
				public boolean accept(File dir, String name) { 
					return name.endsWith(".ps1");
				}
			});
			
			// PowerShell .\Add-AppDevPackage.ps1
			StringBuilder sb = new StringBuilder();
			sb.append(WP_POWERSHELL_PATH);
			sb.append(WP_STR_DOT);
			sb.append(WINDOWS_STR_BACKSLASH);
			sb.append(ps1File[0].getName());
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory(tempDir);
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} 
	}

	
	private boolean packageAlreadyInstalled() throws MojoExecutionException {
		boolean isPackageInstalled = false;
		BufferedReader in = null;
		try {
			String packageName = packageInfo.getPackageName();
			
			// PowerShell (Get-AppxPackage -Name <packageName>).count
			StringBuilder sb = new StringBuilder();
			sb.append(WP_POWERSHELL_PATH);
			sb.append("(Get-AppxPackage -Name " + packageName + ").count");
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory(tempDir);
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			String tempVar = null;
			while ((line = in.readLine()) != null) {
				tempVar = line;
			}
			if (Integer.parseInt(tempVar.trim()) > 0) {
				isPackageInstalled = true;
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return isPackageInstalled;
		
	}
	
	private void uninstallExistingPackage() throws MojoExecutionException {
		BufferedReader in = null;
		try {
			String packageFullName = getPackageFullName();
			// PowerShell (Get-AppxPackage -Name <packageName>).count
			StringBuilder sb = new StringBuilder();
			sb.append(WP_POWERSHELL_PATH);
			sb.append("Remove-AppxPackage " + packageFullName);
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory(tempDir);
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			
			while ((line = in.readLine()) != null) {				
			}
			
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}		
	}
	
	private String getPackageFullName() throws MojoExecutionException {
		String packageFullName = null;
		BufferedReader in = null;
		try {
			String packageName = packageInfo.getPackageName();
			
			// PowerShell (Get-AppxPackage -Name <packageName>).count
			StringBuilder sb = new StringBuilder();
			sb.append(WP_POWERSHELL_PATH);
			sb.append("(Get-AppxPackage -Name " + packageName + ").packagefullname");
			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory(tempDir);
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				packageFullName = line;				
			}
			
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return packageFullName;		
	}
}
