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

	private static final String TEST = "test";

	public void unitTest(Configuration config) throws PhrescoException {
		Map<String, String> configs = MojoUtil.getAllValues(config);
		String sdk = configs.get(SDK);
		String target = configs.get(TARGET);
		boolean unitTest = Boolean.valueOf(configs.get(UNIT_TEST));
		// get command from plugin info
		String unitTestType = configs.get(UNIT_TEST_TYPE);
		String projectType = configs.get(PROJECT_TYPE);
		
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
		sb.append(XCODE_BUILD_COMMAND);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + SDK + EQUAL + sdk);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + TARGET_NAME + EQUAL + target);
		
		sb.append(STR_SPACE);
		sb.append(HYPHEN_D + UNIT_TEST + EQUAL + unitTest);
		
		if (StringUtils.isNotEmpty(projectType)) {
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + PROJECT_TYPE + EQUAL + projectType);
		}
		
		List<Parameter> parameters = config.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (UNIT_TEST_TYPE.equals(parameter.getKey())) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if (mavenCommand.getKey().equals(unitTestType)) {
						sb.append(STR_SPACE);
						sb.append(mavenCommand.getValue());
					}
				}
			}
		}
		
		sb.append(STR_SPACE);
		sb.append(TEST);
		System.out.println("UnitTest Command " + sb.toString());
		Utility.executeStreamconsumer(sb.toString());
	}
}
