/**
 * xcode-phresco-plugin
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
package com.photon.phresco.plugins.xcode;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class FunctionalTest implements PluginConstants {
	public void functionalTest(Configuration config, MavenProjectInfo mavenProjectInfo) throws PhrescoException, MojoExecutionException {
		MavenProject project = mavenProjectInfo.getProject();
		String seleniumToolType = project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_SELENIUM_TOOL);
		String workingDir = project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
		String baseDir = mavenProjectInfo.getBaseDir().getPath();
		String pomFile = project.getFile().getName();
		Map<String, String> configs = MojoUtil.getAllValues(config);
		// calabash Execution
		if(StringUtils.isNotEmpty((seleniumToolType)) && seleniumToolType.equals(CALABASH)) {
			StringBuilder builder = new StringBuilder();
			builder.append(CALABASH_IOS_COMMAND)
			.append(STR_SPACE);
			String device = configs.get(DEVICE);
			if (StringUtils.isNotEmpty(device) && device.equals(DEVICE_IPAD)) {
				builder.append("DEVICE=" + device)
				.append(STR_SPACE);
			}
			builder.append("-f junit -o test-reports -f html -o test-reports/calabash.html");
			Utility.executeStreamconsumer(builder.toString(), baseDir + File.separator + workingDir, baseDir, FUNCTIONAL);
			return;
		} else if(StringUtils.isNotEmpty((seleniumToolType)) && seleniumToolType.equals(APPIUM)) {
			Utility.executeStreamconsumer(TEST_COMMAND, baseDir + File.separator + workingDir, baseDir, FUNCTIONAL);
			return;
		}
		String buildNumber = configs.get(BUILD_NUMBER);
		String deviceId = configs.get(DEVICE_ID);
		if (StringUtils.isEmpty(buildNumber)) {
			System.out.println("Build Number is empty . ");
			throw new PhrescoException("Build Number is empty . ");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(XCODE_FUNCTIONAL_COMMAND);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + BUILD_NUMBER + EQUAL + buildNumber);
		
		if (StringUtils.isNotEmpty(deviceId) && deviceId.equalsIgnoreCase("device")) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + DEVICE_ID + EQUAL + deviceId);
		}
		if(!Constants.POM_NAME.equals(pomFile)) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomFile);
		}
		System.out.println("Functional test Command " + sb.toString());
		boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir, baseDir, FUNCTIONAL);
		if(!status) {
			throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
		}
	}
}
