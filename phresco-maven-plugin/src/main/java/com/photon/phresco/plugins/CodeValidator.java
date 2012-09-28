package com.photon.phresco.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.util.Utility;

/**
 * Goal which validate the code
 * 
 * @goal validate-code
 * 
 */
public class CodeValidator extends PhrescoAbstractMojo implements PluginConstants {

	public void execute() throws MojoExecutionException, MojoFailureException {
		
		getLog().info("Executing Code Validation");
		executeCodeValidation();
	}

	private void executeCodeValidation() throws MojoExecutionException {
		getLog().info("Validating the Code...");
		StringBuilder sb = new StringBuilder();
		sb.append(SONAR_COMMAND);
		Utility.executeStreamconsumer(sb.toString());
	}
}
