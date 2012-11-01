package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;

/**
 * 
 * @author suresh_ma
 * @goal unit-test
 *
 */
public class PhrescoRunUnitTest extends PhrescoAbstractMojo {

	private static final String UNIT_TEST = "unit-test";
	
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
    		if (getPluginName(baseDir, UNIT_TEST) != null) {
				PhrescoPlugin plugin = getPlugin(getPluginName(baseDir, UNIT_TEST));
		        plugin.runUnitTest(getConfiguration(baseDir, UNIT_TEST), getMavenProjectInfo(project));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.runUnitTest(getConfiguration(baseDir, UNIT_TEST),getMavenProjectInfo(project));
			}
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
    }

}
