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
package com.photon.phresco.plugins.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.photon.phresco.configuration.ConfigurationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.util.Constants;

public class Stop implements PluginConstants {
	private File workingDirectory;
	private String subModule = "";
	private String dotPhrescoDirName;
	private File dotPhrescoDir;

	public void stop(MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		File baseDir = mavenProjectInfo.getBaseDir();
		MavenProject project = mavenProjectInfo.getProject();
		workingDirectory = baseDir;
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			subModule = mavenProjectInfo.getModuleName();
			workingDirectory = new File(baseDir + File.separator + subModule);
		}
		dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		dotPhrescoDir = workingDirectory;
		if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
			dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
		}
		File runagsInfoFile = new File(dotPhrescoDir + File.separator + DOT_PHRESCO_FOLDER + File.separator + ENV_FILE);
		String portNumber = findPortNumber(runagsInfoFile);
		PluginUtils pluginutil = new PluginUtils();
		pluginutil.stopServer(portNumber, workingDirectory);
		log.info("Server stopped successfully");
	}

	private String findPortNumber(File runagsInfoFile) throws PhrescoException {
		ConfigurationInfo info = new ConfigurationInfo();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(runagsInfoFile));
			Gson gson = new Gson();
			info = gson.fromJson(reader, ConfigurationInfo.class);
			return info.getServerPort();
		} catch (JsonSyntaxException e) {
			throw new PhrescoException(e);
		} catch (JsonIOException e) {
			throw new PhrescoException(e);
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		}
	}
}
