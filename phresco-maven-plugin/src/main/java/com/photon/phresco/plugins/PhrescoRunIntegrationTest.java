package com.photon.phresco.plugins;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

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
			
			String processName = ManagementFactory.getRuntimeMXBean().getName();
	 		String[] split = processName.split("@");
	 		String processId = split[0].toString();
	 		
	 		Utility.writeProcessid(baseDir.getPath(), PluginConstants.INTEGRATION, processId);
	 		getLog().info("Writing Process Id...");
			
	        plugin.runIntegrationTest(configuration ,getMavenProjectInfo(project));
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
