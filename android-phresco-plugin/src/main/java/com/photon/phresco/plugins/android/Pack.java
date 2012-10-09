package com.photon.phresco.plugins.android;

import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class Pack implements PluginConstants {

	private String environmentName;
	private String vertion = "4.0.3";
	private String proguardSkip = "false";
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) {
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		log.info("Project is Building...");
		StringBuilder builder = new StringBuilder();
		builder.append("mvn install  -DskipTests=false -Dandroid.version="+ vertion +" -DenvironmentName="+ environmentName +" -Dandroid.proguard.skip="+ proguardSkip +" ");
		Utility.executeStreamconsumer(builder.toString());
	}
}
