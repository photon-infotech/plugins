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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.perforce.p4java.Log;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.service.pom.POMConstants;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class RunAndroidTest implements PluginConstants {
	static String currentWorkingDir;
	static String dotPhrescoDirName;
	static File baseDir ;
	static String testDirName;
	public static void runAndroidTest(Configuration configuration, MavenProjectInfo mavenProjectInfo, String workingDir ,String fromTest) throws PhrescoException {
		try {	
			MavenProject project = mavenProjectInfo.getProject();
			baseDir = new File(mavenProjectInfo.getBaseDir().getPath());
			currentWorkingDir = baseDir.getPath();
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			
			dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		    testDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_TEST_DIR);
		    String seleniumToolType = project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_SELENIUM_TOOL);
			if(dotPhrescoDirName!=null && testDirName!=null){
		    	
		    	currentWorkingDir = baseDir.getParentFile().getPath() +File.separator + testDirName + workingDir;
		    	
		    }else{
		    	currentWorkingDir = baseDir.getPath()+ workingDir;
		    	
		    }
			
			// calabash Execution
			if(fromTest.equals("functional") && StringUtils.isNotEmpty((seleniumToolType)) && seleniumToolType.equals(CALABASH)) {
				StringBuilder builder = new StringBuilder();
				builder.append(CALABASH_ANDROID_COMMAND);
				builder.append(STR_SPACE);
				String directory = project.getBuild().getDirectory();
				builder.append(directory + File.separator + project.getArtifactId() + DOT + POMConstants.APK);	
				builder.append(STR_SPACE);
				builder.append("-f junit --out test-reports");
				Utility.executeStreamconsumer(builder.toString(), project.getBasedir() + File.separator + workingDir, project.getBasedir().getPath(), FUNCTIONAL);
				return;
			} else if(fromTest.equals("functional") && StringUtils.isNotEmpty((seleniumToolType)) && seleniumToolType.equals(APPIUM)) {
				Utility.executeStreamconsumer(TEST_COMMAND, currentWorkingDir, project.getBasedir().getPath(), FUNCTIONAL);
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_CLEAN);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_INSTALL);
			
		    String signing = configs.get(SIGNING);
			String deviceList = configs.get(DEVICES_LIST);
			
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + ANDROID_DEVICE + EQUAL + deviceList);
			
			Boolean isSigning = Boolean.valueOf(signing);
			System.out.println("isSigning . " + isSigning);
			//signing
			if (isSigning) {
				sb.append(STR_SPACE);
				sb.append(PSIGN);
			}
			
			File workingFile = new File(currentWorkingDir + File.separator + project.getFile().getName());
			Log.info("workingFile :"+workingFile);
		    if(workingFile.exists()) {
				sb.append(STR_SPACE);
				sb.append(Constants.HYPHEN_F);
				sb.append(STR_SPACE);
				sb.append(project.getFile().getName());
			}
			System.out.println("Command " + sb.toString());
			Commandline commandline = new Commandline(sb.toString());
			
			if (StringUtils.isNotEmpty(workingDir)) {
				commandline.setWorkingDirectory(currentWorkingDir);
			}
			Process pb = commandline.execute();
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				System.out.write(singleByte);
			}
		} catch (CommandLineException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} 
	}
	
	
}
