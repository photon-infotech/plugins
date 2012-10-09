package com.photon.phresco.plugins.android;

import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class Deploy implements PluginConstants {

	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) {
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String buildName = configs.get(BUILD_NAME);
		String usb = "usb";
		String avd = "default";
		log.info("Project is Deploying...");
		StringBuilder builder = new StringBuilder();
		builder.append("mvn android:deploy -DskipTests=false -DbuildName="+buildName+" -Dandroid.device="+usb+" -Dandroid.emulator.avd="+avd+"");
		Utility.executeStreamconsumer(builder.toString());
	}
}
