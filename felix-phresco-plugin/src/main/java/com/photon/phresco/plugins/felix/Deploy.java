/**
 * java-phresco-plugin
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.photon.phresco.plugins.felix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.DatabaseUtil;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class Deploy implements PluginConstants {

	private static final String PROTOCOL_POSTFIX = "://";
	private static final String Felix_PROFILE_ID = "-PautoDeployBundle";
	private static final String PASSWORD = "-Dsling.password";
	private static final String USER_NAME = "-Dsling.user";
	private static final String TARGET_URL = "-Dsling.url";
	private static final String PACKAGE_FILE = "-Dsling.file";
	private MavenProject project;
	private File baseDir;
	private String buildNumber;
	private File buildDir;
	private String environmentName;
	private boolean importSql;
	private File buildFile;
	private File tempDir;
	private Log log;
	private String sqlPath;
	private PluginUtils pUtil;
	private String subModule = "";
	private File workingDirectory;
	private File dependencyJarDir;
	private String dependencyJars;
	private String packagingType;
	private String dotPhrescoDirName;
    private File dotPhrescoDir;
    private File srcDirectory;
    private String buildVersion;
    
	public void deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		buildVersion = mavenProjectInfo.getBuildVersion();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		buildNumber = configs.get(BUILD_NUMBER);
		importSql = Boolean.parseBoolean(configs.get(EXECUTE_SQL));
		sqlPath = configs.get(FETCH_SQL);
		dependencyJars = configs.get("fetchDependency");
		pUtil = new PluginUtils();
		File pomFile = project.getFile();
		workingDirectory = baseDir;
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			subModule = mavenProjectInfo.getModuleName();
			workingDirectory = new File(baseDir.getPath() + File.separator + subModule);
			pomFile = new File(workingDirectory.getPath() + File.separatorChar + pomFile.getName());
		} 
		dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        dotPhrescoDir = baseDir;
        if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        	dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
        }
        dotPhrescoDir = new File(dotPhrescoDir.getPath() + File.separatorChar + subModule);
        File splitProjectDirectory = pUtil.getSplitProjectSrcDir(pomFile, dotPhrescoDir, subModule);
    	srcDirectory = workingDirectory;
    	if (splitProjectDirectory != null) {
    		srcDirectory = splitProjectDirectory;
    	}
		try {
			init();
			initPomAndPackage();
			createDb();
			extractBuild();
			deployToServer();
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}

	}
	
	private void initPomAndPackage() throws PhrescoException {
		ApplicationInfo appInfo = new PluginUtils().getAppInfo(dotPhrescoDir);
		packagingType = project.getPackaging();
		if(project.getFile().getName().equals(appInfo.getPhrescoPomFile())) {
			try {
				File srcPomFile = new File(srcDirectory.getPath() + File.separator + appInfo.getPomFile());
				if(!srcPomFile.exists()) {
					srcPomFile = new File(srcDirectory.getPath() + File.separator + "pom.xml");
				}
				packagingType = new PomProcessor(srcPomFile).getModel().getPackaging();
			} catch (PhrescoPomException e) {
				throw new PhrescoException(e);
			}
		}
	}
	
	private void init() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(buildNumber)
					|| StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			BuildInfo buildInfo = pUtil.getBuildInfo(Integer.parseInt(buildNumber), workingDirectory.getPath());
			buildDir = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY);// build dir
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());// filename
			tempDir = new File(buildDir.getPath() + TEMP_DIR);// temp dir
			tempDir.mkdirs();
			dependencyJarDir = new File(workingDirectory.getPath()
					+ PluginConstants.DEPENDENCY_JAR_DIRECTORY);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void callUsage() throws MojoExecutionException {
		log.error("Invalid usage.");
		log.info("Usage of Deploy Goal");
		log.info("mvn java:deploy -DbuildName=\"Name of the build\""
				+ " -DenvironmentName=\"Multivalued evnironment names\""
				+ " -DimportSql=\"Does the deployment needs to import sql(TRUE/FALSE?)\"");
		throw new MojoExecutionException(
				"Invalid Usage. Please see the Usage of Deploy Goal");
	}

	private void createDb() throws MojoExecutionException {
		DatabaseUtil util = new DatabaseUtil();
		try {
			util.fetchSqlConfiguration(sqlPath, importSql, srcDirectory, environmentName, dotPhrescoDir);
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

	private void deployToServer() throws MojoExecutionException,
			PhrescoException {
		try {
			List<com.photon.phresco.configuration.Configuration> configurations = pUtil.getConfiguration(dotPhrescoDir, environmentName, Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration configuration : configurations) {
				deploy(configuration);
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void deploy(com.photon.phresco.configuration.Configuration configuration) throws MojoExecutionException, PhrescoException {
		if (configuration == null) {
			return;
		}
		String serverhost = configuration.getProperties().getProperty(
				Constants.SERVER_HOST);
		String serverport = configuration.getProperties().getProperty(
				Constants.SERVER_PORT);
		String serverprotocol = configuration.getProperties().getProperty(
				Constants.SERVER_PROTOCOL);
		String serverusername = configuration.getProperties().getProperty(
				Constants.SERVER_ADMIN_USERNAME);
		String serverpassword = configuration.getProperties().getProperty(
				Constants.SERVER_ADMIN_PASSWORD);
		String context = configuration.getProperties().getProperty(
				Constants.SERVER_CONTEXT);
		String certificatePath = configuration.getProperties().getProperty(Constants.CERTIFICATE);

		StringBuilder felixUrl = new StringBuilder();
		felixUrl.append(serverprotocol);
		felixUrl.append(PROTOCOL_POSTFIX);
		felixUrl.append(serverhost);
		felixUrl.append(COLON);
		felixUrl.append(serverport);
		felixUrl.append(FORWARD_SLASH);
		felixUrl.append(context);
		File bundleFile;
		// remote deployment
		if (dependencyJars != null && dependencyJarDir.exists()) {
			List<String> dependencyJarList = Arrays.asList(dependencyJars
					.split("\\s*,\\s*"));
			for (String nextFile : dependencyJarList) {
				bundleFile = new File(dependencyJarDir + File.separator
						+ nextFile);
				if (bundleFile.exists()) {
					log.info("Deploying Dependency Bundle "+ nextFile +" ...");
					deployToServer(bundleFile, felixUrl.toString(),
							serverusername, serverpassword, serverprotocol, certificatePath);
				}
			}
		}
		bundleFile = getBundleFile();
		log.info("============================================");
		log.info("Deploying Bundle "+ bundleFile +" ...");
		log.info("============================================");
		deployToServer(bundleFile, felixUrl.toString(), serverusername,
				serverpassword, serverprotocol, certificatePath);
		log.info("+++++++++++++++++++++++++++++++++++++++++++++++");
	}

	private File getBundleFile() {
		String[] list;
		File bundleFile = null;
		if (packagingType.equals("war")) {
			String warFileName = "";
			list = tempDir.list(new JDWarFileNameFilter());
			if (list.length > 0) {
				warFileName = list[0];
				bundleFile = new File(tempDir.getPath() + "/"
						+ warFileName);
			}
		} else {
			String jarFileName = "";
			list = tempDir.list(new JDJarFileNameFilter());
			if (list.length > 0) {
				jarFileName = list[0];
				bundleFile = new File(tempDir.getPath() + "/"
						+ jarFileName);
			}
		}
		return bundleFile;
	}
	
	private void deployToServer(File bundleFile, String felixUrl, String username, String password, String serverprotocol, String certificatePath)
			throws MojoExecutionException {

		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			log.info("Inside deployToServer");
				StringBuilder sb = new StringBuilder();
				sb.append(MVN_CMD);
				sb.append(STR_SPACE);
				sb.append(MVN_PHASE_CLEAN);
				sb.append(STR_SPACE);
				sb.append(MVN_PHASE_INSTALL);
				sb.append(STR_SPACE);
				sb.append(Felix_PROFILE_ID);
				sb.append(STR_SPACE);
				sb.append(TARGET_URL+"="+felixUrl);
				sb.append(STR_SPACE);
				sb.append(USER_NAME+"="+username);
				sb.append(STR_SPACE);
				sb.append(PASSWORD+"="+password);
				sb.append(STR_SPACE);
				sb.append(PACKAGE_FILE+"=\""+bundleFile+"\"");
				sb.append(STR_SPACE);
				sb.append(SKIP_TESTS);
				sb.append(STR_SPACE);
				sb.append("-Dpackage.version=" + buildVersion);
				sb.append(STR_SPACE);
				sb.append(Constants.HYPHEN_F);
				sb.append(STR_SPACE);
				sb.append(project.getFile().getName());
				log.info(" Before IF sb == "+ sb);
				if (serverprotocol.equals(HTTPS) && certificatePath != null) {

					File certificateFile = null;
					if (new File(baseDir.getPath(), certificatePath).exists()) {
						certificateFile = new File(certificatePath);
					} else {
						certificateFile = new File("../" + certificatePath);
					}
					sb.append(STR_SPACE);
					sb.append(JAVAX_TRUSTSTORE);
					sb.append("\""+certificateFile.getPath()+"\"");
				}
				bufferedReader = Utility.executeCommand(sb.toString(), workingDirectory.getPath());
				String line = null;
				
				while ((line = bufferedReader.readLine()) != null) {
					if (line.startsWith("[ERROR]")) {
						System.out.println(line);
						errorParam = true;
					}
				}
				
				if (errorParam) {
					throw new MojoExecutionException("Deployment Failed ");
				} else {
					log.info(" Bundle Deployed to " + felixUrl);
				}
			} catch (IOException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}  finally {
				Utility.closeStream(bufferedReader);
			}
		}

	public class PhrescoDirFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.equals(DOT_PHRESCO_FOLDER);
		}
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

class JDJarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".jar");
	}
}
