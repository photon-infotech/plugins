/**
 * android-phresco-plugin
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
package com.photon.phresco.plugins.android;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.commons.jxpath.ri.parser.ParseException;
import org.apache.commons.lang.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.*;
import org.apache.maven.project.*;
import org.w3c.dom.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.*;
import com.photon.phresco.util.*;
import com.phresco.pom.android.*;
import com.phresco.pom.model.*;
import com.phresco.pom.model.PluginExecution.Goals;
import com.phresco.pom.util.*;

public class Package implements PluginConstants {
	private Log log;
	private String baseDir;
	private MavenProject project;
	public void pack(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration config, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir().getPath();
		project = mavenProjectInfo.getProject();
		
		Map<String, String> configs = MojoUtil.getAllValues(config);
		String environmentName = configs.get(ENVIRONMENT_NAME);
		String buildName = configs.get(BUILD_NAME);
		String buildNumber = configs.get(BUILD_NUMBER);
		String sdkVersion = configs.get(SDK_VERSION);
		String proguard = configs.get(PROGUARD);
		String skipTest = configs.get(SKIP_TEST);
		
		String signing = configs.get(SIGNING);
		String keystore = configs.get(KEYSTORE);
		String storepass = configs.get(STOREPASS);
		String keypass = configs.get(KEYPASS);
		String alias = configs.get(ALIAS);
		if (StringUtils.isEmpty(environmentName)) {
			System.out.println("Environment Name is empty . ");
			throw new PhrescoException("Environment Name is empty . ");
		}
		
		if (StringUtils.isEmpty(sdkVersion)) {
			System.out.println("sdkVersion is empty . ");
			throw new PhrescoException("sdkVersion is empty . ");
		}
		
		PluginUtils.checkForConfigurations(new File(baseDir), environmentName);
		
		Boolean isSigning = Boolean.valueOf(signing);
		log.info("isSigning . " + isSigning);
		if (isSigning) {
			updateAllPOMWithProfile(keystore, storepass, keypass, alias);
		}
		
		log.info("Project is Building...");
		StringBuilder sb = new StringBuilder();
		sb.append(ANDROID_BUILD_COMMAND);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + ANDROID_VERSION + EQUAL + sdkVersion);
		
		if (StringUtils.isNotEmpty(buildName)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + BUILD_NAME + EQUAL + buildName);
		}
		
		if (StringUtils.isNotEmpty(buildNumber)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + BUILD_NUMBER + EQUAL + buildNumber);
		}
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + ENVIRONMENT_NAME + EQUAL + environmentName);
		
		if (StringUtils.isNotEmpty(proguard)) {
			sb.append(STR_SPACE);
			boolean proguradVal = Boolean.valueOf(proguard);
			sb.append(HYPHEN_D + PROGUARD_SKIP + EQUAL + !proguradVal);
		}
		
		//signing
		if (isSigning) {
			sb.append(STR_SPACE);
			sb.append(PSIGN);
		}
		
		//skipTest impl
		List<Parameter> parameters = config.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (SKIP_TEST.equals(parameter.getKey())) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if (mavenCommand.getKey().equals(skipTest)) {
						sb.append(STR_SPACE);
						sb.append(mavenCommand.getValue());
					}
				}
			}
		}
		
		log.info("Command " + sb.toString());
		boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir);
		if(!status) {
			try {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
			} catch (MojoExecutionException e) {
				throw new PhrescoException(e);
			}
		}
	}
	
	private void updateAllPOMWithProfile(String keystore, String storepass, String keypass, String alias) throws PhrescoException {
		log.info("baseDir " + baseDir);
		List<String> pomsTobeUpdated = new ArrayList<String>();
		pomsTobeUpdated.add("");
		pomsTobeUpdated.add(Constants.POM_PROP_KEY_UNITTEST_DIR);
		pomsTobeUpdated.add(Constants.POM_PROP_KEY_FUNCTEST_DIR);
		pomsTobeUpdated.add(Constants.POM_PROP_KEY_PERFORMANCETEST_DIR);
		
		List<String> projPoms = new ArrayList<String>(pomsTobeUpdated.size());
		for (String pomTobeUpdated : pomsTobeUpdated) {
			// get root dir
			StringBuilder sb = new StringBuilder(baseDir);
			// get pom path
			String property = project.getProperties().getProperty(pomTobeUpdated);
			if (StringUtils.isNotEmpty(property)) {
				sb.append(property);
			}
			// pom file
			sb.append(File.separatorChar);
			sb.append(POM_XML);
			projPoms.add(sb.toString());
		}
		
		for (String projPom : projPoms) {
			log.info("pom updating for " + projPom);
			createAndroidProfile(projPom, keystore, storepass, keypass, alias);
		}
	}
	
	private String createAndroidProfile(String filePath, String keystore, String storepass, String keypass, String alias) throws PhrescoException {
		log.info("Entering Method Build.createAndroidProfile() " + filePath);
		try {
			if (StringUtils.isEmpty(keystore)) {
				throw new PhrescoException("keystore value is empty "); 
			}
			
			if (StringUtils.isEmpty(storepass)) {
				throw new PhrescoException("storepass value is empty "); 
			}
			
			if (StringUtils.isEmpty(keypass)) {
				throw new PhrescoException("keypass value is empty "); 
			}
			
			if (StringUtils.isEmpty(alias)) {
				throw new PhrescoException("alias value is empty "); 
			}
			
			boolean hasSigning = false;

			File pomPath = new File(filePath);

			AndroidPomProcessor processor = new AndroidPomProcessor(pomPath);
			hasSigning = processor.hasSigning();
			String profileId = PROFILE_ID;
			String defaultGoal = GOAL_INSTALL;
			Plugin plugin = new Plugin();
			plugin.setGroupId(ANDROID_PROFILE_GROUP_ID);
			plugin.setArtifactId(ANDROID_PROFILE_ARTIFACT_ID);
			plugin.setVersion(ANDROID_PROFILE_VERSION);

			PluginExecution execution = new PluginExecution();
			execution.setId(ANDROID_EXECUTION_ID);
			Goals goal = new Goals();
			goal.getGoal().add(GOAL_SIGN);
			execution.setGoals(goal);
			execution.setPhase(PHASE_PACKAGE);
			execution.setInherited(TRUE);

			AndroidProfile androidProfile = new AndroidProfile();
			androidProfile.setKeystore(keystore);
			androidProfile.setStorepass(storepass);
			androidProfile.setKeypass(keypass);
			androidProfile.setAlias(alias);
			androidProfile.setVerbose(true);
			androidProfile.setVerify(true);

			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			List<Element> executionConfig = new ArrayList<Element>();
			executionConfig.add(doc.createElement(ELEMENT_ARCHIVE_DIR));
			Element removeExistSignature = doc.createElement(ELEMENT_REMOVE_EXIST_SIGN);
			Element includeElement = doc.createElement(ELEMENT_INCLUDES);
			Element doNotCheckInBuildInclude = doc.createElement(ELEMENT_INCLUDE);
			doNotCheckInBuildInclude.setTextContent(ELEMENT_BUILD);
			Element doNotCheckinTargetInclude = doc.createElement(ELEMENT_INCLUDE);
			doNotCheckinTargetInclude.setTextContent(ELEMENT_TARGET);
			includeElement.appendChild(doNotCheckInBuildInclude);
			includeElement.appendChild(doNotCheckinTargetInclude);
			executionConfig.add(includeElement);
			removeExistSignature.setTextContent(TRUE);
			executionConfig.add(removeExistSignature);

			// verboss
			Element verbos = doc.createElement(ELEMENT_VERBOS);
			verbos.setTextContent(TRUE);
			executionConfig.add(verbos);
			// verify
			Element verify = doc.createElement(ELEMENT_VERIFY);
			verbos.setTextContent(TRUE);
			executionConfig.add(verify);
			
			//arguments
			List<String> argumentContents = new ArrayList<String>();
			argumentContents.add(HYPHEN_SIGALG);
			argumentContents.add(MD5_WITH_RSA);
			argumentContents.add(HYPHEN_DIGESTALG);
			argumentContents.add("SHA1");
			Element arguments = doc.createElement(ARGUMENTS);
			for (String string : argumentContents) {
				Element argument = doc.createElement(ARGUMENT);
				argument.setTextContent(string);
				arguments.appendChild(argument);
			}
			executionConfig.add(arguments);
			
			com.phresco.pom.model.PluginExecution.Configuration configValues = new com.phresco.pom.model.PluginExecution.Configuration();
			configValues.getAny().addAll(executionConfig);
			execution.setConfiguration(configValues);
			List<Element> additionalConfigs = new ArrayList<Element>();
			processor.setProfile(profileId, defaultGoal, plugin, androidProfile, execution, null, additionalConfigs);
			processor.save();
			if (hasSigning) {
				log.info("Signing info updated successfully ");
			} else {
				log.info("Signing info created successfully ");
			}
		} catch (Exception e) {
			throw new PhrescoException(e); 
		}
		return SUCCESS;
	}
}
