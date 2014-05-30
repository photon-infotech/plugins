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

import java.io.*;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.*;
import org.apache.maven.project.*;
import org.codehaus.plexus.PlexusContainer;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.util.Constants;

/**
 * Phresco Maven Plugin for executing ci prebuild step command of the plugins
 * @goal report
 */

public class PhrescoReport extends PhrescoAbstractMojo {
	private static final String REPORT = "report";
	
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
        getLog().info("Form Phresco PhrescoCIPreBuildStep Plugin");
        getLog().info(baseDir.getPath());
        try {
        	String infoFile = baseDir + File.separator + Constants.REPORT_INFO_FILE;
        	getLog().info("Report generation started ");
            PhrescoPlugin plugin = getPlugin(getDependency(infoFile, REPORT), mavenSession, project, container);
            plugin.generateReport(getConfiguration(infoFile, REPORT), getMavenProjectInfo(project));
        } catch (PhrescoException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

}
