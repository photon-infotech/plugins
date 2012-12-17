package com.photon.phresco.plugins;

import java.io.File;

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

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			String infoFile = baseDir + File.separator + Constants.PERFORMANCE_TEST_INFO_FILE;
    		if (isGoalAvailable(infoFile, PERFORMANCE_TEST) && getPluginName(infoFile, PERFORMANCE_TEST) != null) {
				PhrescoPlugin plugin = getPlugin(getPluginName(infoFile, PERFORMANCE_TEST));
		        plugin.runPerformanceTest(getConfiguration(infoFile, PERFORMANCE_TEST), getMavenProjectInfo(project));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.runPerformanceTest(getConfiguration(infoFile, PERFORMANCE_TEST), getMavenProjectInfo(project));
			}
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
		
	}
}
