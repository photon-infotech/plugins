package com.photon.phresco.plugins.java;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.TechnologyTypes;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Plugin;
import com.phresco.pom.model.PluginExecution;
import com.phresco.pom.util.PomProcessor;

public class JavaTest implements PluginConstants {
	private File baseDir;
	private File testConfigPath;
	
	public void runTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException{
		baseDir = mavenProjectInfo.getBaseDir();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		String testAgainst = configs.get(TEST_AGAINST);
		String environment = configs.get(ENVIRONMENT_NAME);
		String goalPackBeforeTest = "";
		if (testAgainst.equals(JS)) {
			PluginUtils pluginUtils = new PluginUtils();
			ApplicationInfo appInfo = pluginUtils.getAppInfo(mavenProjectInfo.getBaseDir());
			String techId = appInfo.getTechInfo().getId();
			copyUnitInfoFile(environment, techId);
			goalPackBeforeTest = getGoalPackBeforeTest(baseDir);
		}
		buildCommand(configuration, testAgainst, goalPackBeforeTest);
	}

	private void copyUnitInfoFile(String environment, String techId) throws PhrescoException {
		try {
			PomProcessor processor = new PomProcessor( new File(baseDir.getPath() + File.separator + POM_XML));
			String testSourcePath = processor.getProperty("phresco.env.test.config.xml");
			if (!techId.equals(TechnologyTypes.JAVA_STANDALONE) && !techId.equals(TechnologyTypes.JAVA_WEBSERVICE) ) {
				PluginUtils utils = new PluginUtils();
				testConfigPath = new File(baseDir + File.separator + testSourcePath);
				String fullPathNoEndSeparator = FilenameUtils.getFullPathNoEndSeparator(testConfigPath.getAbsolutePath());
				File fullPathNoEndSeparatorFile = new File(fullPathNoEndSeparator);
				fullPathNoEndSeparatorFile.mkdirs();
				utils.executeUtil(environment, baseDir.getPath(), testConfigPath);
			}
		} catch (PhrescoPomException e) {
			throw new  PhrescoException(e);
		} catch (PhrescoException e) {
			throw new  PhrescoException(e);
		} 
	}

	private void buildCommand(Configuration configuration, String testAgainst, String goalPackBeforeTest) throws PhrescoException {
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
		executeTest(mavenCommandValue, testAgainst, goalPackBeforeTest);
	}

	private void executeTest(String mavenCommandValue, String testAgainst, String goalPackBeforeTest) throws PhrescoException {
		System.out.println("-----------------------------------------");
		System.out.println("T E S T S");
		System.out.println("-----------------------------------------");
		try {
			StringBuilder sb = new StringBuilder();
			if(StringUtils.isNotEmpty(goalPackBeforeTest)) {
				sb.append("mvn jstest:pack-before-test");
			} else {
				sb.append(UNITTEST_COMMAND);
			}
			sb.append(STR_SPACE).
			append(mavenCommandValue);
			boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir.getPath(), baseDir.getPath(), UNIT);
			if(!status) {
				throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			throw new  PhrescoException(e);
		}
	}
	
	private String getGoalPackBeforeTest(File baseDir) throws PhrescoException {
		try {
			PomProcessor processor = new PomProcessor(new File(baseDir.getPath() + File.separator + POM_XML));
			Plugin plugin = processor.getPlugin("net.awired.jstest", "jstest-maven-plugin");
			if(plugin.getExecutions() != null && CollectionUtils.isNotEmpty(plugin.getExecutions().getExecution())) {
				List<PluginExecution> execution = plugin.getExecutions().getExecution();
				for (PluginExecution pluginExecution : execution) {
					if(pluginExecution.getGoals() != null && CollectionUtils.isNotEmpty(pluginExecution.getGoals().getGoal())) {
						List<String> goals = pluginExecution.getGoals().getGoal();
						for (String goal : goals) {
							if(PACK_BEFORE_TEST.equals(goal)) {
								return goal;
							}
						}
					}
				}
			}
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
		return "";
	}

}
