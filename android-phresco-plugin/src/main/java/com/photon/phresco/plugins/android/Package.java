package com.photon.phresco.plugins.android;

import java.util.*;

import org.apache.commons.lang.*;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.*;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class Package implements PluginConstants {

	public void pack(Configuration config, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		Map<String, String> configs = MojoUtil.getAllValues(config);
		String environmentName = configs.get(ENVIRONMENT_NAME);
		String buildName = configs.get(BUILD_NAME);
		String buildNumber = configs.get(BUILD_NUMBER);
		String sdkVersion = configs.get(SDK_VERSION);
		String proguard = configs.get(PROGUARD);
		String signing = configs.get(SIGNING);
		String skipTest = configs.get(SKIP_TEST);
		
		if (StringUtils.isEmpty(environmentName)) {
			System.out.println("Environment Name is empty . ");
			throw new PhrescoException("Environment Name is empty . ");
		}
		
		if (StringUtils.isEmpty(sdkVersion)) {
			System.out.println("sdkVersion is empty . ");
			throw new PhrescoException("sdkVersion is empty . ");
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
//		sb.append(STR_SPACE);
//		sb.append("-Psign");
		
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
		Utility.executeStreamconsumer(sb.toString());
	}
}
