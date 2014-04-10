/**
 * android-phresco-plugin
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
package com.photon.phresco.plugins.android;

import java.util.*;

import org.apache.commons.lang.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.*;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class Deploy implements PluginConstants {

	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException, MojoExecutionException  {
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String buildNumber = configs.get(BUILD_NUMBER);
		String deviceValue = configs.get(DEVICES);
		String serialNumber = configs.get(SERIAL_NUMBER);
		String workingDir = mavenProjectInfo.getBaseDir().getPath();
		String pomFile = mavenProjectInfo.getProject().getFile().getName();
		
		if (StringUtils.isEmpty(buildNumber)) {
			System.out.println("buildNumber is empty . ");
			throw new PhrescoException("buildNumber is empty . ");
		}
		
		if (StringUtils.isEmpty(deviceValue)) {
			System.out.println("devices is empty . ");
			throw new PhrescoException("devices is empty . ");
		}
		
		log.info("Project is Deploying...");
		StringBuilder sb = new StringBuilder();
		sb.append(ANDROID_DEPLOY_COMMAND);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + BUILD_NUMBER + EQUAL + buildNumber);
		
		String otherDiviceValue = configs.get(deviceValue);
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PluginConstants.PLUGIN_PARAMETER)) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if(mavenCommand.getKey().equalsIgnoreCase(deviceValue)) {
						sb.append(STR_SPACE);
						sb.append(mavenCommand.getValue());
					} 
				}
			}
			if(parameter.getKey().equalsIgnoreCase(deviceValue)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + ANDROID_DEVICE + EQUAL + otherDiviceValue);
			}
		}
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + ANDROID_EMULATOR + EQUAL + DEFAULT);
		
		if(!Constants.POM_NAME.equals(pomFile)) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomFile);
		}
		log.info("Project is Deploying...");
		log.info("Command " + sb.toString());
		boolean status = Utility.executeStreamconsumer(sb.toString(), workingDir, workingDir, "");
		if(!status) {
			throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
		}
	}
}
