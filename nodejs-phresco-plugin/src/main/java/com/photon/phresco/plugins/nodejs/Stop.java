/**
 * nodejs-phresco-plugin
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
package com.photon.phresco.plugins.nodejs;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.util.Constants;

public class Stop implements PluginConstants {


	public void stop(MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		try {
			stopNodeJS();
			log.info("Server Stopped Successfully...");
		} catch (MojoExecutionException e) {
			log.error("Failed to stop server " + e);
			throw new PhrescoException(e);
		}
	}

	private void stopNodeJS() throws MojoExecutionException {
		try {
			if (System.getProperty(Constants.OS_NAME).startsWith(Constants.WINDOWS_PLATFORM)) {
				Runtime.getRuntime().exec("cmd /X /C taskkill /F /IM node.exe");
			} else if (System.getProperty(Constants.OS_NAME).startsWith("Mac")) {
				Runtime.getRuntime().exec("killall node");
			} else {
				Runtime.getRuntime().exec("pkill node");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
}
