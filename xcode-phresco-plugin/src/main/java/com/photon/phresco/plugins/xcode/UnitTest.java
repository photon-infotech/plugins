package com.photon.phresco.plugins.xcode;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class UnitTest implements PluginConstants {

	public void unitTest(Configuration config) throws PhrescoException {
		Map<String, String> configs = MojoUtil.getAllValues(config);
		String sdk = configs.get("sdk");
		String target = configs.get("target");
		boolean unitTest = Boolean.valueOf(configs.get("unittest"));
		// get command from plugin info
		String unitTestType = configs.get("unitTestType");
		String projectType = configs.get("projectType");
		
		if (StringUtils.isEmpty(sdk)) {
			System.out.println("SDK is empty . ");
			throw new PhrescoException("SDK is empty . ");
		}
		
		if (StringUtils.isEmpty(target)) {
			System.out.println("target is empty . ");
			throw new PhrescoException("target is empty . ");
		}
		
		if (StringUtils.isEmpty(unitTestType)) {
			System.out.println("unitTestType is empty . ");
			throw new PhrescoException("unitTestType is empty . ");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("mvn xcode:xcodebuild");
		
		sb.append(STR_SPACE);
		sb.append("-Dsdk=" + sdk);
		
		sb.append(STR_SPACE);
		sb.append("-DtargetName=" + target);
		
		sb.append(STR_SPACE);
		sb.append("-Dunittest=" + unitTest);
		
		if (StringUtils.isNotEmpty(projectType)) {
			sb.append(STR_SPACE);
			sb.append("-DprojectType=" + projectType);
		}
		
		List<Parameter> parameters = config.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if ("unitTestType".equals(parameter.getKey())) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if (mavenCommand.getKey().equals(unitTestType)) {
						sb.append(STR_SPACE);
						sb.append(mavenCommand.getValue());
					}
				}
			}
		}
		System.out.println("UnitTest Command " + sb.toString());
		Utility.executeStreamconsumer(sb.toString());
	}
}
