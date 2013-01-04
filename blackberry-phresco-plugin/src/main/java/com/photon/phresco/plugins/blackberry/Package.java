/*
 * ###
 * blackberry-maven-plugin Maven Mojo
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
package com.photon.phresco.plugins.blackberry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues.Value;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Utility;

/**
 * Goal which generated the installable file (.cod) for BlackBerry
 * 
 * @goal package
 * 
 */

public class Package implements PluginConstants {

	private File baseDir;
	private String environmentName;
	private String buildName;
	private String buildNumber;
	private String keyPass;
	private int buildNo;

	private File buildDir;
	private File buildInfoFile;
	private File tempDir;
	private int nextBuildNo;
	private String tempZipName,tempZipFilePath, zipName, zipFilePath;
	private Date currentDate;
	private String sourceDirectory = "\\source";
	private String phrescoDirectory = "\\.phresco";
	// private ArrayList<String> configUrls;
	private String jsonString;
	private Log log;
	private PluginPackageUtil util;
	private String target;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException { 
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        target = configs.get(TARGET);
        File packageInfoPath = new File(baseDir + File.separator + DOT_PHRESCO_FOLDER + File.separator+ PHASE_PACKAGE_INFO);
        MojoProcessor processor = new MojoProcessor(packageInfoPath);
        Parameter parameter = processor.getParameter(PHASE_PACKAGE, TARGET);
        PossibleValues possibleValues = parameter.getPossibleValues();
        List<Value> possibleValue = possibleValues.getValue();
        for (Value possibleVal : possibleValue) {
        	if (possibleVal.getKey().equals(target)) {
        		String dependencyValue = possibleVal.getDependency();
    			keyPass = configs.get(dependencyValue);
        	}
		}
        util = new PluginPackageUtil();
        try {
			init();
			convertXMLToJSON();
			writeJSONtoJavaScript();
			generateSourceArchive();	// generate .zip file of source folder contents
			boolean buildStatus = build();
			generateArchive();
			writeBuildInfo(buildStatus);
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}

	private void init() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(environmentName)) {
				callUsage();
			}

			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			buildInfoFile = new File(buildDir.getPath() + BUILD_INFO_FILE);
			nextBuildNo = util.generateNextBuildNo(buildInfoFile);
			currentDate = Calendar.getInstance().getTime();
		} catch (Exception e) {
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	

	private void convertXMLToJSON() throws MojoExecutionException {
		try {
			File configFile = new File(baseDir.getPath() + phrescoDirectory
					+ File.separator + CONFIG_FILE);
			InputStream is = new FileInputStream(configFile);
			String xml = IOUtils.toString(is);

			XMLSerializer xmlSerializer = new XMLSerializer();
			JSON json = xmlSerializer.read(xml);
			jsonString = json.toString();
			//getLog().info("Converted JSON = " + jsonString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void writeJSONtoJavaScript() throws MojoExecutionException {
		BufferedWriter out = null;
		try {
			File configJSFile = new File(baseDir.getPath() + sourceDirectory
					+ File.separator + "js" + File.separator + "config.js");
			if (configJSFile.exists()) {
				configJSFile.delete();
			}
			configJSFile.createNewFile();

			out = new BufferedWriter(
					new FileWriter(configJSFile));
			//getLog().info("Config JSON = " + jsonString);
			out.write("var ENV_CONFIG_JSON  = '" + jsonString + "';\n");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new MojoExecutionException(e.getMessage(), e);
		} 
	}

	private void callUsage() throws MojoExecutionException {
		log.error("Invalid usage.");
		log.info("Usage of Package Goal");
		log.info(
				"mvn blackberry:package -DenvironmentName=\"Multivalued evnironment names\""
						+ " -Doutput=\"Path to output folder\"");
		throw new MojoExecutionException(
				"Invalid Usage. Please see the Usage of Package Goal");
	}

	
	private void generateSourceArchive() throws MojoExecutionException {
		try {
			tempZipName = "temp.zip";
			tempDir = new File(baseDir + sourceDirectory);
			//getLog().info("generateSourceArchive: == tempDir == " + tempDir.getPath());
			
			tempZipFilePath = tempDir.getPath() + File.separator + tempZipName;
			//getLog().info("zipFilePath == " + tempZipFilePath);

			ArchiveUtil.createArchive(tempDir.getPath(), tempZipFilePath,
					ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}
	
	private void generateArchive() throws MojoExecutionException {
		try {
			zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
			zipFilePath = buildDir.getPath() + File.separator + zipName;
			tempDir = new File(baseDir + sourceDirectory + File.separator + tempZipName.substring(0, tempZipName.length() - 4));
			ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {
			createPackage();
		} catch (Exception e) {
			isBuildSuccess = false;
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return isBuildSuccess;
	}

	private void createPackage() throws MojoExecutionException,
			PhrescoException {
		BufferedReader in = null;
		try {
			log.info("Building project ...");

			// bbwp <filename>.zip -g photon123 -o <path\to\output\folder>
			StringBuilder sb = new StringBuilder();
			sb.append(BB_BBWP_HOME);
			sb.append(STR_SPACE);
			sb.append(tempZipName);
			if (keyPass != null) {
				sb.append(STR_SPACE);
				sb.append("-g");
				sb.append(STR_SPACE);
				sb.append(keyPass); 
			}
			sb.append(STR_SPACE);
			sb.append("-o");
			sb.append(STR_SPACE);
			sb.append(tempZipName.substring(0, tempZipName.length() - 4));

			log.info("Build command: " + sb.toString());

			Commandline cl = new Commandline(sb.toString());
			cl.setWorkingDirectory(baseDir.getPath() + sourceDirectory);
			Process process = cl.execute();
			in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
			}
		} catch (CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(in);
		}
	}

	private void writeBuildInfo(boolean isBuildSuccess)
			throws MojoExecutionException {
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
	}
	
	private void cleanUp() throws MojoExecutionException {
		try {
			//getLog().info("Temp dir in cleanup = " + tempDir.getPath());
			FileUtils.deleteDirectory(tempDir);
			File f =new File(baseDir + sourceDirectory + File.separator + tempZipName);
			f.delete();
			
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}
}
