/**
 * ci-phresco-plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.model.CIJob;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.util.PomProcessor;


public class PreBuildStep  implements PluginConstants {
	
    private MavenProject project;
    private File baseDir;
    private Log log;
    private PluginPackageUtil util;
    private String pom;
    
	public void performCIPreBuildStep(String name, String goal, String phase, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		log.info("CI prebuild step execution reached " + name);
		log.info("goal is  " + goal);
		try {
	        this.log = log;
	        baseDir = mavenProjectInfo.getBaseDir();
	        project = mavenProjectInfo.getProject();
	        pom = project.getFile().getName();
	        
	        // getting jenkins workspace job dir
	        String jenkinsJobDirPath = getJenkinsJobDirPath(name);
	        log.info("jenkinsJobDirPath ... " + jenkinsJobDirPath);
			File jenkinsJobDir = new File(jenkinsJobDirPath);
			
			// get projects plugin info file path
			File projectInfo = new File(baseDir, DOT_PHRESCO_FOLDER + File.separator + PROJECT_INFO_FILE);
			if (!projectInfo.exists()) {
				throw new MojoExecutionException("Project info file is not found in jenkins workspace dir " + baseDir.getCanonicalPath());
			}
			log.info("projectInfo path ... " + projectInfo.getCanonicalPath());
			ApplicationInfo appInfo = getApplicationInfo(projectInfo);
			if (appInfo == null) {
				throw new MojoExecutionException("AppInfo value is Null ");
			}
			
			// get execution project's appid and dirname and search in phresco project workspace name and get phrescoPluginInfo file path
			String appId = appInfo.getId();
			log.info("appId ... " + appId);
			String appDirName = appInfo.getAppDirName();
			log.info("appDirName ... " + appDirName);
			if (StringUtils.isEmpty(appDirName)) {
				throw new MojoExecutionException("appDirName is empty");
			}
			
			// get job name and get the ciJob object from CIJob file and update it in phrescoPackage file using mojo
			CIJob job = getJob(appInfo, name);
			if (job == null) {
				throw new PhrescoException("Job object is empty ");
			}
			
			File phrescoPluginInfoFile = getPhrescoPluginInfoFileInJenkins(name, phase);
			log.info("phresco Plugin Info File in phresco projects workspace ... " + phrescoPluginInfoFile.getPath());
			log.info("phresco Plugin Info File exists ... " + phrescoPluginInfoFile.exists());
			
			MojoProcessor mojo = new MojoProcessor(phrescoPluginInfoFile);
			String testDirectory = "";
			if (Constants.PHASE_FUNCTIONAL_TEST.equals(phase)) {
				File pomFile = new File(baseDir, pom);
				log.info("Pom path " + pomFile.getPath());
				PomProcessor pm = new PomProcessor(pomFile);
				String seleniumTool = pm.getProperty(Constants.POM_PROP_KEY_FUNCTEST_SELENIUM_TOOL);
				if (StringUtils.isNotEmpty(seleniumTool)) {
					phase = phase + Constants.STR_HYPHEN + seleniumTool;
				}
			} else if(Constants.PHASE_PERFORMANCE_TEST.equals(phase)) {
				// Reads the ci.info from server/database/webservice folder,
				//inside of the performance folder and creates new json file
				//for each values present in the ci.info file.
				log.info("Execute Performance Test");
				testDirectory = Constants.POM_PROP_KEY_PERFORMANCETEST_DIR;
				loadPerformanceTestHandler(mavenProjectInfo, appInfo, testDirectory, phase);	
			} else if(Constants.PHASE_LOAD_TEST.equals(phase)) {
				log.info("Execute Load Test");
				testDirectory = Constants.POM_PROP_KEY_LOADTEST_DIR;
				loadPerformanceTestHandler(mavenProjectInfo, appInfo, testDirectory, phase);
			}
			
			log.info("phase " + phase);
			Configuration configuration = mojo.getConfiguration(phase);
			if (configuration != null) {
				List<Parameter> parameters = configuration.getParameters().getParameter();
				BeanUtils bu = new BeanUtils();
				
				if (CollectionUtils.isNotEmpty(parameters)) {
					for (Parameter parameter : parameters) {
						try {
							log.info("Storing parameter Key " + parameter.getKey());
							String paramValue = bu.getProperty(job, parameter.getKey());
							log.info("Storing paramValue " + paramValue);
							parameter.setValue(paramValue);
						} catch (Exception e) {
							log.info("Key is missing ... " + parameter.getKey());
						}
					}
				}
			}
			mojo.save();
		} catch (Exception e) {			
			throw new PhrescoException(e);
		}
	}

	private void loadPerformanceTestHandler(MavenProjectInfo mavenProjectInfo,
			ApplicationInfo appInfo, String testDirectory, String phase) throws Exception {
		
		BufferedReader reader = null ;	
		
		try {										
			List<String> tests = new ArrayList<String>(3);
			tests.add(PER_TEST_SERVER);
			tests.add(PER_TEST_WEBSERVICE);
			
			if(Constants.PHASE_PERFORMANCE_TEST.equals(phase)) {
				tests.add(PER_TEST_DATABASE);
			}
			
			for (String test : tests) {
				
				StringBuilder copyDir = new StringBuilder(baseDir.toString())				
				.append(mavenProjectInfo.getProject().getProperties().getProperty(testDirectory))
				.append(File.separator)
				.append(test)
				.append(File.separator)
				.append(Constants.FOLDER_JSON);				
				
				StringBuilder infoFilePath = new StringBuilder(Utility.getProjectHome())
				.append(appInfo.getAppDirName())
				.append(mavenProjectInfo.getProject().getProperties().getProperty(testDirectory))
				.append(File.separator)
				.append(test)
				.append(File.separator)
				.append(Constants.FOLDER_JSON)
				.append(File.separator)
//				.append("CITemp")
//				.append(File.separator)
				.append(CI_INFO);
				
				File infoFile = new File(infoFilePath.toString());
				if(infoFile.exists()) {	
					reader = new BufferedReader(new FileReader(infoFilePath.toString()));
					String lineToRead = null ;
					while((lineToRead = reader.readLine()) != null) {
						String [] jsonFiles = lineToRead.split("\\,");
						for (String jsonFile : jsonFiles) {	
							// 
							StringBuilder s = new StringBuilder(copyDir)				    	
					    	.append(File.separator)
					    	.append(jsonFile);								
							
							
							StringBuilder sb = new StringBuilder(Utility.getProjectHome())
					    	.append(appInfo.getAppDirName())				
							.append(mavenProjectInfo.getProject().getProperties().getProperty(testDirectory))
							.append(File.separator)
							.append(test)
							.append(File.separator)
							.append(Constants.FOLDER_JSON)
							.append(File.separator)
//							.append("CITemp")
//							.append(File.separator)
							.append(jsonFile);		
							
							File source = new File(sb.toString());
							File destination = new File(s.toString());
							// source file is exist check . . .
							if(source.exists()) {
								FileUtils.copyFile(source, destination);								
							}							
						}
					}						
				}					
			}
		} catch (Exception e) {		
		} finally {
			Utility.closeReader(reader);
		}
	}
	
	public File getPhrescoPluginInfoFile(String appDirName, String goal) throws MojoExecutionException {
		log.info("getPhrescoPluginInfoFile goal  ... " + goal);
		String projectHome = Utility.getProjectHome();
		if (StringUtils.isEmpty(projectHome)) {
			throw new MojoExecutionException("Phresco project home not found ");
		}
		StringBuilder sb = new StringBuilder(projectHome);
		sb.append(appDirName);
		sb.append(File.separator);
		sb.append(DOT_PHRESCO_FOLDER);
		sb.append(File.separator);
		sb.append(PHRESCO_HYPEN);
		sb.append(goal);
		sb.append(INFO_XML);
		File pluginInfoFile = new File(sb.toString());
		log.info("getPhrescoPluginInfoFile method fiel path  ... " + sb.toString());
		if (!pluginInfoFile.exists()) {
			throw new MojoExecutionException("Plugin info file not found in phresco projects workspace for appDirName... " + appDirName + " Searched in... " + sb.toString());
		}
		return pluginInfoFile;
	}
	
	private ApplicationInfo getApplicationInfo(File projectInfoFile) throws MojoExecutionException {
		log.info("getApplicationInfo method called  ... " + projectInfoFile);
		try {
	        Gson gson = new Gson();
	        BufferedReader reader = null;
	        reader = new BufferedReader(new FileReader(projectInfoFile));
	        ProjectInfo projectInfo = gson.fromJson(reader, ProjectInfo.class);
	        log.info("projectInfo name ... " + projectInfo.getName());
	        List<ApplicationInfo> appInfos = projectInfo.getAppInfos();
	        for (ApplicationInfo appInfo : appInfos) {
	        	log.info("appInfo dir name ... " + appInfo.getAppDirName());
				return appInfo;
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return null;
	}
	
	public File getPhrescoPluginInfoFileInJenkins(String name, String phase) throws MojoExecutionException {
		log.info("getPhrescoPluginInfoFileInJenkins method called ... ");
		try {
			String jenkinsHomePath = System.getenv(JENKINS_HOME);
			if (StringUtils.isEmpty(jenkinsHomePath)) {
				throw new MojoExecutionException("Jenkins Home not found in environemt variable ");
			}
			StringBuilder builder = new StringBuilder(jenkinsHomePath);
			builder.append(File.separator);
	        builder.append(WORKSPACE_DIR);
	        builder.append(File.separator);
	        builder.append(name);
	        builder.append(File.separator);
	        builder.append(DOT_PHRESCO_FOLDER);
	        builder.append(File.separator);
	        builder.append(PHRESCO_HYPEN);
	        builder.append(phase);
	        builder.append(INFO_XML);
			File pluginInfoFile = new File(builder.toString());
			log.info("getPhrescoPluginInfoFile method fiel path  ... " + builder.toString());
//			if (!pluginInfoFile.exists()) {
//				throw new MojoExecutionException("Plugin info file not found in jenkins workspace for jobaname ... " + name + " Searched in... " + builder.toString());
//			}
			return pluginInfoFile;
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private String getJenkinsJobDirPath(String name) throws MojoExecutionException {
		log.info("getJenkinsJobDirPath method called ... ");
		try {
			String jenkinsHomePath = System.getenv(JENKINS_HOME);
			if (StringUtils.isEmpty(jenkinsHomePath)) {
				throw new PhrescoException("Jenkins Home not found in environemt variable ");
			}
			StringBuilder builder = new StringBuilder(jenkinsHomePath);
			builder.append(File.separator);
	        builder.append(WORKSPACE_DIR);
	        builder.append(File.separator);
	        builder.append(name);
	        builder.append(File.separator);
	        builder.append(DOT_PHRESCO_FOLDER);
	        builder.append(File.separator);
	        FileUtils.mkdir(builder.toString());
	        log.info("Jenkins JobDir Path is  ... " + builder.toString());
	        return builder.toString();
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	 public CIJob getJob(ApplicationInfo appInfo, String name) throws PhrescoException {
		 try {
			 log.info("Search for name => " + name);
			 if (StringUtils.isEmpty(name)) {
				 throw new PhrescoException("job name is empty");
			 }
			 List<CIJob> jobs = getJobs(appInfo);
			 if(CollectionUtils.isEmpty(jobs)) {
				 throw new PhrescoException("job list is empty!!!!!!!!");
			 }
			 log.info("Job list found!!!!!");
			 for (CIJob job : jobs) {
				 log.info("job list job Names => " + job.getName());
				 if (job.getName().equals(name)) {
					 return job;
				 }
			 }
		 } catch (Exception e) {
			 throw new PhrescoException(e);
		 }
		 return null;
	 }
	 
	 public List<CIJob> getJobs(ApplicationInfo appInfo) throws PhrescoException {
		 log.info("GetJobs Called!");
		 try {
			 Gson gson = new Gson();
			 String ciJobPath = getCIJobPath(appInfo);
			 File ciJobFile = new File(ciJobPath);
			 if (!ciJobFile.isFile()) {
				 throw new PhrescoException("Ci job info file is not available to get the jobs .");
			 }
			 BufferedReader br = new BufferedReader(new FileReader(getCIJobPath(appInfo)));
			 Type type = new TypeToken<List<CIJob>>(){}.getType();
			 List<CIJob> jobs = gson.fromJson(br, type);
			 br.close();
			 return jobs;
		 } catch (Exception e) {
			 log.info("CI job info file reading failed ");
			 throw new PhrescoException(e);
		 }
	 }
	 
	 private String getCIJobPath(ApplicationInfo appInfo) {
		 StringBuilder builder = new StringBuilder(Utility.getProjectHome());
		 builder.append(appInfo.getAppDirName());
		 builder.append(File.separator);
		 builder.append(DOT_PHRESCO_FOLDER);
		 builder.append(File.separator);
		 builder.append(CI_INFO_FILE);
		 return builder.toString();
	 }
}
