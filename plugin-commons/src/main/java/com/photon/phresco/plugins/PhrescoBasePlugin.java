package com.photon.phresco.plugins;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;

public class PhrescoBasePlugin implements PhrescoPlugin, PluginConstants {

	public Log log;

	public PhrescoBasePlugin(Log log) {
		this.log = log;
	}

	protected final Log getLog() {
		return log;
	}

	public void runUnitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		generateMavenCommand(mavenProjectInfo, Constants.POM_PROP_KEY_UNITTEST_DIR);
	}

	public void runFunctionalTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		generateMavenCommand(mavenProjectInfo, Constants.POM_PROP_KEY_FUNCTEST_DIR);
	}

	public void runPerformanceTest() throws PhrescoException {
		// TODO Auto-generated method stub

	}

	public void runLoadTest() throws PhrescoException {
		// TODO Auto-generated method stub

	}

	public void validate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(SONAR_COMMAND);
			Map<String, String> config = MojoUtil.getAllValues(configuration);
			MavenProject project = mavenProjectInfo.getProject();
			String baseDir = project.getBasedir().getPath();
			Commandline commandline = null;
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
							commandline = new Commandline(sb.toString());
						}
					}
				}
			}
			if(value.equals("functional")) {
				String workingDir = project.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
				sb.append("-Dsonar.branch=functional");
				commandline = new Commandline(sb.toString());
				if (StringUtils.isNotEmpty(workingDir)) {
					commandline.setWorkingDirectory(baseDir + workingDir);
		        }
			}
			commandline.execute();
		} catch (CommandLineException e) {
			throw new PhrescoException(e);
		}

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
	
	public void performCIPreBuildStep(String jobName, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("Just root method call ... ");
		// TODO Auto-generated method stub
	}
	
	private void generateMavenCommand(MavenProjectInfo mavenProjectInfo, String propertyTagName) throws PhrescoException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(TEST_COMMAND);
			MavenProject project = mavenProjectInfo.getProject();
			String baseDir = project.getBasedir().getPath();
			String workingDirectory = project.getProperties().getProperty(propertyTagName);
			Commandline cl = new Commandline(sb.toString());
			
			if (StringUtils.isNotEmpty(workingDirectory)) {
	            cl.setWorkingDirectory(baseDir + workingDirectory);
	        }
				cl.execute();
				
			} catch (CommandLineException e) {
				throw new PhrescoException(e);
			}
	}
}
