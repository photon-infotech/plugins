/**
 * sitecore-phresco-plugin
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
package com.photon.phresco.plugins.sitecore;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoBasePlugin;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class SiteCorePlugin extends PhrescoBasePlugin {

	public SiteCorePlugin(Log log) {
		super(log);
	}
	
	@Override
	public ExecutionStatus pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			File targetDir = new File(mavenProjectInfo.getBaseDir() + DO_NOT_CHECKIN_FOLDER + File.separator + TARGET);
			if (targetDir.exists()) {
				FileUtils.deleteDirectory(targetDir);
				log.info("Target Folder Deleted Successfully");
			}
			writePhrescoBuildXml(configuration, mavenProjectInfo);
			Package pack = new Package();
			pack.pack(configuration, mavenProjectInfo, getLog());
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException { 
		Deploy deploy = new Deploy();
		deploy.deploy(configuration, mavenProjectInfo, getLog());
		return new DefaultExecutionStatus();
	}
}
