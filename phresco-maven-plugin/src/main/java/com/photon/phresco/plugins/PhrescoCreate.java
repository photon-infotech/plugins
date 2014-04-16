package com.photon.phresco.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ArtifactGroup;
import com.photon.phresco.commons.model.ArtifactGroupInfo;
import com.photon.phresco.commons.model.ArtifactInfo;
import com.photon.phresco.commons.model.Customer;
import com.photon.phresco.commons.model.DownloadInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.commons.model.Technology;
import com.photon.phresco.commons.model.TechnologyInfo;
import com.photon.phresco.commons.model.User;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectManager;
import com.photon.phresco.service.client.api.ServiceManager;

/**
 * Phresco Maven Plugin for create project
 * @goal create
 */
public class PhrescoCreate extends PhrescoAbstractMojo {
	
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
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		if(servicePropertyFile == null) {
			throw new MojoExecutionException("The Service Property File Or ProjectProperty File Should Not Be Empty ");
		}
		try {
			initProperty(servicePropertyFile, projectPropertyFile);
			serviceManager = getServiceManager();
			ProjectManager projectManager = PhrescoFrameworkFactory.getProjectManager();
			projectManager.create(createProjectInfo(), serviceManager);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}


	/**
	 * @throws PhrescoException
	 * @throws MojoExecutionException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
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
			List<DownloadInfo> listdatabas=serviceManager.getDownloads(customerByName.getId(), tech.getId(), "DATABASE", getPlatForm());
			if(CollectionUtils.isNotEmpty(listdatabas))
			{
				System.out.println("Do you want to add database to your Project?Y/N");
				br = new BufferedReader(new InputStreamReader(System.in));
				String DBYN=br.readLine();
				if(DBYN.equalsIgnoreCase("Y"))
				{
					System.out.println("Select Database : " );
					for (int i = 0; i < listdatabas.size(); i++) {				
						System.out.println(i + " " + listdatabas.get(i).getName());	
					}

					try {
						DownloadInfo downloadinfo = listdatabas.get(Integer.parseInt(br.readLine()));
						ArtifactGroup selectedGroup = downloadinfo.getArtifactGroup();
						List<ArtifactInfo> versioninfo = selectedGroup.getVersions();
						System.out.println("Select Version :");
						for (int i = 0; i < versioninfo.size(); i++) {
							System.out.println(i + " "+ versioninfo.get(i).getVersion());
						}
						ArtifactInfo artifactInfo = versioninfo.get(Integer.parseInt(br.readLine()));
						List<ArtifactGroupInfo> selectedDatabases = new ArrayList<ArtifactGroupInfo>();
						ArtifactGroupInfo agi = new ArtifactGroupInfo();
						agi.setArtifactGroupId(selectedGroup.getId());
						agi.setArtifactInfoIds(Arrays.asList(artifactInfo.getId()));
						selectedDatabases.add(agi);
						appInfo.setSelectedDatabases(selectedDatabases);
					} catch (Exception e) {
						throw new MojoExecutionException(e.getMessage());
					}

				}
			}
			List<DownloadInfo> listserver=serviceManager.getDownloads(customerByName.getId(), tech.getId(), "SERVER", getPlatForm());
			if(CollectionUtils.isNotEmpty(listserver))
			{
				System.out.println("Do you want to add server to your Project?Y/N");
				br = new BufferedReader(new InputStreamReader(System.in));
				String DBYN=br.readLine();
				if(DBYN.equalsIgnoreCase("Y"))
				{
					System.out.println("Select Server : " );
					for (int i = 0; i < listserver.size(); i++) {				
						System.out.println(i + " " + listserver.get(i).getName());	
					}

					try {
						DownloadInfo downloadinfo = listserver.get(Integer.parseInt(br.readLine()));
						ArtifactGroup selectedGroup = downloadinfo.getArtifactGroup();
						List<ArtifactInfo> versioninfo = selectedGroup.getVersions();
						System.out.println("Select Version :");
						for (int i = 0; i < versioninfo.size(); i++) {
							System.out.println(i + " "+ versioninfo.get(i).getVersion());
						}
						ArtifactInfo artifactInfo = versioninfo.get(Integer.parseInt(br.readLine()));
						List<ArtifactGroupInfo> selectedServers = new ArrayList<ArtifactGroupInfo>();
						ArtifactGroupInfo agi = new ArtifactGroupInfo();
						agi.setArtifactGroupId(selectedGroup.getId());
						agi.setArtifactInfoIds(Arrays.asList(artifactInfo.getId()));
						selectedServers.add(agi);
						appInfo.setSelectedServers(selectedServers);
					} catch (Exception e) {
						throw new MojoExecutionException(e.getMessage());
					}

				}
			}
				
				projectInfo.setAppInfos(Collections.singletonList(appInfo));
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private String getSelectedCustomerName() throws MojoExecutionException {
		String customerName = "";
		Map<Integer, String> customerMap = new HashMap<Integer, String>(); 
		User userInfo = serviceManager.getUserInfo();
		List<Customer> customers = null;
		try {
			if(userInfo == null) {
				customers = serviceManager.getCustomers();
			} else {
				customers = userInfo.getCustomers();
			}
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
			int read = Integer.parseInt(br.readLine());
			customerName = customerMap.get(read);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		return customerName;
	}
	
	private String getPlatForm() {
		String Osversion=System.getProperty("os.name");
		String processor=System.getProperty("os.arch");
		String processorbit=null;
		if(Osversion.contains("Windows 8")){
			Osversion="Windows";}
		if(processor.contains("64")){
			processorbit="64";}
		else{
			processorbit="86";}
		String platform=Osversion+processorbit;
		return platform;
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
