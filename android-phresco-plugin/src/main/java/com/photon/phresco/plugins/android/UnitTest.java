package com.photon.phresco.plugins.android;

import java.util.List;
import java.util.Map;

import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class UnitTest {
	
	public void unitTest(Configuration configuration) {
		StringBuilder sb = new StringBuilder();
		sb.append("mvn install");
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String deviceValue = configs.get("devices");
		String otherDiviceValue = configs.get(deviceValue);
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PluginConstants.PLUGIN_PARAMETER)) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if(mavenCommand.getKey().equalsIgnoreCase(deviceValue)) {
						sb.append(PluginConstants.STR_SPACE);
						sb.append(mavenCommand.getValue());
					} 
				}
			}
			if(parameter.getKey().equalsIgnoreCase(deviceValue)) {
				sb.append(PluginConstants.STR_SPACE);
				sb.append("-Dandroid.divice="+ otherDiviceValue +"");
			}
		}
		System.out.println("Final UnitTests Command ============> " + sb.toString());
		Utility.executeStreamconsumer(sb.toString());
	}

}
