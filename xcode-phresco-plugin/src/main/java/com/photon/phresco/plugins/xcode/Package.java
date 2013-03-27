package com.photon.phresco.plugins.xcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.google.gson.Gson;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ActionType;
import com.photon.phresco.framework.api.ApplicationManager;
import com.photon.phresco.framework.api.ProjectManager;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class Package implements PluginConstants {

	private String environmentName;
	private Log log;
	/**
	 * Execute the xcode command line utility.
	 * @throws PhrescoException 
	 */
	public void pack(Configuration config, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
	    BufferedReader projectInfoReader = null;
		try {
		    this.log = log;
		    File baseDir = mavenProjectInfo.getBaseDir();
            Map<String, String> configs = MojoUtil.getAllValues(config);
            environmentName = configs.get(ENVIRONMENT_NAME);
            String buildName = configs.get(BUILD_NAME);
            String buildNumber = configs.get(BUILD_NUMBER);
            String sdk = configs.get(SDK);
            String target = configs.get(TARGET);
            if (StringUtils.isNotEmpty(target)) {
            	target = target.replace(STR_SPACE, SHELL_SPACE);
            }
            String configuration = configs.get(MODE);
            String encrypt = configs.get(ENCRYPT);
            String plistFile = configs.get(PLIST_FILE);
            String projectType = configs.get(PROJECT_TYPE);
            
            PluginUtils.checkForConfigurations(baseDir, environmentName);
            
		    StringBuilder projectInfoFile = new StringBuilder(baseDir.getPath())
		    .append(File.separator)
		    .append(Constants.DOT_PHRESCO_FOLDER)
		    .append(File.separator)
		    .append(Constants.PROJECT_INFO_FILE);
		    Gson gson = new Gson();
		    projectInfoReader = new BufferedReader(new FileReader(new File(projectInfoFile.toString())));
		    ProjectInfo projectInfo = gson.fromJson(projectInfoReader, ProjectInfo.class);
		    ApplicationInfo applicationInfo = projectInfo.getAppInfos().get(0);
		    String embedAppId = applicationInfo.getEmbedAppId();
		    if (StringUtils.isNotEmpty(embedAppId)) {
		        embedApplication(baseDir, projectInfo, embedAppId);
		    }
		    
		    StringBuilder sb = new StringBuilder();
			sb.append(XCODE_BUILD_COMMAND);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + PROJECT_TYPE + EQUAL + projectType);
			
			if (StringUtils.isNotEmpty(environmentName)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + ENVIRONMENT_NAME + EQUAL + environmentName);
			}
			
			if (StringUtils.isNotEmpty(sdk)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + SDK + EQUAL + sdk);
			}
			
			if (StringUtils.isNotEmpty(target)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + TARGET_NAME + EQUAL + target);
			}
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + CONFIGURATION + EQUAL + configuration);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + ENCRYPT + EQUAL + encrypt);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + PLIST_FILE + EQUAL + plistFile);
			
			if (StringUtils.isNotEmpty(buildName)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + BUILD_NAME + EQUAL + buildName);
			}
			
			if (StringUtils.isNotEmpty(buildNumber)) {
				sb.append(STR_SPACE);
				sb.append(HYPHEN_D + BUILD_NUMBER + EQUAL + buildNumber);
			}
			
			System.out.println("Command" + sb.toString());
			boolean status = Utility.executeStreamconsumer(sb.toString(), baseDir.getPath());
			if(!status) {
				try {
					throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
				} catch (MojoExecutionException e) {
					throw new PhrescoException(e);
				}
			}
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		} finally {
            if (projectInfoReader != null) {
                try {
                    projectInfoReader.close();
                } catch (IOException e) {
                    throw new PhrescoException(e);
                }
            }
        }

	}

    private void embedApplication(File baseDir, ProjectInfo projectInfo, String embedAppId) throws PhrescoException {
        try {
        ProjectManager projectManager = PhrescoFrameworkFactory.getProjectManager();
        ProjectInfo embedProject = projectManager.getProject(projectInfo.getId(), projectInfo.getCustomerIds().get(0), embedAppId);
        ApplicationInfo embedAppInfo = embedProject.getAppInfos().get(0);
        String embedBaseDir = Utility.getProjectHome() + embedAppInfo.getAppDirName();
        StringBuilder packageInfoFile = new StringBuilder(embedBaseDir)
        .append(File.separator)
        .append(Constants.PACKAGE_INFO_FILE);
        MojoProcessor processor = new MojoProcessor(new File(packageInfoFile.toString()));
        Parameter parameter = processor.getParameter(Constants.MVN_GOAL_PACKAGE, Constants.MOJO_KEY_ENVIRONMENT_NAME);
        parameter.setValue(environmentName);
        processor.save();
        
        ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
        BufferedReader reader = applicationManager.performAction(embedProject, ActionType.BUILD, null, embedBaseDir);
        while (reader.readLine() != null) {
            System.out.println(reader.readLine());
        }
        File pomFile = new File(baseDir.getPath() + File.separator + Constants.POM_NAME);
        PomProcessor pomProcessor = new PomProcessor(pomFile);
        String appTargetProp = pomProcessor.getProperty(Constants.POM_PROP_KEY_EMBED_APP_TARGET_DIR);
        if(StringUtils.isEmpty(appTargetProp)) {
            throw new PhrescoException("Target directory to embed the selected application is not specified");
        }
        String appTargetDir = baseDir + File.separator + appTargetProp;
        File[] wwwFiles = new File(appTargetDir).listFiles();
        if (!ArrayUtils.isEmpty(wwwFiles)) {
            for (File file : wwwFiles) {
                file.delete();
            }
        }
        String source = embedBaseDir + File.separator + Constants.DO_NOT_CHECKIN_DIRY + File.separator + "target";
        File[] listFiles = new File(source).listFiles();
        String fileName = "";
        for (File file : listFiles) {
            if (file.getName().endsWith(".war")) {
                source = source + File.separator + file.getName();
                fileName = file.getName();
                break;
            }
        }
        File src = new File(source);
        File dest = new File(appTargetDir + File.separator + fileName);
        FileUtils.copyFile(src, dest);
        System.out.println("[info] Extracting the war");
        BufferedReader warExtractRdr = Utility.executeCommand("jar xvf " + fileName, appTargetDir);
        while (warExtractRdr.readLine() != null) {
            System.out.println(warExtractRdr.readLine());
        }
        System.out.println("[info] war extracted successfully...");
        dest.delete();
        } catch (PhrescoException e) {
            throw new PhrescoException(e);
        } catch (IOException e) {
            throw new PhrescoException(e);
        } catch (PhrescoPomException e) {
            throw new PhrescoException(e);
        }
    }

}