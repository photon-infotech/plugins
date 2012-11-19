package com.photon.phresco.plugins.xcode;

import java.util.*;

import org.apache.commons.lang.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.*;
import com.photon.phresco.plugins.util.*;
import com.photon.phresco.util.*;

public class FunctionalTest implements PluginConstants {
	public void functionalTest(Configuration config) throws PhrescoException {
		Map<String, String> configs = MojoUtil.getAllValues(config);
		String buildNumber = configs.get("buildNumber");
		
		if (StringUtils.isEmpty(buildNumber)) {
			System.out.println("Build Number is empty . ");
			throw new PhrescoException("Build Number is empty . ");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("mvn xcode:instruments");
		
		sb.append(STR_SPACE);
		sb.append("-DbuildNumber=" + buildNumber);
		
		System.out.println("Functional test Command " + sb.toString());
		Utility.executeStreamconsumer(sb.toString());
	}
}
