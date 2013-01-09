package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.util.Constants;

/**
 * 
 * @author suresh_ma
 * @goal unit-test
 *
 */
public class PhrescoRunUnitTest extends PhrescoAbstractMojo {

	private static final String UNIT_TEST = Constants.PHASE_UNIT_TEST;
	
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
    		File infoFile = new File(baseDir + File.separator + Constants.UNIT_TEST_INFO_FILE);
    		if (infoFile.exists() && isGoalAvailable(infoFile.getPath(), UNIT_TEST) && getDependency(infoFile.getPath(), UNIT_TEST) != null) {
				PhrescoPlugin plugin = getPlugin(getDependency(infoFile.getPath(), UNIT_TEST));
		        plugin.runUnitTest(getConfiguration(infoFile.getPath(), UNIT_TEST), getMavenProjectInfo(project));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.runUnitTest(null,getMavenProjectInfo(project));
			}
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
    }

}
