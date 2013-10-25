package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Constants;

/**
 * 
 * @author suresh_ma
 * @goal integration-test
 *
 */
public class PhrescoRunIntegrationTest extends PhrescoAbstractMojo {

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

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			File infoFile = new File(baseDir + File.separator + Constants.INTEGRATION_TEST_INFO_FILE);
			MojoProcessor processor = new MojoProcessor(infoFile);
			Configuration configuration = processor.getConfiguration(Constants.PHASE_INTEGRATION_TEST);
			PhrescoPlugin plugin = new PhrescoBasePlugin(getLog());
	        plugin.runIntegrationTest(configuration ,getMavenProjectInfo(project));
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
