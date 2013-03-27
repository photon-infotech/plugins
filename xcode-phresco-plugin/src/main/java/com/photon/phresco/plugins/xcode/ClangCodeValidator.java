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

import java.io.*;
import java.util.*;

import org.apache.commons.lang.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.*;
import com.photon.phresco.util.*;

public class ClangCodeValidator implements PluginConstants {
	
	private Log log;
	/**
	 * Execute the xcode command line utility for iphone code validation.
	 * @throws PhrescoException 
	 */
	public void validate(Configuration config, MavenProjectInfo mavenProjectInfo, final Log log) throws PhrescoException {
		this.log = log;
		log.debug("Iphone code validation started ");
		Map<String, String> configs = MojoUtil.getAllValues(config);
		
		String target = configs.get(TARGET);
		if (StringUtils.isNotEmpty(target)) {
			target = target.replace(STR_SPACE, SHELL_SPACE);
		}
		String projectType = configs.get(PROJECT_TYPE);
		String sdk = configs.get(SDK);
		
		File baseDir = mavenProjectInfo.getBaseDir();
		if (StringUtils.isEmpty(target)) {
			System.out.println("Target is empty for code validation . ");
			throw new PhrescoException("Target is empty for code validation .");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(XCODE_CODE_VALIDATOR_COMMAND);
		
		sb.append(STR_SPACE);
//			sb.append("-DprojectType=" + projectType);
		sb.append(HYPHEN_D + PROJECT_TYPE + EQUAL + projectType);
		
		sb.append(STR_SPACE);
//			sb.append("-Dscheme=" + target);
		sb.append(HYPHEN_D + SCHEME + EQUAL + target);
		
		if (StringUtils.isNotEmpty(sdk)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + SDK + EQUAL + sdk);
		}
		
		log.debug("Command " + sb.toString());
		boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir.getPath());
		if(!status) {
			try {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
			} catch (MojoExecutionException e) {
				throw new PhrescoException(e);
			}
		}
	}
}
