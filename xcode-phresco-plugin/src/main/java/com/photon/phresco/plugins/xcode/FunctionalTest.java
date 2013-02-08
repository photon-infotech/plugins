package com.photon.phresco.plugins.xcode;

import java.util.*;

import org.apache.commons.lang.*;
import org.apache.maven.plugin.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.*;
import com.photon.phresco.util.*;

public class FunctionalTest implements PluginConstants {
	public void functionalTest(Configuration config, MavenProjectInfo mavenProjectInfo) throws PhrescoException, MojoExecutionException {
		Map<String, String> configs = MojoUtil.getAllValues(config);
		String buildNumber = configs.get(BUILD_NUMBER);
		String deviceId = configs.get(DEVICE_ID);
		String workingDir = mavenProjectInfo.getBaseDir().getPath();
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
		boolean status = Utility.executeStreamconsumer(sb.toString(), workingDir);
		if(!status) {
			throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
		}
	}
}
