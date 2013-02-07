package com.photon.phresco.plugins.java;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class JavaTest implements PluginConstants {
	private File baseDir;

	public void runTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException{
		baseDir = mavenProjectInfo.getBaseDir();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String testAgainst = configs.get(TEST_AGAINST);
		System.out.println("Test Against = " + testAgainst);
		buildCommand(configuration, testAgainst);
	}

	private void buildCommand(Configuration configuration, String testAgainst) throws PhrescoException {
		String mavenCommandValue = null;
		if (testAgainst != null) {
			List<Parameter> parameters = configuration.getParameters().getParameter();
			for (Parameter parameter : parameters) {
				if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PLUGIN_PARAMETER)) {
					List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
					for (MavenCommand mavenCommand : mavenCommands) {
						if (parameter.getValue().equals(testAgainst) && mavenCommand.getKey().equals(testAgainst)) {
							mavenCommandValue = mavenCommand.getValue();
						}
					}
				}
			}
		}
		executeTest(mavenCommandValue);
	}

	private void executeTest(String testAgainst) throws PhrescoException {
		System.out.println("-----------------------------------------");
		System.out.println("T E S T S");
		System.out.println("-----------------------------------------");
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(UNITTEST_COMMAND).append(STR_SPACE).
			append(testAgainst);
			boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir.getPath());
			if(!status) {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			throw new  PhrescoException(e);
		}
	}

}
