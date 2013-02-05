package com.photon.phresco.plugins.android;

import java.util.*;

import org.apache.commons.lang.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.*;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class Deploy implements PluginConstants {

	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException, MojoExecutionException  {
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String buildNumber = configs.get(BUILD_NUMBER);
		String deviceValue = configs.get(DEVICES);
		String serialNumber = configs.get(SERIAL_NUMBER);
		String workingDir = mavenProjectInfo.getBaseDir().getPath();
		
		if (StringUtils.isEmpty(buildNumber)) {
			System.out.println("buildNumber is empty . ");
			throw new PhrescoException("buildNumber is empty . ");
		}
		
		if (StringUtils.isEmpty(deviceValue)) {
			System.out.println("devices is empty . ");
			throw new PhrescoException("devices is empty . ");
		}
		
		log.info("Project is Deploying...");
		StringBuilder sb = new StringBuilder();
		sb.append(ANDROID_DEPLOY_COMMAND);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + BUILD_NUMBER + EQUAL + buildNumber);
		
		String otherDiviceValue = configs.get(deviceValue);
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PluginConstants.PLUGIN_PARAMETER)) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if(mavenCommand.getKey().equalsIgnoreCase(deviceValue)) {
						sb.append(STR_SPACE);
						sb.append(mavenCommand.getValue());
					} 
				}
			}
			if(parameter.getKey().equalsIgnoreCase(deviceValue)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + ANDROID_DEVICE + EQUAL + otherDiviceValue);
			}
		}
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + ANDROID_EMULATOR + EQUAL + DEFAULT);
		
		log.info("Project is Deploying...");
		log.info("Command " + sb.toString());
		boolean status = Utility.executeStreamconsumer(sb.toString(), workingDir);
		if(!status) {
			throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
		}
	}
}
