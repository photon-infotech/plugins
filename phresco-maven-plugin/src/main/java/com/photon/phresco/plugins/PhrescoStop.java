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
 * @goal stop
 * 
 */
public class PhrescoStop extends PhrescoAbstractMojo {

	private static final String STOP = Constants.PHASE_RUNGAINST_SRC_STOP;

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
			String infoFile = baseDir + File.separator + Constants.STOP_INFO_FILE;
			PhrescoPlugin plugin = getPlugin(getDependency(infoFile, STOP));
			plugin.stopServer(getMavenProjectInfo(project));
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
