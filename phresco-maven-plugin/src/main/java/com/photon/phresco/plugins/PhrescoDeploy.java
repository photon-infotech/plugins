package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Constants;

/**
 * Goal which deploys the PHP project
 * 
 * @goal deploy
 * 
 */
public class PhrescoDeploy extends PhrescoAbstractMojo {
	
	
	 private static final String DEPLOY = Constants.PHASE_DEPLOY;

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
        getLog().info("Form Phresco Plugin");
        getLog().info("Hello Phresco");
        getLog().info(baseDir.getPath());
        //Read the selected info file
        //Convert it into Java Bean Objects using JAXB
        //Find the implementation class based on the technology
        //execute deploy method
        try {
        	String infoFile = baseDir + File.separator + Constants.DEPLOY_INFO_FILE; 
            PhrescoPlugin plugin = getPlugin(getPluginName(infoFile, DEPLOY));
            plugin.deploy(getConfiguration(infoFile, DEPLOY), getMavenProjectInfo(project));
        } catch (PhrescoException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
