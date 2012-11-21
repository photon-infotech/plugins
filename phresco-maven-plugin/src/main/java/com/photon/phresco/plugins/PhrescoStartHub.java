package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.util.Constants;

/**
 * Goal which deploys the PHP project
 * 
 * @goal start-hub
 * 
 */
public class PhrescoStartHub extends PhrescoAbstractMojo {
	
	/**
     * @parameter expression="${project.basedir}" required="true"
     * @readonly
     */
    protected File baseDir;
    
    /**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
	 * @parameter expression="${phresco.project.code}" required="true"
	 */
	protected String projectCode;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(baseDir.getPath());
        try {
        	PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
			plugin.startHub(getConfiguration(baseDir, Constants.PHASE_START_HUB),getMavenProjectInfo(project));
        } catch (PhrescoException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
