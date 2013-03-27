/**
 * HybridAppLauncher-maven-plugin Maven Mojo
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
package com.photon.phresco.plugins;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.util.Constants;
import com.phresco.pom.util.PomProcessor;

/**
 * Goal which touches a timestamp file.
 *
 * @goal applaunch
 * 
 */
public class HybridAppLauncher extends AbstractMojo {
	
	protected MavenProject project;
	/**
	 * @parameter expression="${project.basedir}" required="true"
	 * @readonly
	 */
	protected File baseDir;
	

	public void execute() throws MojoExecutionException {
		hybridAppLauncherScripts();
	}

	private void hybridAppLauncherScripts() throws MojoExecutionException {
		try {
			File directory = new File(baseDir.getPath());
			File pom = new File(baseDir.getPath() + File.separator + PluginConstants.POM_XML);
			PomProcessor processor = new PomProcessor(pom);
			String packageName = processor.getProperty(PluginConstants.PHRESCO_TEST_PACKAGE);
			
			if(packageName !=null ){
			StringBuilder sb = new StringBuilder();
			sb.append(PluginConstants.ADB_SHELL_MONKEY);
			sb.append(Constants.STR_BLANK_SPACE);
			sb.append(packageName);
			sb.append(Constants.STR_BLANK_SPACE);
			sb.append(PluginConstants.V_1);
			Commandline c1 = new Commandline(sb.toString());
			c1.setWorkingDirectory(directory);
			Process pb = c1.execute();
			Thread.sleep(9000);
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				System.out.write(singleByte);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}




