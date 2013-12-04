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
import com.photon.phresco.util.Constants;

/**
 * @author suresh_ma
 * @goal performance-test
 *
 */
public class PhrescoRunPerformanceTest extends PhrescoAbstractMojo {

	private static final String PERFORMANCE_TEST = Constants.PHASE_PERFORMANCE_TEST;
	
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
     * @parameter expression="${moduleName}"
     * @readonly
     */
    protected String moduleName;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			String dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        	if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        		baseDir = new File(baseDir.getParent() +  File.separatorChar + dotPhrescoDirName);
        	}
        	if (StringUtils.isNotEmpty(dotPhrescoDirName) && StringUtils.isNotEmpty(moduleName)) {
        		baseDir = new File(baseDir.getParentFile().getPath() +  File.separatorChar + dotPhrescoDirName);
        	}
			String infoFile = baseDir + File.separator + Constants.PERFORMANCE_TEST_INFO_FILE;
			if (StringUtils.isNotEmpty(moduleName)) {
				infoFile = baseDir + File.separator + moduleName + File.separator + Constants.PERFORMANCE_TEST_INFO_FILE;
			}
			System.out.println("infoFile====> " + infoFile);
    		if (isGoalAvailable(infoFile, PERFORMANCE_TEST) && getDependency(infoFile, PERFORMANCE_TEST) != null) {
				PhrescoPlugin plugin = getPlugin(getDependency(infoFile, PERFORMANCE_TEST));
		        plugin.runPerformanceTest(getConfiguration(infoFile, PERFORMANCE_TEST), getMavenProjectInfo(project, moduleName));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.runPerformanceTest(getConfiguration(infoFile, PERFORMANCE_TEST), getMavenProjectInfo(project, moduleName));
			}
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
		
	}
}
