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
 * @goal test
 *
 */
public class PhrescoRunUnitTest extends PhrescoAbstractMojo {

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
    		PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
    		plugin.runUnitTest();
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
    }

}
