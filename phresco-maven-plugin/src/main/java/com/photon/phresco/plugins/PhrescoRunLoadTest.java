package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.util.Constants;

/**
 * @author jagadeesh_r
 * @goal load-test
 *
 */
public class PhrescoRunLoadTest extends PhrescoAbstractMojo {

	private static final String LOAD_TEST = Constants.PHASE_LOAD_TEST;
	
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
			String infoFile = baseDir + File.separator + Constants.LOAD_TEST_INFO_FILE;
    		if (isGoalAvailable(infoFile, LOAD_TEST) && getPluginName(infoFile, LOAD_TEST) != null) {
				PhrescoPlugin plugin = getPlugin(getPluginName(infoFile, LOAD_TEST));
		        plugin.runLoadTest(getConfiguration(infoFile, LOAD_TEST), getMavenProjectInfo(project));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.runLoadTest(getConfiguration(infoFile, LOAD_TEST), getMavenProjectInfo(project));
			}
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
		
	}
}
