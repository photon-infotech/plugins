/*
 * ###
 * java-maven-plugin Maven Mojo
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
package com.photon.phresco.plugins.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.google.gson.Gson;
import com.photon.phresco.configuration.ConfigurationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.util.Utility;

public class Stop implements PluginConstants {
	private File baseDir;
	private String projectCode;
	private Log log;

	public void stop(MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		projectCode = mavenProjectInfo.getProjectCode();
		try {
			stopServer();
			log.info("Server stopped successfully");
		} catch (MojoExecutionException e) {
			log.error("Failed to stop server " + e);
			throw new PhrescoException(e);
		}
	}

	private void stopServer() throws MojoExecutionException {
		String portNo = findPortNumber();
		if (System.getProperty(OS_NAME).startsWith(WINDOWS_PLATFORM)) {
			stopJavaServerInWindows("netstat -ao | findstr " + portNo + " | findstr LISTENING");
		} else if (System.getProperty(OS_NAME).startsWith("Mac")) {
			stopJavaServer("lsof -i tcp:" + portNo + " | awk '{print $2}'");
		} else {
			stopJavaServer("fuser " + portNo + "/tcp " + "|" + "awk '{print $1}'");
		}
	}

	private void stopJavaServerInWindows(String command) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		try {
			String pid = "";
			bufferedReader = Utility.executeCommand(command, baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				pid = line.substring(line.length() - 4, line.length());
			}
			Runtime.getRuntime().exec("cmd /X /C taskkill /F /PID " + pid);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}

	private void stopJavaServer(String command) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		try {
			String pid = "";
			bufferedReader = Utility.executeCommand(command, baseDir.getPath());
			String line = null;
			int count = 1;
			while ((line = bufferedReader.readLine()) != null) {
				if (count == 2) {
					pid = line.trim();
				}
				count++;
			}
			Runtime.getRuntime().exec(JAVA_UNIX_PROCESS_KILL_CMD + pid);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}

	public String findPortNumber() throws MojoExecutionException {
		ConfigurationInfo info = new ConfigurationInfo();
		try {
			File pomPath = new File(Utility.getProjectHome() + File.separator + projectCode + File.separator
					+ DOT_PHRESCO_FOLDER + File.separator + NODE_ENV_FILE);
			BufferedReader reader = new BufferedReader(new FileReader(pomPath));
			Gson gson = new Gson();
			info = gson.fromJson(reader, ConfigurationInfo.class);
			return info.getServerPort();

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
}
