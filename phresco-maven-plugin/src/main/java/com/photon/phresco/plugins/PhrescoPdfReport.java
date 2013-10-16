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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

/**
 * Goal which validate the code
 * 
 * @goal pdf-report
 * 
 */
public class PhrescoPdfReport extends PhrescoAbstractMojo implements PluginConstants {
	
	private static final String PDF_REPORT = Constants.PHASE_PDF_REPORT;

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
		getLog().info("Executing pdf report generation ");
		try {
			String infoFile = baseDir + File.separator + Constants.PDF_REPORT_INFO_FILE;
        	if (StringUtils.isNotEmpty(moduleName)) {
        		infoFile = baseDir + File.separator + moduleName + File.separator + Constants.PDF_REPORT_INFO_FILE;
        	}
			Configuration configuration = getConfiguration(infoFile, PDF_REPORT);
			// To kill the process
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String testType = configs.get("testType");
			if (StringUtils.isNotEmpty(testType)) {
				String processName = ManagementFactory.getRuntimeMXBean().getName();
	    		String[] split = processName.split("@");
	    		String processId = split[0].toString();
	    		Utility.writeProcessid(baseDir.getPath(), testType+"PdfReport", processId);
			}
			if (isGoalAvailable(infoFile, PDF_REPORT) && getDependency(infoFile, PDF_REPORT) != null) {
				PhrescoPlugin plugin = getPlugin(getDependency(infoFile, PDF_REPORT));
		        plugin.generateReport(configuration, getMavenProjectInfo(project, moduleName));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.generateReport(configuration, getMavenProjectInfo(project, moduleName));
			}
	    } catch (PhrescoException e) {
	        throw new MojoExecutionException(e.getMessage(), e);
	    }
	}
}
