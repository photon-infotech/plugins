/**
 * Phresco Plugin Commons
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
package com.photon.phresco.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.configuration.Environment;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.impl.ConfigManagerImpl;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class PhrescoAppiumPlugin implements PluginConstants {
	
	public Log log;

	public PhrescoAppiumPlugin(Log log) {
		this.log = log;
	}

	protected final Log getLog() {
		return log;
	}

	public ExecutionStatus startAppium(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		File baseDir = mavenProjectInfo.getBaseDir();
		
		String subModule = "";
		File workingDir = baseDir;
		if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
			subModule = mavenProjectInfo.getModuleName();
			workingDir = new File(baseDir + File.separator + subModule);
		}
		
		// Split Projects
		MavenProject project = mavenProjectInfo.getProject();
		String dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
		File dotPhrescoDir = baseDir;
		if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
			dotPhrescoDir = new File(baseDir.getParent() +  File.separatorChar + dotPhrescoDirName);
		}
		dotPhrescoDir = new File(dotPhrescoDir.getPath() + File.separatorChar + subModule);
					
		String host = "", androidPackage = "", androidActivity = "", iosDeviceType = "", deviceId = "";
		Integer port, buildNumber;
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		Properties properties = new Properties();       
		
		try {
			// Appium Home Path
			String appiumHome = project.getProperties().getProperty(Constants.POM_PROP_KEY_APPIUM_HOME);
			if (StringUtils.isEmpty(appiumHome)) {
				log.error(APPIUM_MSG_HOME_NOT_FOUND);
			    throw new PhrescoException(APPIUM_MSG_HOME_NOT_FOUND);
			}
			
			// Host
			host = configs.get(HOST);
			properties.put(HOST, host);
			
			// Port
			port = Integer.parseInt(configs.get(PORT));
			properties.put(PORT, port.toString());
			
			// If port is already in use
			Boolean isConnectionAlive = Utility.isConnectionAlive(HTTP, host, port);
			if (isConnectionAlive) {
				log.error(APPIUM_MSG_PORT_IN_USE);
			    throw new PhrescoException(APPIUM_MSG_PORT_IN_USE);
			}
			
			// Build Number
			buildNumber = Integer.parseInt(configs.get(BUILD_NUMBER));
	        BuildInfo buildInfo = Utility.getBuildInfo(buildNumber, getBuildInfoPath(baseDir.getPath(),subModule).toString());
	        if (buildInfo == null) {
            	throw new PhrescoException(APPIUM_MSG_BUILD_INFO_NOT_FOUND + buildNumber);
            }
			
			// iOS Device Type - Simulator/Device
			if (StringUtils.isNotEmpty(configs.get(IOS_DEVICE_TYPE))) {
				iosDeviceType = configs.get(IOS_DEVICE_TYPE);
			}
			
			// iOS Device UDID
			if (StringUtils.isNotEmpty(configs.get(DEVICE_UDID))) {
				deviceId = configs.get(DEVICE_UDID);
			}
			
			// Android Package
			if (StringUtils.isNotEmpty(configs.get(ANDROID_PACKAGE))) {
				androidPackage = configs.get(ANDROID_PACKAGE);
				properties.put(ANDROID_PACKAGE, androidPackage);
			}
			
			// Android Activity
			if (StringUtils.isNotEmpty(configs.get(ANDROID_ACTIVITY))) {
				androidActivity = configs.get(ANDROID_ACTIVITY);
				properties.put(ANDROID_ACTIVITY, androidActivity);
			}
						
			// Building command
			StringBuilder command = new StringBuilder()
	        .append(NODE_CMD)
	        .append(STR_SPACE)
	        .append(DOT)
	        
	        // Host
	        .append(STR_SPACE)
	        .append(HYPHEN_A)
	        .append(STR_SPACE)
	        .append(host)
	        
	        // Port
	        .append(STR_SPACE)
	        .append(HYPHEN_P)
	        .append(STR_SPACE)
	        .append(port);
	        
	        // App Path
	        if (StringUtils.isNotEmpty(iosDeviceType)) {
	        	if (!iosDeviceType.equals(DEVICE)) {
		        	// App path - iOS - .app
	        		String iosAppPath = buildInfo.getDeployLocation();
		        	command.append(STR_SPACE)
			        .append(HYPHEN_APP)
			        .append(STR_SPACE)
		        	.append(iosAppPath);
		        	properties.put(APP_PATH, iosAppPath);

		        	if (iosDeviceType.equals(IPHONE_SIMULATOR)) {
			        	command.append(STR_SPACE)
				        .append(FORCE_IPHONE);
		        	} else if (iosDeviceType.equals(IPAD_SIMULATOR)) {
		        		command.append(STR_SPACE)
				        .append(FORCE_IPAD);
		        	}
	        	} else {
		        	// IPA path
	        		String ipaPath = getIpaPath(baseDir.getPath(), subModule, buildInfo.getBuildName()); 
	        		command.append(STR_SPACE)
	        		.append(HYPHEN_IPA)
	        		.append(STR_SPACE)
	        		.append(ipaPath);
	        		properties.put(APP_PATH, ipaPath);
	        		
	        		// UDID
	        		command.append(STR_SPACE)
	        		.append(HYPHEN_U)
	        		.append(STR_SPACE)
	        		.append(deviceId);
	        	}
	        } else {
	        	// App path - Android - .apk	        
		        StringBuilder appPath = new StringBuilder(workingDir.getPath())
		        .append(File.separator)
	        	.append(DO_NOT_CHECKIN)
	        	.append(File.separator)
	        	.append(BUILD)
	        	.append(File.separator)
	        	.append(buildInfo.getBuildName())
	        	.append(DOT_APK);
	        	
	        	command.append(STR_SPACE)
		        .append(HYPHEN_APP)
		        .append(STR_SPACE)
	        	.append(appPath.toString());
	        	properties.put(APP_PATH, appPath.toString());
	        	
		        // Android Package
		        if (!androidPackage.isEmpty()) {
		        	command.append(STR_SPACE)
			        .append(APP_PKG)
			        .append(STR_SPACE)
			        .append(androidPackage);
		        }
		        
		        // Android Activity
		        if (!androidActivity.isEmpty()) {
		        	command.append(STR_SPACE)
			        .append(APP_ACTIVITY)
			        .append(STR_SPACE)
			        .append(androidActivity);
		        }
	        }
       
	        // Updating Configuration
	        String configFileName = project.getProperties().getProperty(Constants.PHRESCO_FUNCTIONAL_TEST_ADAPT_DIR);
	        File configFile = new File(workingDir + configFileName);
	        updateAppiumConfiguration(configFile, properties);
	        
			log.info(APPIUM_MSG_STARTING);
			
			// Starting Appium
			FileOutputStream fos = null;
		        
		    File logDir = new File(baseDir + File.separator + Constants.DO_NOT_CHECKIN_DIRY + File.separator + Constants.LOG_DIRECTORY);
		    if (!logDir.exists()) {
		    	logDir.mkdirs();
		    }
		    File logFile  = new File(logDir + Constants.SLASH + Constants.APPIUM_LOG);
		    fos = new FileOutputStream(logFile, false);
		    Utility.executeStreamconsumerFOS(appiumHome, command.toString(), fos);
		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		}
		return new DefaultExecutionStatus();
	}

	public ExecutionStatus stopAppium(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			File baseDir = mavenProjectInfo.getBaseDir();
			String subModule = "";
			File workingDir = baseDir;
			if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
				subModule = mavenProjectInfo.getModuleName();
				workingDir = new File(baseDir + File.separator + subModule);
			}        
	        Map<String, String> configs = MojoUtil.getAllValues(configuration);
	     	String portNo = configs.get(PORT);
	     	PluginUtils pluginUtils = new PluginUtils();
	     	pluginUtils.stopAppium(portNo, workingDir);
			log.info(APPIUM_MSG_STOPPED);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return new DefaultExecutionStatus();
	}
	
	private StringBuilder getBuildInfoPath(String rootModulePath, String subModuleName) throws PhrescoException {
		File pomFileLocation = Utility.getPomFileLocation(rootModulePath, subModuleName);
	    StringBuilder builder = new StringBuilder(pomFileLocation.getParent())
	    .append(File.separator)
	    .append(DO_NOT_CHECKIN)
	    .append(File.separator)
	    .append(BUILD)
	    .append(File.separator)
	    .append(BUILD_INFO);
	    return builder;
	}
	
	private String getIpaPath(String rootModulePath, String subModuleName, String buildName) throws PhrescoException {
		Boolean isFilePresent = false;
		File pomFileLocation = Utility.getPomFileLocation(rootModulePath, subModuleName);
	    StringBuilder builder = new StringBuilder(pomFileLocation.getParent())
	    .append(File.separator)
	    .append(DO_NOT_CHECKIN)
	    .append(File.separator)
	    .append(BUILD)
	    .append(File.separator)
	    .append(buildName.substring(0, buildName.length() - 4))
	    .append(File.separator);
	    File parentDir = new File(builder.toString());
	    String files[] = parentDir.list();
	    for(String file:files) {
	    	if (file.endsWith(DOT_IPA)) {
	    		builder.append(file);
	    		isFilePresent = true;
	    		break;
	    	}
	    }
	    if (!isFilePresent) {
	    	throw new PhrescoException(APPIUM_MSG_IPA_NOT_FOUND);
	    }
	    return builder.toString();
	}
	
	private void updateAppiumConfiguration(File configFile, Properties properties) throws ConfigurationException {
		ConfigManager configManager = new ConfigManagerImpl(configFile);      
        List<Environment> environments = new ArrayList<Environment>();
        Environment env = new Environment(APPIUM_ENV_NAME, APPIUM_ENV_DESC, true, null);
        List<com.photon.phresco.configuration.Configuration> configurations = new ArrayList<com.photon.phresco.configuration.Configuration>();
        com.photon.phresco.configuration.Configuration config = new com.photon.phresco.configuration.Configuration(APPIUM_CONFIG_NAME, APPIUM_CONFIG_NAME, APPIUM_CONFIG_TYPE, properties);
        configurations.add(config);
        env.setConfigurations(configurations);
        environments.add(env);
        configManager.addEnvironments(environments);
	}
}