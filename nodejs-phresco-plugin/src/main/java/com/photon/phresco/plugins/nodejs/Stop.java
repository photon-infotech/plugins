/*
 * ###
 * nodejs-maven-plugin Maven Mojo
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
package com.photon.phresco.plugins.nodejs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;

public class Stop implements PluginConstants {

	private File baseDir;
	private Log log;

	public void stop(MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		ByteArrayInputStream is = null;
		BufferedReader reader = null;
		InputStreamReader isr = null;
		FileWriter fileWriter = null;
		String result = "";
		try {
			stopNodeJS();
			result = "Server Stopped Successfully...";
			is = new ByteArrayInputStream(result.getBytes());
			isr = new InputStreamReader(is);
			reader = new BufferedReader(isr);
			fileWriter = new FileWriter(baseDir.getPath() + LOG_FILE_DIRECTORY + SERVER_LOG_FILE, false);
			LogWriter writer = new LogWriter();
			writer.writeLog(reader, fileWriter);
		} catch (MojoExecutionException e) {
			log.error("Failed to stop server " + e);
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (Exception e) {
				throw new PhrescoException(e);
			}
		}
	}

	private void stopNodeJS() throws MojoExecutionException {
		try {
			if (System.getProperty(OS_NAME).startsWith(WINDOWS_PLATFORM)) {
				Runtime.getRuntime().exec("cmd /X /C taskkill /F /IM node.exe");
			} else if (System.getProperty(OS_NAME).startsWith("Mac")) {
				Runtime.getRuntime().exec("killall node");
			} else {
				Runtime.getRuntime().exec("pkill node");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
}
