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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.PhrescoBasePlugin;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.model.Profile;

public class NodeJsPlugin extends PhrescoBasePlugin {

	public NodeJsPlugin(Log log) {
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
			pack.pack(configuration, mavenProjectInfo, log);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
		
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus startServer(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Start start = new Start();
		start.start(configuration, mavenProjectInfo, log);
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus stopServer(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Stop stop = new Stop();
		stop.stop(mavenProjectInfo, log);
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus validate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		try {
		File pomFile = mavenProjectInfo.getProject().getFile();
		Profile profile = null;
		MavenProject project = mavenProjectInfo.getProject();
		File workingDir = project.getBasedir();
		String subModule = "";
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			subModule = mavenProjectInfo.getModuleName();
			workingDir = new File(workingDir + File.separator
					+ subModule);
		}
		String dotPhrescoDirName = project.getProperties().getProperty(
				Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		File baseDir = project.getBasedir();
		File dotPhrescoDir = baseDir;
		if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
			dotPhrescoDir = new File(baseDir.getParent() + File.separator
					+ dotPhrescoDirName + File.separatorChar + subModule);
		}
		PluginUtils pluginUtils = new PluginUtils();
		ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
		String skipTest = configs.get(SKIP);
		String value = configs.get(SONAR);
		StringBuilder sb = new  StringBuilder();
		sb.append(TEST_COMMAND).
		append(STR_SPACE).
		append(SONARCOMMAND).
		append(STR_SPACE).
		append("-Dskip=").
		append(skipTest);
		List<Parameter> parameters = configuration.getParameters().getParameter();
		String branchName = PluginUtils.getSonarBranchName(parameters);
		if(StringUtils.isNotEmpty(branchName)) {
			sb.append(STR_SPACE).append("-Dsonar.branch=").append(branchName).append(appInfo.getId());
		}
			for (Parameter parameter : parameters) {
				if (parameter.getPluginParameter() != null
						&& parameter.getPluginParameter().equals(PLUGIN_PARAMETER)&& parameter.getMavenCommands() != null) {
					List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
					for (MavenCommand mavenCommand : mavenCommands) {
						if (parameter.getValue().equals(mavenCommand.getKey())) {
							sb.append(STR_SPACE);
							sb.append(mavenCommand.getValue());
						}
					}
				} 
			}

		sb = sb.append(mavenProjectInfo.getSonarParams().toString());
		if(value.equals(FUNCTIONAL)) {
			sb.delete(0, sb.length());
			workingDir = new File(workingDir + project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR));
			sb.append(SONAR_COMMAND).
			append(STR_SPACE).
			append("-Dsonar.branch=functional").append(appInfo.getId()).
			append(STR_SPACE).
			append(SKIP_TESTS);
			sb = sb.append(mavenProjectInfo.getSonarParams().toString());
		}
		boolean status = Utility.executeStreamconsumer(sb.toString(), workingDir.getPath(), project.getBasedir().getPath(), CODE_VALIDATE);
		if(!status) {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
		}
			} catch (MojoExecutionException e) {
				throw new PhrescoException(e);
			}
		return new DefaultExecutionStatus();
	}
}
