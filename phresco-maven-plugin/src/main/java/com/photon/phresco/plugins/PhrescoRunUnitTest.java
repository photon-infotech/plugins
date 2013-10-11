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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Constants;

/**
 * 
 * @author suresh_ma
 * @goal unit-test
 *
 */
public class PhrescoRunUnitTest extends PhrescoAbstractMojo {

	private static final String UNIT_TEST = Constants.PHASE_UNIT_TEST;
	
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
     * @parameter expression="${interactive}" required="true"
     * @readonly
     */
    private boolean interactive;
    
    /**
     * @parameter expression="${moduleName}"
     * @readonly
     */
    protected String moduleName;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
    	try {
    		File infoFile = new File(baseDir + File.separator + Constants.UNIT_TEST_INFO_FILE);
    		if (StringUtils.isNotEmpty(moduleName)) {
        		infoFile = new File(baseDir + File.separator + moduleName + File.separator + Constants.UNIT_TEST_INFO_FILE);
        	} 
    		MojoProcessor processor = new MojoProcessor(infoFile);
        	Configuration configuration = processor.getConfiguration(UNIT_TEST);
        	if(interactive) {
        		configuration = getInteractiveConfiguration(configuration, processor, project,UNIT_TEST);
        	} 
    		if (infoFile.exists() && isGoalAvailable(infoFile.getPath(), UNIT_TEST) && getDependency(infoFile.getPath(), UNIT_TEST) != null) {
				PhrescoPlugin plugin = getPlugin(getDependency(infoFile.getPath(), UNIT_TEST));
		        plugin.runUnitTest(configuration, getMavenProjectInfo(project, moduleName));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.runUnitTest(configuration ,getMavenProjectInfo(project,moduleName));
			}
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
    }

}
