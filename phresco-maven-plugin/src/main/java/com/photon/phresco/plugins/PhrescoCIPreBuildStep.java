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
import org.sonatype.aether.util.*;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
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
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Form Phresco PhrescoCIPreBuildStep Plugin");
        getLog().info("Hello Phresco");
        getLog().info(baseDir.getPath());
        try {
        	String infoFile = baseDir + File.separator + Constants.CI_INFO_FILE;
        	if (StringUtils.isEmpty(jobName)) {
        		throw new MojoExecutionException("job name is empty. Pass job name.");
        	}
            PhrescoPlugin plugin = getPlugin(getPluginName(infoFile, PRE_BUILD_STEP));
            plugin.performCIPreBuildStep(jobName, getMavenProjectInfo(project));
        } catch (PhrescoException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
