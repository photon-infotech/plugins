package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.api.PhrescoPlugin2;

/**
 * 
 * @goal content-convertor
 * 
 */
public class PhrescoContentConvertor extends PhrescoAbstractMojo {
	
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
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
    	try {
    		File infoFile = new File(baseDir + File.separator + ".phresco/phresco-content-info.xml");
    		if (infoFile.exists() && isGoalAvailable(infoFile.getPath(), "content-convertor") && getDependency(infoFile.getPath(), "content-convertor") != null) {
				PhrescoPlugin plugin = getPlugin(getDependency(infoFile.getPath(), "content-convertor"));
				if(plugin instanceof PhrescoPlugin2) {
    				PhrescoPlugin2 plugin2 = (PhrescoPlugin2) plugin;
    				plugin2.contentConvertor(getMavenProjectInfo(project));
    			}
			} 
    	} catch (PhrescoException e) {
    		throw new MojoExecutionException(e.getMessage(), e);
    	}
    }
}
