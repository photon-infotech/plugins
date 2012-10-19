package com.photon.phresco.plugins;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Utility;

/**
 * Goal which validate the code
 * 
 * @goal validate-code
 * 
 */
public class SonarCodeValidator extends PhrescoAbstractMojo implements PluginConstants {
	
	private static final String VALIDATE_CODE = "validate-code";

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
			if (getPluginName(baseDir, VALIDATE_CODE) != null) {
				PhrescoPlugin plugin = getPlugin(getPluginName(baseDir, VALIDATE_CODE));
		        plugin.validate(getConfiguration(baseDir, VALIDATE_CODE));
			} else {
				PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
		        plugin.validate(getConfiguration(baseDir, VALIDATE_CODE));
			}
	    } catch (PhrescoException e) {
	        throw new MojoExecutionException(e.getMessage(), e);
	    }
	}
}
