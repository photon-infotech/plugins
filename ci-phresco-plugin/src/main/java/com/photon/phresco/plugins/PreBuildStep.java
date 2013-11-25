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
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.commons.model.CIJob;
import com.photon.phresco.commons.model.ContinuousDelivery;
import com.photon.phresco.commons.model.ProjectDelivery;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.util.PomProcessor;


public class PreBuildStep  implements PluginConstants {
	
    private MavenProject project;
    private File baseDir;
    private Log log;
    private String pom;
    private String pomFileName;
    private File pomFile;
    
    private File getPomFile() throws PhrescoException {
    	PluginUtils pu = new PluginUtils();
    	ApplicationInfo appInfo = pu.getAppInfo(baseDir);
    	pomFileName = Utility.getPhrescoPomFromWorkingDirectory(appInfo, baseDir);
    	pom = pomFileName;
    	File pom = new File(baseDir.getPath() + File.separator + pomFileName);
    	return pom;
    }

    
	public void performCIPreBuildStep(String name, String goal, String phase,String creationType, String id, String continuousDeliveryName, String moduleName, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		log.info("CI prebuild step execution reached " + name);
		log.info("goal is  " + goal);
		try {
	        this.log = log;
	        baseDir = mavenProjectInfo.getBaseDir();
	        project = mavenProjectInfo.getProject();
	        
	        // module name
	        String pomVersion = mavenProjectInfo.getProject().getVersion();
	        
			// get projects plugin info file path
			File rootProjectInfo = new File(baseDir, DOT_PHRESCO_FOLDER + File.separator + PROJECT_INFO_FILE);
			if (!rootProjectInfo.exists()) {
				throw new MojoExecutionException("Project info file is not found in jenkins workspace dir " + baseDir.getCanonicalPath());
			}
			log.info("projectInfo path ... " + rootProjectInfo.getCanonicalPath());
			ApplicationInfo rootAppInfo = getApplicationInfo(rootProjectInfo);
			String rootAppDirName = rootAppInfo.getAppDirName();
	     // Multi module handling
	        if (StringUtils.isNotEmpty(moduleName)) {
	        	baseDir = new File(baseDir, moduleName);
	        }
	        pomFile = getPomFile();
	        pom = project.getFile().getName();
	        
	        System.out.println("pomFile > " + pomFile.getPath());
	        org.apache.maven.model.Model project = new org.apache.maven.model.Model();
	        PomProcessor pp = new PomProcessor(pomFile);
	        com.phresco.pom.model.Model.Properties modelProperties = pp.getModel().getProperties();
	        List<Element> propElem = modelProperties.getAny();
	        Properties properties = new Properties();
	        for (Element element : propElem) {
	        	properties.put(element.getTagName(), element.getTextContent());
	        }
	        project.setProperties(properties);

	        MavenProject mavenProject = new MavenProject(project);
	        mavenProject.setFile(pomFile);
	        mavenProject.setVersion(pomVersion);
	        mavenProjectInfo.setProject(mavenProject);
	        mavenProjectInfo.setBaseDir(pomFile);
	        
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
			CIJob job = getJob(appInfo, name, creationType, id, continuousDeliveryName);
			if (job == null) {
				throw new PhrescoException("Job object is empty ");
			}
			
			
			File phrescoPluginInfoFile = getPhrescoPluginInfoFileInJenkins(name, phase, moduleName);
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
			} else if(Constants.PHASE_LOAD_TEST.equals(phase)) {
				log.info("Execute Load Test");
				testDirectory = Constants.POM_PROP_KEY_LOADTEST_DIR;
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
	
	public File getPhrescoPluginInfoFileInJenkins(String name, String phase, String moduleName) throws MojoExecutionException {
		log.info("getPhrescoPluginInfoFileInJenkins method called ... ");
		try {
			StringBuilder builder = new StringBuilder(baseDir.getPath());
	        builder.append(File.separator);
	        builder.append(name);
	        builder.append(File.separator);
	        if(StringUtils.isNotEmpty(moduleName)) {
	        	builder.append(moduleName);
	        	builder.append(File.separator);
	        }
	        builder.append(DOT_PHRESCO_FOLDER);
	        builder.append(File.separator);
	        builder.append(PHRESCO_HYPEN);
	        builder.append(phase);
	        builder.append(INFO_XML);
	        log.info("Info file path on the jenkins workspace => " + builder.toString());
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
	 public CIJob getJob(ApplicationInfo appInfo, String name, String creationType, String id, String continuousDeliveryName) throws PhrescoException {
		 List<CIJob> jobs = new ArrayList<CIJob>();
		 try {
			 log.info("Search for name => " + name);
			 if (StringUtils.isEmpty(name)) {
				 throw new PhrescoException("job name is empty");
			 }
			 if (creationType.equals("local")) {
				 jobs = getJobs(appInfo, id, continuousDeliveryName);
			 } else if (creationType.equals("global")) {
				 jobs = getJobs(id, continuousDeliveryName);
			 }
			 if(CollectionUtils.isEmpty(jobs)) {
				 throw new PhrescoException("job list is empty!!!!!!!!");
			 }
			 log.info("Job list found!!!!!");
			 for (CIJob job : jobs) {
				 log.info("job list job Names => " + job.getJobName());
				 if (job.getJobName().equals(name)) {
					 return job;
				 }
			 }
		 } catch (Exception e) {
			 throw new PhrescoException(e);
		 }
		 return null;
	 }
	 
	 public List<CIJob> getJobs(String id, String name) throws PhrescoException {
		 log.info("Global GetJobs Called!");
		 List<CIJob> jobs = new ArrayList<CIJob>();
		 try {
			 StringBuilder builder = new StringBuilder(baseDir.getPath());
			 builder.append(File.separator);
			 builder.append(DOT_PHRESCO_FOLDER);
			 builder.append(File.separator);
			 builder.append(GLOBAL_CI_INFO_FILE);
			 log.info("Test globa ci path => " + builder.toString());
			 File ciJobFile = new File(builder.toString());
			 if (!ciJobFile.isFile()) {
				 throw new PhrescoException("Ci job info file is not available to get the jobs in ProjectLevel.");
			 }
			 return extractJobs(ciJobFile, id , name);
		 } catch (Exception e) {
			 log.info("CI job info global file reading failed ");
			 throw new PhrescoException(e);
		 }
	 }
	 
	 public List<CIJob> getJobs(ApplicationInfo appInfo, String id, String name) throws PhrescoException {
		 log.info("Local GetJobs Called!");
		 try {
			 String ciJobPath = getCIJobPath(appInfo);
			 File ciJobFile = new File(ciJobPath);
			 if (!ciJobFile.isFile()) {
				 throw new PhrescoException("Ci job info file is not available to get the jobs in AppLevel .");
			 }
			 return extractJobs(ciJobFile, id, name);
		 } catch (Exception e) {
			 log.info("CI job info file reading failed ");
			 throw new PhrescoException(e);
		 }
	 }
	 
	 private List<CIJob> extractJobs(File file, String id, String name) throws PhrescoException {
		 try {
			 List<CIJob> jobs = new ArrayList<CIJob>();
			 List<ProjectDelivery> projectDeliveries;
			 projectDeliveries = Utility.getProjectDeliveries(file);
			 if (projectDeliveries != null) {
				 ContinuousDelivery continuousDelivery = Utility.getContinuousDelivery(id, name, projectDeliveries);
				 if (continuousDelivery != null) {
					 jobs = continuousDelivery.getJobs();
					 if (jobs != null) {
						 return jobs;
					 }
				 }
			 }
		 } catch (PhrescoException e) {
			 throw new PhrescoException();
		 }
		 return null;
	 }
	 
	 private String getCIJobPath(ApplicationInfo appInfo) {
		 StringBuilder builder = new StringBuilder(baseDir.getPath());
		 builder.append(File.separator);
		 builder.append(DOT_PHRESCO_FOLDER);
		 builder.append(File.separator);
		 builder.append(CI_INFO_FILE);
		 log.info("CI info file path => "  + builder.toString());
		 return builder.toString();
	 }
}
