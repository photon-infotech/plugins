package com.photon.phresco.plugins.android;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;

public class UnitTest implements PluginConstants {
	
	public void unitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_INSTALL);
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String deviceValue = configs.get(DEVICES);
			String otherDiviceValue = configs.get(deviceValue);
			List<Parameter> parameters = configuration.getParameters().getParameter();
			for (Parameter parameter : parameters) {
				if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PluginConstants.PLUGIN_PARAMETER)) {
					List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
					for (MavenCommand mavenCommand : mavenCommands) {
						if(mavenCommand.getKey().equalsIgnoreCase(deviceValue)) {
							sb.append(STR_SPACE);
							sb.append(mavenCommand.getValue());
						} 
					}
				}
				if(parameter.getKey().equalsIgnoreCase(deviceValue)) {
					sb.append(STR_SPACE);
					sb.append("-Dandroid.divice="+ otherDiviceValue +"");
				}
			}
			sb.append("-Dandroid.emulator.avd=default");
			Commandline commandline = new Commandline(sb.toString());
			MavenProject project = mavenProjectInfo.getProject();
			String workingDir = project.getProperties().getProperty(FrameworkConstants.POM_PROP_KEY_UNITTEST_DIR);
			String baseDir = mavenProjectInfo.getBaseDir().getPath();
			if (StringUtils.isNotEmpty(workingDir)) {
				commandline.setWorkingDirectory(baseDir + workingDir);
			}
			commandline.execute();
		} catch (CommandLineException e) {
			throw new PhrescoException(e);
		}
	}
}
