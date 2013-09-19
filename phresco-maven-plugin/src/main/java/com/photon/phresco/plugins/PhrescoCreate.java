package com.photon.phresco.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.Customer;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.commons.model.Technology;
import com.photon.phresco.commons.model.TechnologyInfo;
import com.photon.phresco.commons.model.User;
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
     * @parameter expression="${project.basedir}" required="true"
     * @readonly
     */
    private File baseDir;
    
    /**
     * @parameter expression="${project.properties}" required="false"
     * @readonly
     */
    private String projectPropertyFile;
    
    /**
     * @parameter expression="${service.properties}" required="true"
     * @readonly
     */
    private String servicePropertyFile;
    
    /**
     * @parameter expression="${interactive}" required="true"
     * @readonly
     */
    private boolean interactive;
    
    private ServiceManager serviceManager;
    private Properties serverProperties;
    private Properties projectProperties;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		initProperty();
		try {
			serviceManager = PhrescoFrameworkFactory.getServiceManager(getServiceContext());
			ProjectManager projectManager = PhrescoFrameworkFactory.getProjectManager();
			projectManager.create(createProjectInfo(), serviceManager);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private ServiceContext getServiceContext() {
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.put("phresco.service.url", serverProperties.get("phresco.service.url"));
		serviceContext.put("phresco.service.password", serverProperties.get("phresco.service.password"));
		serviceContext.put("phresco.service.username", serverProperties.get("phresco.service.username"));
		return serviceContext;
	}
	
	private void initProperty() throws MojoExecutionException {
		if(servicePropertyFile == null) {
			throw new MojoExecutionException("The Service Property File Or ProjectProperty File Should Not Be Empty ");
		}
		try {
			serverProperties = new Properties();
			projectProperties = new Properties();
			File serverFile = new File(servicePropertyFile);
			if(!serverFile.exists()) {
				serverFile = new File(baseDir, servicePropertyFile);
				if(!serverFile.exists()) {
					throw new MojoExecutionException("Server Property File Not Exists");
				}
			}
			if(!interactive) {
				File projectPropFile = new File(projectPropertyFile);
				if(!projectPropFile.exists()) {
					projectPropFile = new File(baseDir, projectPropertyFile);
					if(!projectPropFile.exists()) {
						throw new MojoExecutionException("Project Property File Not Exists");
					}
				}
				projectProperties.load(new FileInputStream(projectPropFile));
			}
			serverProperties.load(new FileInputStream(serverFile));
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private ProjectInfo createProjectInfo() throws MojoExecutionException, PhrescoException {
		ProjectInfo projectInfo = new ProjectInfo();
		if(interactive) {
			createInteractiveProjectInfo(projectInfo);
			return projectInfo;
		}
		projectInfo.setCustomerIds(Collections.singletonList(projectProperties.getProperty("customer")));
		projectInfo.setName((String)projectProperties.get("project.name"));
		projectInfo.setNoOfApps(1);
		projectInfo.setProjectCode((String)projectProperties.get("project.name"));
		projectInfo.setVersion((String)projectProperties.get("project.version"));
		ApplicationInfo appInfo = new ApplicationInfo();
		appInfo.setAppDirName((String)projectProperties.get("app.name"));
		appInfo.setCode((String)projectProperties.get("app.name"));
		appInfo.setName((String)projectProperties.get("app.name"));
		appInfo.setVersion((String)projectProperties.get("project.version"));
		TechnologyInfo techInfo = new TechnologyInfo();
		Technology tech = serviceManager.getTechnologyByName((String)projectProperties.get("app.technology"));
		techInfo.setId(tech.getId());
		techInfo.setVersion((String)projectProperties.get("app.technology.version"));
		appInfo.setTechInfo(techInfo);
		projectInfo.setAppInfos(Collections.singletonList(appInfo));
		return projectInfo;
	}

	private void createInteractiveProjectInfo(ProjectInfo projectInfo) throws MojoExecutionException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Select Customer : " );
			String customerName = getSelectedCustomerName();
			Customer customerByName = serviceManager.getCustomerByName(customerName);
			projectInfo.setCustomerIds(Collections.singletonList(customerByName.getId()));
			System.out.println("Enter Project Name : " );
			br = new BufferedReader(new InputStreamReader(System.in));
			String projectName = br.readLine();
			projectInfo.setName(projectName);
			projectInfo.setNoOfApps(1);
			projectInfo.setProjectCode(projectName);
			System.out.println("Enter Project Version : " );
			br = new BufferedReader(new InputStreamReader(System.in));
			String projectVersion = br.readLine();
			projectInfo.setVersion(projectVersion);
			ApplicationInfo appInfo = new ApplicationInfo();
			System.out.println("Enter Application Name : " );
			br = new BufferedReader(new InputStreamReader(System.in));
			String appName = br.readLine();
			appInfo.setAppDirName(appName);
			appInfo.setCode(appName);
			appInfo.setName(appName);
			System.out.println("Enter Application Version : " );
			br = new BufferedReader(new InputStreamReader(System.in));
			appInfo.setVersion(br.readLine());
			TechnologyInfo techInfo = new TechnologyInfo();
			System.out.println("Select Technology : " );
			Technology tech = getSelectedTechnology(customerByName.getId());
			techInfo.setName(tech.getName());
			techInfo.setId(tech.getId());
			List<String> techVersions = tech.getTechVersions();
			if (CollectionUtils.isNotEmpty(techVersions)) {
				System.out.println("Select Technolog Version : " );
				br = new BufferedReader(new InputStreamReader(System.in));
				StringBuilder builder = new StringBuilder();
					for (int i = 0; i < techVersions.size(); i++) {
						builder.append( i  + " " + techVersions.get(i));
						if(i != techVersions.size()-1) {
							builder.append("\n");
						}
				}
				System.out.println(builder);
				int read = Integer.parseInt(br.readLine());
				techInfo.setVersion(techVersions.get(read));
			}
				appInfo.setTechInfo(techInfo);
				projectInfo.setAppInfos(Collections.singletonList(appInfo));
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private String getSelectedCustomerName() {
		String customerName = "";
		Map<Integer, String> customerMap = new HashMap<Integer, String>(); 
		User userInfo = serviceManager.getUserInfo();
		if(userInfo == null) {
			return customerName;
		}
		List<Customer> customers = userInfo.getCustomers();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < customers.size(); i++) {
			builder.append( i  + " " + customers.get(i).getName());
			if(i != customers.size()-1) {
				builder.append("\n");
			}
			customerMap.put(i, customers.get(i).getName());
		}
		System.out.println(builder.toString());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			int read = Integer.parseInt(br.readLine());
			customerName = customerMap.get(read);
		} catch (IOException e) {
		}
		return customerName;
	}
	
	private Technology getSelectedTechnology(String customerId) throws MojoExecutionException {
		Technology tech = null;
		Map<Integer, Technology> techMap = new HashMap<Integer, Technology>();
		try {
			List<Technology> techs = serviceManager.getTechnologyByCustomer(customerId);
			if(CollectionUtils.isEmpty(techs)) {
				throw new MojoExecutionException("Technology not available for the given customer");
			}
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < techs.size(); i++) {
				builder.append( i  + " " + techs.get(i).getName());
				if(i != techs.size()-1) {
					builder.append("\n");
				}
				techMap.put(i, techs.get(i));
			}
			System.out.println(builder.toString());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int read = Integer.parseInt(br.readLine());
			tech = techMap.get(read);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		return tech;
	}
}
