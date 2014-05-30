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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Constants;

/**
 * Goal which Analysis the WEB project
 * 
 * @goal zap-start
 * 
 */
public class PhrescoZapStart extends PhrescoAbstractMojo {
	/**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
	
	
	/**
	 * @parameter expression="${environmentName}"
	 * @readonly
	 */
	
	protected String environmentName;

	/**
	 * @parameter expression="${targetId}"
	 * @readonly
	 */

	protected String targetId;
	

	/**
	 * @parameter expression="${type}"
	 * @readonly
	 */

	protected String type;
	
	 /**
     * @parameter expression="${moduleName}"
     * @readonly
     */
    protected String moduleName;
    
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
     
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Log log = getLog();
			String dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
			if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        		baseDir = new File(baseDir.getParent() +  File.separatorChar + dotPhrescoDirName);
        	}
        	if (StringUtils.isNotEmpty(dotPhrescoDirName) && StringUtils.isNotEmpty(moduleName)) {
        		baseDir = new File(baseDir.getParentFile().getPath() +  File.separatorChar + dotPhrescoDirName);
        	}
        	File infoFile = new File(baseDir + File.separator +  Constants.DOT_PHRESCO_FOLDER  + File.separator +  Constants.ZAP_INFO_XML);
    		if (StringUtils.isNotEmpty(moduleName)) {
        		infoFile = new File(baseDir + File.separator + moduleName + File.separator + Constants.ZAP_INFO_XML);
        	} 
    		PhrescoPlugin plugin = getPlugin(getDependency(infoFile.getPath(), Constants.PHASE_ZAP_TEST), mavenSession, project, container);
    		MojoProcessor processor = new MojoProcessor(infoFile);
        	Configuration configuration = processor.getConfiguration(Constants.PHASE_ZAP_TEST);
        	plugin.zapTest(configuration, getMavenProjectInfo(project, moduleName));
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		
	}
}
