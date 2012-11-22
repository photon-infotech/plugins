package com.photon.phresco.plugins.android;

import java.util.Map;

import org.apache.commons.lang.*;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class Deploy implements PluginConstants {

	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException  {
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String buildNumber = configs.get(BUILD_NUMBER);
		String device = configs.get(DEVICES);
		String serialNumber = configs.get(SERIAL_NUMBER);

		if (StringUtils.isEmpty(buildNumber)) {
			System.out.println("buildNumber is empty . ");
			throw new PhrescoException("buildNumber is empty . ");
		}
		
		if (StringUtils.isEmpty(device)) {
			System.out.println("devices is empty . ");
			throw new PhrescoException("devices is empty . ");
		}
		
		log.info("Project is Deploying...");
		StringBuilder sb = new StringBuilder();
		sb.append(ANDROID_DEPLOY_COMMAND);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + BUILD_NUMBER + EQUAL + buildNumber);
		
		if (device.equals(SERIAL_NUMBER)) {
			device = serialNumber;
		}
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + ANDROID_DEVICE + EQUAL + device);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + ANDROID_EMULATOR + EQUAL + DEFAULT);
		
		log.info("Project is Deploying...");
		log.info("Command " + sb.toString());
		Utility.executeStreamconsumer(sb.toString());
	}
}
