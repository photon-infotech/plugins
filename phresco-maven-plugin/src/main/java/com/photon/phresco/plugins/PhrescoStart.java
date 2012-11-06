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
 * @goal start
 * 
 */
public class PhrescoStart extends PhrescoAbstractMojo {

	private static final String START = Constants.PHASE_RUNGAINST_SRC_START;

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
		try {
			PhrescoPlugin plugin = getPlugin(getPluginName(baseDir, START));
			plugin.startServer(getConfiguration(baseDir, START), getMavenProjectInfo(project));
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
