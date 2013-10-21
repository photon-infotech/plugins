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

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.util.StringUtils;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.CIPlugin;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.util.Constants;

/**
 * Phresco Maven Plugin for executing ci prebuild step command of the plugins
 * @goal ci-prestep
 */
public class PhrescoCIPreBuildStep extends PhrescoAbstractMojo {
    
    private static final String PRE_BUILD_STEP = "CIPrebuildStep";

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
    
	/**
	 * @parameter expression ="${jobName}"
	 * 
	 * this job name is passed from jenkins
	 */
	private String jobName;
	
	/**
	 * @parameter expression ="${goal}"
	 * 
	 * this goal is passed from jenkins
	 */
	private String goal;
	
	/**
	 * @parameter expression ="${phase}"
	 * 
	 * this phase is passed from jenkins
	 */
	private String phase;
	
	/**
	 * @parameter expression ="${creationType}"
	 * 
	 * this creationType is passed from jenkins
	 */
	private String creationType;
	
	/**
	 * @parameter expression ="${id}"
	 * 
	 * this id is passed from jenkins
	 */
	private String id;
	
	/**
	 * @parameter expression ="${continuousDeliveryName}"
	 * 
	 * this continuousDeliveryName is passed from jenkins
	 */
	private String continuousDeliveryName;
	
	
	/**
	 * @parameter expression ="${moduleName}"
	 * 
	 * this continuousDeliveryName is passed from jenkins
	 */
	private String moduleName;
	
	
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Form Phresco PhrescoCIPreBuildStep Plugin");
        getLog().info(baseDir.getPath());
        getLog().info("Phase " + phase);
        getLog().info("goal " + goal);
        getLog().info("creationType " + creationType);
        getLog().info("id " + id);
        getLog().info("continuousDeliveryName " + continuousDeliveryName);
        getLog().info("moduleName " + moduleName);
        try {
//        	String infoFile = baseDir + File.separator + Constants.CI_INFO_FILE;
        	String infoFile = baseDir + File.separator + ".phresco" + File.separator +"phresco-ci-" + phase + "-info.xml";
        	if (StringUtils.isEmpty(jobName)) {
        		throw new MojoExecutionException("job name is empty. Pass job name.");
        	}
        	
            PhrescoPlugin plugin = getPlugin(getDependency(infoFile, PRE_BUILD_STEP));
            if(plugin instanceof CIPlugin) {
            	CIPlugin ciPlugin = (CIPlugin) plugin;
            	ciPlugin.performCIPreBuildStep(jobName, goal, phase, creationType, id, continuousDeliveryName, moduleName, getMavenProjectInfo(project));
			}
        } catch (PhrescoException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
