package com.photon.phresco.plugins;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.beanutils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.*;
import org.apache.maven.project.*;
import org.codehaus.plexus.util.*;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.photon.phresco.commons.model.*;
import com.photon.phresco.exception.*;
import com.photon.phresco.framework.model.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.*;
import com.photon.phresco.plugins.util.*;
import com.photon.phresco.util.*;
import com.phresco.pom.util.*;

public class PreBuildStep  implements PluginConstants {
	
    private MavenProject project;
    private File baseDir;
    private Log log;
    private PluginPackageUtil util;
    
	public void performCIPreBuildStep(String jobName, String goal, String phase, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		log.info("CI prebuild step execution reached " + jobName);
		log.info("goal is  " + goal);
		try {
	        this.log = log;
	        baseDir = mavenProjectInfo.getBaseDir();
	        project = mavenProjectInfo.getProject();
	        
	        // getting jenkins workspace job dir
	        String jenkinsJobDirPath = getJenkinsJobDirPath(jobName);
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
			CIJob job = getJob(appInfo, jobName);
			if (job == null) {
				throw new PhrescoException("Job object is empty ");
			}
			
			File phrescoPluginInfoFile = getPhrescoPluginInfoFileInJenkins(jobName, phase);
			log.info("phresco Plugin Info File in phresco projects workspace ... " + phrescoPluginInfoFile.getCanonicalPath());
			
			MojoProcessor mojo = new MojoProcessor(phrescoPluginInfoFile);
			
			if (Constants.PHASE_FUNCTIONAL_TEST.equals(phase)) {
				File pomFile = new File(baseDir, POM_XML);
				log.info("Pom path " + pomFile.getPath());
				PomProcessor pm = new PomProcessor(pomFile);
				String seleniumTool = pm.getProperty(Constants.POM_PROP_KEY_FUNCTEST_SELENIUM_TOOL);
				if (StringUtils.isNotEmpty(seleniumTool)) {
					phase = phase + Constants.STR_HYPHEN + seleniumTool;
				}
			}
			
			log.info("phase " + phase);
			Configuration configuration = mojo.getConfiguration(phase);
			List<Parameter> parameters = configuration.getParameters().getParameter();
			BeanUtils bu = new BeanUtils();
			
			if (CollectionUtils.isNotEmpty(parameters)) {
				for (Parameter parameter : parameters) {
					log.info("Storing parameter Key " + parameter.getKey());
					String paramValue = bu.getProperty(job, parameter.getKey());
					log.info("Storing paramValue " + paramValue);
					parameter.setValue(paramValue);					
				}
			}
			mojo.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PhrescoException(e);
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
	
	public File getPhrescoPluginInfoFileInJenkins(String jobName, String phase) throws MojoExecutionException {
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
	        builder.append(jobName);
	        builder.append(File.separator);
	        builder.append(DOT_PHRESCO_FOLDER);
	        builder.append(File.separator);
	        builder.append(PHRESCO_HYPEN);
	        builder.append(phase);
	        builder.append(INFO_XML);
			File pluginInfoFile = new File(builder.toString());
			log.info("getPhrescoPluginInfoFile method fiel path  ... " + builder.toString());
			if (!pluginInfoFile.exists()) {
				throw new MojoExecutionException("Plugin info file not found in jenkins workspace for jobaname ... " + jobName + " Searched in... " + builder.toString());
			}
			return pluginInfoFile;
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private String getJenkinsJobDirPath(String jobName) throws MojoExecutionException {
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
	        builder.append(jobName);
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
	
	 public CIJob getJob(ApplicationInfo appInfo, String jobName) throws PhrescoException {
		 try {
			 log.info("Search for jobName => " + jobName);
			 if (StringUtils.isEmpty(jobName)) {
				 throw new PhrescoException("job name is empty");
			 }
			 List<CIJob> jobs = getJobs(appInfo);
			 if(CollectionUtils.isEmpty(jobs)) {
				 throw new PhrescoException("job list is empty!!!!!!!!");
			 }
			 log.info("Job list found!!!!!");
			 for (CIJob job : jobs) {
				 log.info("job list job Names => " + job.getName());
				 if (job.getName().equals(jobName)) {
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
