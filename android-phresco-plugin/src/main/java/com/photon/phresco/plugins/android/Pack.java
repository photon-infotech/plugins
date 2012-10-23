package com.photon.phresco.plugins.android;

import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class Pack implements PluginConstants {

	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) {
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String environmentName = configs.get(ENVIRONMENT_NAME);
		String buildName = configs.get(BUILD_NAME);
		String vertion = configs.get(ANDROID_VERSION);
		String proguardSkip = configs.get(PROGUARD_SKIP);
		log.info("Project is Building...");
		StringBuilder builder = new StringBuilder();
		builder.append("mvn install  -DskipTests=false -Dandroid.version="+ vertion +" -DbuildName="+buildName+" -DenvironmentName="+ environmentName +" -Dandroid.proguard.skip="+ proguardSkip);
		Utility.executeStreamconsumer(builder.toString());
	}
}
