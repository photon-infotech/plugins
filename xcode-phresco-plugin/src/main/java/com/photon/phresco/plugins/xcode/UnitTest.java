package com.photon.phresco.plugins.xcode;

import java.util.List;
import java.util.Map;

import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class UnitTest implements PluginConstants {

	public void unitTest(Configuration configuration) {
		StringBuilder sb = new StringBuilder();
		sb.append("mvn xcode:xcodebuild -DskipTests=false -Dunittest=true");
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String sdkValue = configs.get("sdk");
		String targetValue = configs.get("target");
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PluginConstants.PLUGIN_PARAMETER)) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if(mavenCommand.getKey().equalsIgnoreCase(sdkValue) || mavenCommand.getKey().equalsIgnoreCase(targetValue)) {
						sb.append(PluginConstants.STR_SPACE);
						sb.append(mavenCommand.getValue());
					} 
				}
			}
		}
		System.out.println("Final UnitTests Command ============> " + sb.toString());
		Utility.executeStreamconsumer(sb.toString());
	}
}
