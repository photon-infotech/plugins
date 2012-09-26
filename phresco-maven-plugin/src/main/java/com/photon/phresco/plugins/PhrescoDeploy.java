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

/**
 * Goal which deploys the PHP project
 * 
 * @goal deploy
 * 
 */
public class PhrescoDeploy extends PhrescoAbstractMojo {
	
	
	 private static final String DEPLOY = "deploy";

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
            PhrescoPlugin plugin = getPlugin(getPluginName());
            plugin.deploy(getConfiguration(), getMavenProjectInfo());
        } catch (PhrescoException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
	
	private Configuration getConfiguration() throws PhrescoException {
        MojoProcessor processor = new MojoProcessor(new File(baseDir, PHRESCO_PLUGIN_INFO_XML));
        return processor.getConfiguration(DEPLOY);
    }
    
    private MavenProjectInfo getMavenProjectInfo() {
        MavenProjectInfo mavenProjectInfo = new MavenProjectInfo();
        mavenProjectInfo.setBaseDir(baseDir);
        mavenProjectInfo.setProject(project);
        mavenProjectInfo.setProjectCode(projectCode);
        return mavenProjectInfo;
    }
    
    private String getPluginName() throws PhrescoException {
    	MojoProcessor processor = new MojoProcessor(new File(baseDir, PHRESCO_PLUGIN_INFO_XML));
    	return processor.getImplementationClassName(DEPLOY);
    }

}
