package com.photon.phresco.plugins.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.model.SettingsInfo;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Dependency;
import com.phresco.pom.util.PomProcessor;

public class Deploy implements PluginConstants {
	
	private static final String CARGO_MAVEN2_PLUGIN = "cargo-maven2-plugin";
	private static final String CODEHAUS_CARGO_PLUGIN = "org.codehaus.cargo";
	private MavenProject project;
	private File baseDir;
	private String buildName;
	private String environmentName;
	private boolean importSql;
	private String projectCode;
	private File buildFile;
	private File tempDir;
	private String context;
	private Map<String, String> serverVersionMap = new HashMap<String, String>();
	private Log log;
	
	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		projectCode = mavenProjectInfo.getProjectCode();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        
		try {
			init();
			initMap();
			updateFinalName();
			createDb();
			extractBuild();
			deployToServer();
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		
	}
	
	private void init() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(buildName) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			File buildDir = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY);// build dir
			buildFile = new File(buildDir.getPath() + File.separator + buildName);// filename
			tempDir = new File(buildDir.getPath() + TEMP_DIR);// temp dir
			tempDir.mkdirs();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void initMap() {
		serverVersionMap.put("tomcat-7.0.x", "tomcat7x");
		serverVersionMap.put("tomcat-6.0.x", "tomcat6x");
		serverVersionMap.put("tomcat-5.5.x", "tomcat5x");
		serverVersionMap.put("jboss-7.1.x", "jboss71x");
		serverVersionMap.put("jboss-7.0.x", "jboss7x");
		serverVersionMap.put("jboss-6.1.x", "jboss61x");
		serverVersionMap.put("jboss-6.0.x", "jboss6x");
		serverVersionMap.put("jboss-5.1.x", "jboss51x");
		serverVersionMap.put("jboss-5.0.x", "jboss5x");
		serverVersionMap.put("jboss-4.2.x", "jboss42x");
		serverVersionMap.put("jboss-4.0.x", "jboss4x");
	}

	private void callUsage() throws MojoExecutionException {
		log.error("Invalid usage.");
		log.info("Usage of Deploy Goal");
		log.info(
				"mvn java:deploy -DbuildName=\"Name of the build\""
						+ " -DenvironmentName=\"Multivalued evnironment names\""
						+ " -DimportSql=\"Does the deployment needs to import sql(TRUE/FALSE?)\"");
		throw new MojoExecutionException("Invalid Usage. Please see the Usage of Deploy Goal");
	}
	
	private void updateFinalName() throws MojoExecutionException {
		try {
				ProjectAdministrator projAdmin = PhrescoFrameworkFactory.getProjectAdministrator();
				String envName = environmentName;
				if (environmentName.indexOf(',') > -1) {
					envName = projAdmin.getDefaultEnvName(projectCode);
				}
				List<SettingsInfo> settingsInfos = projAdmin.getSettingsInfos(Constants.SETTINGS_TEMPLATE_SERVER,
						projectCode, envName);
				for (SettingsInfo settingsInfo : settingsInfos) {
					context = settingsInfo.getPropertyInfo(Constants.SERVER_CONTEXT).getValue();
					break;
				}
			File pom = project.getFile();
			PomProcessor pomprocessor = new PomProcessor(pom);
			pomprocessor.setFinalName(context);
			pomprocessor.save();

		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void createDb() throws MojoExecutionException {
		PluginUtils util = new PluginUtils();
		try {
			if (importSql) {
				List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_DB);
				for (SettingsInfo databaseDetails : settingsInfos) {
					String databaseType = databaseDetails.getPropertyInfo(Constants.DB_TYPE).getValue();
//					util.getSqlFilePath(databaseDetails,baseDir, databaseType);
				}
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void extractBuild() throws MojoExecutionException {
		try {
			ArchiveUtil.extractArchive(buildFile.getPath(), tempDir.getPath(), ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void deployToServer() throws MojoExecutionException {
		try {
			List<SettingsInfo> settingsInfos  = getSettingsInfo(Constants.SETTINGS_TEMPLATE_SERVER);
			for (SettingsInfo serverDetails : settingsInfos) {
				deploy(serverDetails);
			}			
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}
	
	private void deploy(SettingsInfo info) throws MojoExecutionException, PhrescoException {
		if (info == null) {
			return;
		}
		String serverhost = info.getPropertyInfo(Constants.SERVER_HOST).getValue();
		String serverport = info.getPropertyInfo(Constants.SERVER_PORT).getValue();
		String serverprotocol = info.getPropertyInfo(Constants.SERVER_PROTOCOL).getValue();
		String serverusername = info.getPropertyInfo(Constants.SERVER_ADMIN_USERNAME).getValue();
		String serverpassword = info.getPropertyInfo(Constants.SERVER_ADMIN_PASSWORD).getValue();
		String version = info.getPropertyInfo(Constants.SERVER_VERSION).getValue();
		String servertype = info.getPropertyInfo(Constants.SERVER_TYPE).getValue();
		context = info.getPropertyInfo(Constants.SERVER_CONTEXT).getValue();
		String remotedeploy = info.getPropertyInfo(Constants.SERVER_REMOTE_DEPLOYMENT).getValue();
		
		String containerId = "";
		renameWar(context);
		
		// local deployment
		if (remotedeploy.equals("false")) {
			deployLocal();
			return;
		}

		// remote deployment
		if (servertype.contains(TYPE_TOMCAT)) {
			removeCargoDependency();
			containerId = serverVersionMap.get("tomcat-" + version);
			deployToServer(serverprotocol, serverhost, serverport, serverusername, serverpassword, containerId);
		} else if (servertype.contains(TYPE_JBOSS)) {
			addCargoDependency(version);
			containerId = serverVersionMap.get("jboss-" + version);
			deployToServer(serverprotocol, serverhost, serverport, serverusername, serverpassword, containerId);
		} else if (servertype.contains(TYPE_WEBLOGIC) && ((version.equals(Constants.WEBLOGIC_12c)) || (version.equals(Constants.WEBLOGIC_11gR1)))) {
			deployToWeblogicServer(serverprotocol, serverhost, serverport, serverusername, serverpassword, version);
		} 
	}

	private void addCargoDependency(String version) throws PhrescoException {
		try {
			PomProcessor processor = new PomProcessor(project.getFile());
			processor.deletePluginDependency(CODEHAUS_CARGO_PLUGIN, CARGO_MAVEN2_PLUGIN);
			
			//For Jboss4 dependency is not needed
			if (version.startsWith("5.") || version.startsWith("6.")) {
				addJBoss5xDependency(processor);
			} else if (version.startsWith("7.")) {
				addJBoss7xDependency(processor);
			}
			
			processor.save();
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void addJBoss7xDependency(PomProcessor processor) throws PhrescoPomException {
		Dependency dependency = new Dependency();
		dependency.setGroupId("org.jboss.as");
		dependency.setArtifactId("jboss-as-controller-client");
		dependency.setVersion("7.0.2.Final");
		
		processor.addPluginDependency(CODEHAUS_CARGO_PLUGIN, CARGO_MAVEN2_PLUGIN, dependency);	
	}

	private void addJBoss5xDependency(PomProcessor processor) throws PhrescoPomException {
		Dependency dependency = new Dependency();
		dependency.setGroupId("org.jboss.integration");
		dependency.setArtifactId("jboss-profileservice-spi");
		dependency.setVersion("5.1.0.GA");

		processor.addPluginDependency(CODEHAUS_CARGO_PLUGIN, CARGO_MAVEN2_PLUGIN, dependency);
		
		Dependency dependency2 = new Dependency();
		dependency2.setGroupId("org.jboss.jbossas");
		dependency2.setArtifactId("jboss-as-client");
		dependency2.setVersion("5.1.0.GA");
		dependency2.setType("pom");

		processor.addPluginDependency(CODEHAUS_CARGO_PLUGIN, CARGO_MAVEN2_PLUGIN, dependency2);
	}

	private void removeCargoDependency() throws PhrescoException {
		try {
			PomProcessor processor = new PomProcessor(project.getFile());
			processor.deletePluginDependency(CODEHAUS_CARGO_PLUGIN, CARGO_MAVEN2_PLUGIN);
			processor.save();
		} catch (JAXBException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}
	
	private void renameWar(String context) throws MojoExecutionException {
		String contextName = context + ".war";
		String warFileName = "";
		String[] list = tempDir.list(new JDWarFileNameFilter());
		if (list.length > 0) {
			warFileName = list[0];
			if (!warFileName.equals(contextName)) {
				File oldWar = new File(tempDir.getPath() + "/" + warFileName);
				File newWar = new File(tempDir.getPath() + "/" + contextName);
				oldWar.renameTo(newWar);
			}
		}
	}
	
	
	private void deployToServer(String serverprotocol, String serverhost, String serverport,
			String serverusername, String serverpassword, String containerId) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(JBOSS_GOAL);
			sb.append(STR_SPACE);
			sb.append("-Dserver.protocol=");
			sb.append(serverprotocol);
			sb.append(STR_SPACE);
			sb.append(SERVER_HOST);
			sb.append(serverhost);
			sb.append(STR_SPACE);
			sb.append(SERVER_PORT);
			sb.append(serverport);
			sb.append(STR_SPACE);
			sb.append(SERVER_USERNAME);
			sb.append(serverusername);
			sb.append(STR_SPACE);
			sb.append(SERVER_PASSWORD);
			sb.append(serverpassword);
			sb.append(STR_SPACE);
			sb.append("-Dcontainer.id=");
			sb.append(containerId);
			sb.append(STR_SPACE);
			sb.append("-Dserver.context=");
			sb.append(context);
			sb.append(STR_SPACE);
			sb.append(SKIP_TESTS);
			
			bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("[ERROR]")) {
					errorParam = true;
				}
			}
			if (errorParam) {
				throw new MojoExecutionException("Remote Deployment Failed ");
			} else {
				log.info(
						" Project is Deploying into " + serverprotocol + "://" + serverhost + ":" + serverport + "/"
								+ context);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}  finally {
			Utility.closeStream(bufferedReader);
		}
	}


	private void deployToWeblogicServer(String serverprotocol, String serverhost, String serverport, String serverusername,
			String serverpassword, String version) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			String webLogicVersion = "";
			if (version.equals(Constants.WEBLOGIC_12c)) {
				webLogicVersion = Constants.WEBLOGIC_12c_PLUGIN_VERSION;
			} else if (version.equals(Constants.WEBLOGIC_11gR1)) {
				webLogicVersion = Constants.WEBLOGIC_11gr1c_PLUGIN_VERSION;
			} 
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(WEBLOGIC_GOAL + webLogicVersion + WEBLOGIC_REDEPLOY);
			sb.append(STR_SPACE);
			sb.append(SERVER_HOST);
			sb.append(serverhost);
			sb.append(STR_SPACE);
			sb.append(SERVER_PORT);
			sb.append(serverport);
			sb.append(STR_SPACE);
			sb.append(SERVER_USERNAME);
			sb.append(serverusername);
			sb.append(STR_SPACE);
			sb.append(SERVER_PASSWORD);
			sb.append(serverpassword);
			sb.append(STR_SPACE);
			sb.append(SKIP_TESTS);
			
						
			 bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					if (line.startsWith("[ERROR]")) {
						errorParam = true;
					}
				}
				if (errorParam) {
					throw new MojoExecutionException("Remote Deploy Failed ");
				} else {
					log.info(
							" Project is Deploying into " + serverprotocol + "://" + serverhost + ":" + serverport + "/"
									+ context);
				}
			} catch (IOException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			} finally {
				Utility.closeStream(bufferedReader);
			}
	}
	
	private void deployLocal() throws MojoExecutionException {
		String deployLocation = "";
		try {
			List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_SERVER);
			for (SettingsInfo serverDetails : settingsInfos) {
				deployLocation = serverDetails.getPropertyInfo(Constants.SERVER_DEPLOY_DIR).getValue();
				break;
			}		
			File deployDir = new File(deployLocation);
			if (!deployDir.exists()) {
				throw new MojoExecutionException(" Deploy Directory" + deployLocation + " Does Not Exists ");
			}
			log.info("Project is deploying into " + deployLocation);
			FileUtils.copyDirectoryStructure(tempDir.getAbsoluteFile(), deployDir);
			log.info("Project is deployed successfully");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private File getProjectRoot(File childDir) {
		File[] listFiles = childDir.listFiles(new PhrescoDirFilter());
		if (listFiles != null && listFiles.length > 0) {
			return childDir;
		}
		if (childDir.getParentFile() != null) {
			return getProjectRoot(childDir.getParentFile()); 
		}
		return null;
	}
	
	public class PhrescoDirFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            return name.equals(DOT_PHRESCO_FOLDER);
        }
    }
	
//	private void deploy() throws MojoExecutionException {
//		String deployLocation = "";
//		try {
//			List<SettingsInfo> settingsInfos = getSettingsInfo(Constants.SETTINGS_TEMPLATE_SERVER);
//			for (SettingsInfo serverDetails : settingsInfos) {
//				deployLocation = serverDetails.getPropertyInfo(Constants.SERVER_DEPLOY_DIR).getValue();
//				break;
//			}		
//			File deployDir = new File(deployLocation);
//				if (!deployDir.exists()) {
//				throw new MojoExecutionException(" Deploy Directory" + deployLocation + " Does Not Exists ");
//			}
//			log.info("Project is deploying into " + deployLocation);
//			FileUtils.copyDirectoryStructure(tempDir.getAbsoluteFile(), deployDir);
//			log.info("Project is deployed successfully");
//		} catch (Exception e) {
//			log.error(e);
//			throw new MojoExecutionException(e.getMessage(), e);
//		}
//	}
	
	private List<SettingsInfo> getSettingsInfo(String configType) throws PhrescoException {
		ProjectAdministrator projAdmin = PhrescoFrameworkFactory.getProjectAdministrator();
		return projAdmin.getSettingsInfos(configType, getProjectRoot(baseDir).getName(), environmentName);
	}
	
	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}

class JDWarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".war");
	}

}
