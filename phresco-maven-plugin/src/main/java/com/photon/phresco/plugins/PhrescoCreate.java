package com.photon.phresco.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.commons.model.Technology;
import com.photon.phresco.commons.model.TechnologyInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectManager;
import com.photon.phresco.service.client.api.ServiceContext;
import com.photon.phresco.service.client.api.ServiceManager;

/**
 * Phresco Maven Plugin for create project
 * @goal create
 */
public class PhrescoCreate extends AbstractMojo {
	
	/**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
     * @parameter expression="${project.basedir}" required="true"
     * @readonly
     */
    protected File baseDir;
    
    private ServiceManager serviceManager;
    private Properties properties;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		initProperty();
		try {
			serviceManager = PhrescoFrameworkFactory.getServiceManager(getServiceContext());
			ProjectManager projectManager = PhrescoFrameworkFactory.getProjectManager();
			projectManager.create(createProjectInfo(), serviceManager);
		} catch (PhrescoException e) {
			System.out.println("**************************** Got Exception **********************");
			e.printStackTrace();
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private ServiceContext getServiceContext() {
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.put("phresco.service.url", properties.get("phresco.service.url"));
		serviceContext.put("phresco.service.password", properties.get("phresco.service.password"));
		serviceContext.put("phresco.service.username", properties.get("phresco.service.username"));
		return serviceContext;
	}
	
	private void initProperty() throws MojoExecutionException {
		try {
			properties = new Properties();
			properties.load(new FileInputStream(new File(baseDir, "createproject.properties")));
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private ProjectInfo createProjectInfo() throws MojoExecutionException, PhrescoException {
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.setCustomerIds(Collections.singletonList((String) properties.get("customer")));
		projectInfo.setName((String)properties.get("project.name"));
		projectInfo.setNoOfApps(1);
		projectInfo.setProjectCode((String)properties.get("project.name"));
		projectInfo.setVersion((String)properties.get("project.version"));
		ApplicationInfo appInfo = new ApplicationInfo();
		appInfo.setAppDirName((String)properties.get("app.name"));
		appInfo.setCode((String)properties.get("app.name"));
		appInfo.setName((String)properties.get("app.name"));
		appInfo.setVersion((String)properties.get("project.version"));
		TechnologyInfo techInfo = new TechnologyInfo();
		Technology tech = serviceManager.getTechnologyByName((String)properties.get("app.technology"));
		System.out.println("Tech is  " + tech);
		techInfo.setId(tech.getId());
		techInfo.setVersion((String)properties.get("app.technology.version"));
		appInfo.setTechInfo(techInfo);
		projectInfo.setAppInfos(Collections.singletonList(appInfo));
		return projectInfo;
	}
}
