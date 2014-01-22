/**
 * xcode-phresco-plugin
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
package com.photon.phresco.plugins.xcode;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class Deploy implements PluginConstants {
	
	private Log log;
	/**
	 * Execute the xcode command line utility for iphone deployment.
	 * @throws PhrescoException 
	 */
	public void deploy(Configuration config, MavenProjectInfo mavenProjectInfo, final Log log) throws PhrescoException {
		try {
			System.out.println("Deployment started ");
			this.log = log;
			Map<String, String> configs = MojoUtil.getAllValues(config);
			File baseDir = mavenProjectInfo.getBaseDir();
			String pomFile = mavenProjectInfo.getProject().getFile().getName();
			String buildNumber = configs.get(BUILD_NUMBER);
			String family = configs.get(FAMILY);
			String simVersion = configs.get(SIM_VERSION);
			String deviceType = configs.get(DEVICE_TYPE);
			String triggerSimulator = configs.get(TRIGGER_SIMULATOR);
			String projectType = configs.get(PROJECT_TYPE);
			if(StringUtils.isNotEmpty(projectType) && projectType.equals(MAC)) {
				macAppDeploy(Integer.parseInt(buildNumber), baseDir.getPath());
				return;
			}
			if (StringUtils.isEmpty(buildNumber)) {
				System.out.println("Build number is empty for deployment . ");
				throw new PhrescoException("Build number is empty for deployment . ");
			}
			
			if (StringUtils.isEmpty(deviceType)) {
				System.out.println("deviceType is not specified for deployment . ");
				throw new PhrescoException("deviceType is not specified for deployment . ");
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(XCODE_DEPLOY_COMMAND);
			
			if (StringUtils.isNotEmpty(buildNumber)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + BUILD_NUMBER + EQUAL + buildNumber);
			}
			
			if (StringUtils.isNotEmpty(family)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + FAMILY + EQUAL + family);
			}
			
			if (StringUtils.isNotEmpty(simVersion)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + SIMULATOR_VERSION + EQUAL + simVersion);
			}
			
			List<Parameter> parameters = config.getParameters().getParameter();
			for (Parameter parameter : parameters) {
				if (DEVICE_TYPE.equals(parameter.getKey())) {
					List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
					for (MavenCommand mavenCommand : mavenCommands) {
						if (mavenCommand.getKey().equals(deviceType)) {
							sb.append(STR_SPACE);
							sb.append(mavenCommand.getValue());
						}
					}
				}
			}
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + TRIGGER_SIMULATOR + EQUAL + triggerSimulator);
			
			if(!Constants.POM_NAME.equals(pomFile)) {
				sb.append(STR_SPACE);
				sb.append(Constants.HYPHEN_F);
				sb.append(STR_SPACE);
				sb.append(pomFile);
			}
			System.out.println("Command " + sb.toString());
			
			boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir.getPath(), baseDir.getPath(), "");
			if(!status) {
				try {
					throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
				} catch (MojoExecutionException e) {
					throw new PhrescoException(e);
				}
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	private void macAppDeploy(int buildNumber, String baseDir) throws PhrescoException {
		PluginUtils pu = new PluginUtils();
		try {
			BuildInfo buildInfo = pu.getBuildInfo(buildNumber);
			String appPath = buildInfo.getDeployLocation();
			StringBuilder sb = new StringBuilder();
			sb.append("open -n .");
			sb.append(STR_SPACE);
			sb.append("--args -AppCommandLineArg");
			System.out.println("command : " + sb.toString());
			Utility.executeStreamconsumer(sb.toString(), appPath, baseDir, "");
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
	}

}
