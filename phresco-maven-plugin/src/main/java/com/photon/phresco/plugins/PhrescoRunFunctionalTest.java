package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;

/**
 * @author suresh_ma
 *
 */
public class PhrescoRunFunctionalTest extends PhrescoAbstractMojo {

	private static final String FUNCTIONAL_TEST = "fuctional-test";
	
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
    		if (getPluginName(baseDir, FUNCTIONAL_TEST) != null) {
				PhrescoPlugin plugin = getPlugin(getPluginName(baseDir, FUNCTIONAL_TEST));
		        plugin.runFunctionalTest(getConfiguration(baseDir, FUNCTIONAL_TEST), getMavenProjectInfo(project));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.runFunctionalTest(getConfiguration(baseDir, FUNCTIONAL_TEST), getMavenProjectInfo(project));
			}
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
		
	}
}
