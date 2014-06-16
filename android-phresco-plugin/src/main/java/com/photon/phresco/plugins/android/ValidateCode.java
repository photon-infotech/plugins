package com.photon.phresco.plugins.android;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Element;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Profile;
import com.phresco.pom.util.PomProcessor;

public class ValidateCode implements PluginConstants {

	public ExecutionStatus validate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		File pomFile = mavenProjectInfo.getProject().getFile();
		String subModule = "";
		Profile profile = null;
		try {
		MavenProject project = mavenProjectInfo.getProject();
		File workingDir = project.getBasedir();
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			subModule = mavenProjectInfo.getModuleName();
			workingDir = new File(workingDir + File.separator
					+ subModule);
		}
		String dotPhrescoDirName = project.getProperties().getProperty(
				Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		File baseDir = project.getBasedir();
		File dotPhrescoDir = baseDir;
		if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
			dotPhrescoDir = new File(baseDir.getParent() + File.separator
					+ dotPhrescoDirName + File.separatorChar + subModule);
		}
		PluginUtils pluginUtils = new PluginUtils();
		ApplicationInfo appInfo = pluginUtils.getAppInfo(dotPhrescoDir);
		StringBuilder sb = new StringBuilder();
		sb.append(MVN_CMD);
		sb.append(STR_SPACE);
		sb.append(SONARCOMMAND);
		sb = sb.append(mavenProjectInfo.getSonarParams().toString());
		Map<String, String> config = MojoUtil.getAllValues(configuration);
		String projectModule = config.get(PROJECT_MODULE);
		if (StringUtils.isNotEmpty(projectModule)) {
            workingDir = new File(workingDir + File.separator + projectModule);
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
						PomProcessor pomPro = new PomProcessor(pomFile);
						profile = pomPro.getProfile(mavenCommand.getKey());
						if(profile != null) {
						List<Element> properties = profile.getProperties().getAny();
						for (Element element : properties) {
							if( element.getTagName().equalsIgnoreCase("sonar.branch")) {
								element.setTextContent(mavenCommand.getKey() + appInfo.getId());
							}
						}
						pomPro.save();
						}
					} 
				}
			}
		}
		if(profile == null) {
			sb.append(STR_SPACE).append("-Dsonar.branch=").append(value).append(appInfo.getId());
		}
		if(value.equals(FUNCTIONAL)) {
			sb.delete(0, sb.length());
			workingDir = new File(workingDir + project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR));
			sb.append(SONAR_COMMAND).
			append(STR_SPACE).
			append(SKIP_TESTS).
			append(STR_SPACE).
			append("-Dsonar.branch=functional").append(appInfo.getId());
			sb = sb.append(mavenProjectInfo.getSonarParams().toString());
		}
		File workingFile = new File(workingDir + File.separator + pomFile);
		if(workingFile.exists()) {
			sb.append(STR_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(STR_SPACE);
			sb.append(pomFile);
		}
		System.out.println("Sonar command :"+sb.toString());
		boolean status = Utility.executeStreamconsumer(sb.toString(), workingDir.getPath(), project.getBasedir().getPath(), CODE_VALIDATE);
		if(!status) {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
		}
			} catch (MojoExecutionException e) {
				throw new PhrescoException(e);
			} catch (PhrescoPomException e) {
				throw new PhrescoException(e);
			}
		return new DefaultExecutionStatus();
	}
}
