package com.photon.phresco.plugins;

import java.io.File;
import java.lang.reflect.Constructor;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;

public abstract class PhrescoAbstractMojo extends AbstractMojo {
    
    public PhrescoPlugin getPlugin(String pluginClassName) throws PhrescoException {
        //Caching not needed since it will be triggered as a new process every time from the maven
    	return constructClass(pluginClassName);
    }

    private PhrescoPlugin constructClass(String pluginClassName) throws PhrescoException {
        try {
            Log log = getLog();
            Class<PhrescoPlugin> pluginClass = (Class<PhrescoPlugin>) Class.forName(pluginClassName);
            Constructor<PhrescoPlugin> constructor = pluginClass.getDeclaredConstructor(Log.class);
            return constructor.newInstance(log);
        } catch (Exception e) {
            throw new PhrescoException(e);
        }
    }
    
    protected Configuration getConfiguration(String infoFile, String goal) throws PhrescoException {
        MojoProcessor processor = new MojoProcessor(new File(infoFile));
        return processor.getConfiguration(goal);
    }
    
    protected MavenProjectInfo getMavenProjectInfo(MavenProject project) {
        MavenProjectInfo mavenProjectInfo = new MavenProjectInfo();
    	mavenProjectInfo.setBaseDir(project.getBasedir());
        mavenProjectInfo.setProject(project);
        mavenProjectInfo.setProjectCode(project.getBasedir().getName());
        return mavenProjectInfo;
    }
    
    protected String getPluginName(String infoFile, String goal) throws PhrescoException {
    	MojoProcessor processor = new MojoProcessor(new File(infoFile));
    	return processor.getImplementationClassName(goal);
    }
    
    protected boolean isGoalAvailable(String infoFile, String goal) throws PhrescoException {
    	MojoProcessor processor = new MojoProcessor(new File(infoFile));
    	return processor.isGoalAvailable(goal);
	}
}
