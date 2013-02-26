package com.photon.phresco.plugins.xcode;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class FunctionalTest implements PluginConstants {
	public void functionalTest(Configuration config, MavenProjectInfo mavenProjectInfo) throws PhrescoException, MojoExecutionException {
		Map<String, String> configs = MojoUtil.getAllValues(config);
		String buildNumber = configs.get(BUILD_NUMBER);
		String deviceId = configs.get(DEVICE_ID);
		String baseDir = mavenProjectInfo.getBaseDir().getPath();
		MavenProject project = mavenProjectInfo.getProject();
		String seleniumToolType = project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_SELENIUM_TOOL);
		String workingDir = project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
		// calabash Execution
		if(StringUtils.isNotEmpty((seleniumToolType)) && seleniumToolType.equals(CALABASH)) {
			StringBuilder builder = new StringBuilder();
			builder.append(CALABASH_IOS_COMMAND);
			Utility.executeStreamconsumer(builder.toString(), baseDir + File.separator + workingDir);
			return;
		}
		if (StringUtils.isEmpty(buildNumber)) {
			System.out.println("Build Number is empty . ");
			throw new PhrescoException("Build Number is empty . ");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(XCODE_FUNCTIONAL_COMMAND);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + BUILD_NUMBER + EQUAL + buildNumber);
		
		if (StringUtils.isNotEmpty(deviceId)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + DEVICE_ID + EQUAL + deviceId);
		}
		
		System.out.println("Functional test Command " + sb.toString());
		boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir);
		if(!status) {
			throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
		}
	}
}
