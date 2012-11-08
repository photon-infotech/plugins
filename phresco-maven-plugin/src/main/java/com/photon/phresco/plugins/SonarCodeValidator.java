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
 * @goal validate-code
 * 
 */
public class SonarCodeValidator extends PhrescoAbstractMojo implements PluginConstants {
	
	private static final String VALIDATE_CODE = Constants.PHASE_VALIDATE_CODE;

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
		
		getLog().info("Executing Code Validation");
		try {
			if (isGoalAvailable(baseDir, VALIDATE_CODE) && getPluginName(baseDir, VALIDATE_CODE) != null) {
				PhrescoPlugin plugin = getPlugin(getPluginName(baseDir, VALIDATE_CODE));
		        plugin.validate(getConfiguration(baseDir, VALIDATE_CODE), getMavenProjectInfo(project));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.validate(getConfiguration(baseDir, VALIDATE_CODE), getMavenProjectInfo(project));
			}
	    } catch (PhrescoException e) {
	        throw new MojoExecutionException(e.getMessage(), e);
	    }
	}
}
