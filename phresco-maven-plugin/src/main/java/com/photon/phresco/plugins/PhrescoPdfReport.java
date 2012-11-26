package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.util.Constants;

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
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		getLog().info("Executing pdf report generation ");
		try {
			if (isGoalAvailable(baseDir, PDF_REPORT) && getPluginName(baseDir, PDF_REPORT) != null) {
				PhrescoPlugin plugin = getPlugin(getPluginName(baseDir, PDF_REPORT));
		        plugin.generateReport(getConfiguration(baseDir, PDF_REPORT), getMavenProjectInfo(project));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.generateReport(getConfiguration(baseDir, PDF_REPORT), getMavenProjectInfo(project));
			}
	    } catch (PhrescoException e) {
	        throw new MojoExecutionException(e.getMessage(), e);
	    }
	}
}
