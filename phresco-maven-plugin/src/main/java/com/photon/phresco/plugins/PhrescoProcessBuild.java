/**
 * Phresco Maven Plugin
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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.api.PhrescoPlugin2;
import com.photon.phresco.util.Constants;

/**
 * 
 * @goal process-build
 * 
 */
public class PhrescoProcessBuild extends PhrescoAbstractMojo {
	
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
     * <p>We can't autowire strongly typed RepositorySystem from Aether because it may be Sonatype (Maven 3.0.x)
     * or Eclipse (Maven 3.1.x/3.2.x) version, so we switch to service locator by autowiring entire {@link PlexusContainer}</p>
     *
     * <p>It's a bit of a hack but we have not choice when we want to be usable both in Maven 3.0.x and 3.1.x/3.2.x</p>
     *
     * @component
     * @required
     * @readonly
     */
     protected PlexusContainer container;
     
     /**
      * The current Maven session.
      *
      * @parameter default-value="${session}"
      * @parameter required
      * @readonly
      */
     private MavenSession mavenSession;
     
    public void execute() throws MojoExecutionException, MojoFailureException {
    	try {
    		File infoFile = new File(baseDir + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + Constants.PROCESS_BUILD_INFO_FILE);
    		if (infoFile.exists() && isGoalAvailable(infoFile.getPath(), Constants.PHASE_PROCESS_BUILD) && getDependency(infoFile.getPath(), Constants.PHASE_PROCESS_BUILD) != null) {
    			PhrescoPlugin plugin = getPlugin(getDependency(infoFile.getPath(), Constants.PHASE_PROCESS_BUILD), mavenSession, project, container);
    			if(plugin instanceof PhrescoPlugin2) {
    				PhrescoPlugin2 plugin2 = (PhrescoPlugin2) plugin;
    				plugin2.processBuild(getConfiguration(infoFile.getPath(), Constants.PHASE_PROCESS_BUILD), getMavenProjectInfo(project));
    			}
    		} 
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}		
    }
}
