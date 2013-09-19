package com.photon.phresco.plugins.android;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class ValidateCode implements PluginConstants {

	public ExecutionStatus validate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		String pomFile = mavenProjectInfo.getProject().getFile().getName();
		StringBuilder sb = new StringBuilder();
		sb.append(MVN_CMD);
		sb.append(STR_SPACE);
		sb.append(SONARCOMMAND);
		Map<String, String> config = MojoUtil.getAllValues(configuration);
		String projectModule = config.get(PROJECT_MODULE);
		MavenProject project = mavenProjectInfo.getProject();
		String workingDir = project.getBasedir().getPath();
		if (StringUtils.isNotEmpty(projectModule)) {
            workingDir = workingDir + File.separator + projectModule;
        }
		String value = config.get(SONAR);
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PLUGIN_PARAMETER) && parameter.getMavenCommands() != null) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if (parameter.getValue().equals(mavenCommand.getKey())) {
						sb.append(STR_SPACE);
						sb.append(mavenCommand.getValue());
					} 
				}
			}
		}
		if(value.equals(FUNCTIONAL)) {
			sb.delete(0, sb.length());
			workingDir = workingDir + project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
			sb.append(SONAR_COMMAND).
			append(STR_SPACE).
			append(SKIP_TESTS).
			append(STR_SPACE).
			append("-Dsonar.branch=functional");
		}
		File workingFile = new File(workingDir + File.separator + pomFile);
		if(workingFile.exists()) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomFile);
		}
		boolean status = Utility.executeStreamconsumer(sb.toString(), workingDir, project.getBasedir().getPath(), CODE_VALIDATE);
		if(!status) {
			try {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
			} catch (MojoExecutionException e) {
				throw new PhrescoException(e);
			}
		}
		return new DefaultExecutionStatus();
	}
}
