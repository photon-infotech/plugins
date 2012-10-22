package com.photon.phresco.plugins;

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Utility;

public class PhrescoBasePlugin implements PhrescoPlugin, PluginConstants {

	public Log log;

	public PhrescoBasePlugin(Log log) {
		this.log = log;
	}

	protected final Log getLog() {
		return log;
	}

	public void runUnitTest() throws PhrescoException {
		StringBuilder sb = new StringBuilder();
		sb.append(UNIT_TEST_COMMAND);
		Utility.executeStreamconsumer(sb.toString());
	}

	public void runFunctionalTest() throws PhrescoException {
		// TODO Auto-generated method stub

	}

	public void runPerformanceTest() throws PhrescoException {
		// TODO Auto-generated method stub

	}

	public void runLoadTest() throws PhrescoException {
		// TODO Auto-generated method stub

	}

	public void validate(Configuration configuration) throws PhrescoException {
		StringBuilder sb = new StringBuilder();
		sb.append(SONAR_COMMAND);
		Map<String, String> config = MojoUtil.getAllValues(configuration);
		String value = config.get(SONAR);
		String string = config.get(value);
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PLUGIN_PARAMETER)) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if (parameter.getValue().equals(value) || mavenCommand.getKey().equals(string)) {
						String mavenCommandValue = mavenCommand.getValue();
						sb.append(STR_SPACE);
						sb.append(mavenCommandValue);
					}
				}
			}
		}
		Utility.executeStreamconsumer(sb.toString());

	}

	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub

	}

	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
	}

	public void startServer(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
	}

	public void stopServer(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
	}
}
