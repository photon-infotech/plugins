/**
 * Phresco Maven Plugin
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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

import fr.opensagres.xdocreport.utils.StringUtils;

/**
 * Goal which deploys the PHP project
 * 
 * @goal deploy
 * 
 */
public class PhrescoDeploy extends PhrescoAbstractMojo {
	
	
	 private static final String DEPLOY = Constants.PHASE_DEPLOY;

	/**
     * @parameter expression="${project.basedir}" required="true"
     * @readonly
     */
    protected File baseDir;
    
    /**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
	 * @parameter expression="${phresco.project.code}" required="true"
	 */
	protected String projectCode;
	
	 /**
     * @parameter expression="${interactive}" required="true"
     * @readonly
     */
    private boolean interactive;
    
	/**
     * @parameter expression="${moduleName}"
     * @readonly
     */
    protected String moduleName;
    
    /**
     * @parameter expression="${deployFromNexus}"
     * @readonly
     */
    protected String deployFromNexus;
    
    /**
	 * The project's remote repositories to use for the resolution of project dependencies.
	 * 
	 * @parameter default-value="${project.remoteProjectRepositories}"
	 * @readonly
	 *//*
    protected List<RemoteRepository> remoteRepos;
	
	*//**
	 * The entry point to Aether, i.e. the component doing all the work.
	 * 
	 * @component
	 *//*
    protected RepositorySystem repoSystem;

	*//**
	 * The current repository/network configuration of Maven.
	 * 
	 * @parameter default-value="${repositorySystemSession}"
	 * @readonly
	 *//*
    protected RepositorySystemSession repoSession;*/
	
	public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(baseDir.getPath());
        try {
        	String dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        	if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        		baseDir = new File(baseDir.getParent() +  File.separatorChar + dotPhrescoDirName);
        	}
        	if (StringUtils.isNotEmpty(dotPhrescoDirName) && StringUtils.isNotEmpty(moduleName)) {
        		baseDir = new File(baseDir.getParentFile().getPath() +  File.separatorChar + dotPhrescoDirName);
        	}
        	String infoFile = baseDir + File.separator + Constants.DEPLOY_INFO_FILE; 
        	if (StringUtils.isNotEmpty(moduleName)) {
        		infoFile = baseDir + File.separator + moduleName + File.separator + Constants.DEPLOY_INFO_FILE;
        	} 
        	Configuration configuration = null;
        	MojoProcessor processor = new MojoProcessor(new File(infoFile));
        	configuration = processor.getConfiguration(DEPLOY);
        	Map<String, Object> keyValues = new HashMap<String, Object>();
        	keyValues.put(PluginConstants.DEPLOY_FROM_NEXUS, deployFromNexus);
        	
        	if(interactive) {
        		configuration = getInteractiveConfiguration(configuration, processor, project,DEPLOY);
        	} 
            PhrescoPlugin plugin = getPlugin(getDependency(infoFile, DEPLOY));
            String processName = ManagementFactory.getRuntimeMXBean().getName();
    		String[] split = processName.split("@");
    		String processId = split[0].toString();
    		
    		Utility.writeProcessid(baseDir.getPath(), FrameworkConstants.DEPLOY, processId);
    		getLog().info("Writing Process Id...");
            plugin.deploy(configuration, getMavenProjectInfo(project, moduleName, keyValues));
        } catch (PhrescoException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
