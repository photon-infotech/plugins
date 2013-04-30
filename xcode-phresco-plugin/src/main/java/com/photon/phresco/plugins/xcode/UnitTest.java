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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class UnitTest implements PluginConstants {

	private static final String TEST = "test";

	public void unitTest(Configuration config, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Map<String, String> configs = MojoUtil.getAllValues(config);
		String sdk = configs.get(SDK);
		String target = configs.get(TARGET);
        if (StringUtils.isNotEmpty(target)) {
        	target = target.replace(STR_SPACE, SHELL_SPACE);
        }
		boolean unitTest = Boolean.valueOf(configs.get(UNIT_TEST));
		// get command from plugin info
		String unitTestType = configs.get(UNIT_TEST_TYPE);
		String projectType = configs.get(PROJECT_TYPE);
		String baseDir = mavenProjectInfo.getBaseDir().getPath();
		String pomFile = mavenProjectInfo.getProject().getFile().getName();
		StringBuilder sb = new StringBuilder();
		sb.append(XCODE_BUILD_COMMAND);
		
		if (StringUtils.isNotEmpty(projectType)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + PROJECT_TYPE + EQUAL + projectType);
		}
		
		if (StringUtils.isNotEmpty(sdk)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + SDK + EQUAL + sdk);
		}
		
		if (StringUtils.isNotEmpty(target)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + TARGET_NAME + EQUAL + target);
		}
		
		if (StringUtils.isNotEmpty(unitTestType)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + UNIT_TEST + EQUAL + unitTest);
		}
		
		List<Parameter> parameters = config.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (UNIT_TEST_TYPE.equals(parameter.getKey())) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if (mavenCommand.getKey().equals(unitTestType)) {
						sb.append(STR_SPACE);
						sb.append(mavenCommand.getValue());
					}
				}
			}
		}
		
		sb.append(STR_SPACE);
		sb.append(TEST);
		
		if(!Constants.POM_NAME.equals(pomFile)) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomFile);
		}
		
		System.out.println("UnitTest Command " + sb.toString());
		boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir, baseDir, UNIT);
		if(!status) {
			try {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
			} catch (MojoExecutionException e) {
				throw new PhrescoException(e);
			}
		}
	}
}
