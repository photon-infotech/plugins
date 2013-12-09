package com.photon.phresco.plugins;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ReactorManager;
import org.apache.maven.lifecycle.LifecycleExecutor;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginConfigurationException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginNotFoundException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;

/**
 * Goal which releases the project
 * 
 * @goal release
 * 
 */
public class PhrescoRelease extends AbstractMojo {
	
	/**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * The current Maven session.
     *
     * @parameter default-value="${session}"
     * @parameter required
     * @readonly
     */
    private MavenSession mavenSession;

    /**
     * The Maven BuildPluginManager component.
     *
     * @component
     * @required
     */
    private BuildPluginManager pluginManager;
    
    /**
     * @parameter expression="${releaseVersion}"
     * @readonly
     */
    protected String releaseVersion;
    
    /**
     * @parameter expression="${tag}"
     * @readonly
     */
    protected String tag;
    
    /**
     * @parameter expression="${developmentVersion}"
     * @readonly
     */
    protected String developmentVersion;
    
    /**
     * @parameter expression="${username}"
     * @readonly
     */
    protected String username;
    
    /**
     * @parameter expression="${password}"
     * @readonly
     */
    protected String password;
    
    /**
     * @parameter expression="${branchName}"
     * @readonly
     */
    protected String branchName;
    
    /**
     * @parameter expression="${component.org.apache.maven.lifecycle.LifecycleExecutor}"
     */
    private LifecycleExecutor lifecycleExecutor;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		if(StringUtils.isNotEmpty(branchName)) {
			createBranch();
			return;
		}
		prepareRelease();
		perFormRelease();
	}
	
	private void executePackage() {
        mavenSession.getRequest().setGoals(Arrays.asList( "install" ) );
        lifecycleExecutor.execute(mavenSession);
	}
	
	private void createBranch() throws MojoExecutionException {
		try {
			MojoDescriptor mojoDescriptor = getMojoDescriptor("branch");
			Xpp3Dom configuration = new Xpp3Dom("configuration");
			Xpp3Dom branchNameConf = new Xpp3Dom("branchName");
			branchNameConf.setValue(branchName);
			configuration.addChild(branchNameConf);
			Xpp3Dom usernameConf = new Xpp3Dom("username");
			usernameConf.setValue(username);
			configuration.addChild(usernameConf);
			Xpp3Dom passwordConf = new Xpp3Dom("password");
			passwordConf.setValue(password);
			configuration.addChild(passwordConf);
			executeMojo(mojoDescriptor, configuration);
		} catch (MojoExecutionException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void releaseClean() throws MojoExecutionException {
		MojoDescriptor mogoDescriptor = getMojoDescriptor("clean");
		Xpp3Dom configuration = new Xpp3Dom("configuration");
		executeMojo(mogoDescriptor, configuration);
	}

	private void rollbackRelease() throws MojoExecutionException {
		MojoDescriptor mogoDescriptor = getMojoDescriptor("rollback");
		Xpp3Dom configuration = new Xpp3Dom("configuration");
		try {
			executeMojo(mogoDescriptor, configuration);
		} catch (MojoExecutionException e) {
			releaseClean();
		}
	}

	private void perFormRelease() throws MojoExecutionException {
		try {
			MojoDescriptor mogoDescriptor = getMojoDescriptor("perform");
			Xpp3Dom configuration = new Xpp3Dom("configuration");
			Xpp3Dom usernameConf = new Xpp3Dom("username");
			usernameConf.setValue(username);
			configuration.addChild(usernameConf);
			Xpp3Dom passwordConf = new Xpp3Dom("password");
			passwordConf.setValue(password);
			configuration.addChild(passwordConf);
			executeMojo(mogoDescriptor, configuration);
		} catch (MojoExecutionException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void prepareRelease() throws MojoExecutionException {
		MojoDescriptor mogoDescriptor = getMojoDescriptor("prepare");
		Xpp3Dom configuration = new Xpp3Dom("configuration");
		Xpp3Dom releaseVersionConf = new Xpp3Dom("releaseVersion");
		releaseVersionConf.setValue(releaseVersion);
		configuration.addChild(releaseVersionConf);
		Xpp3Dom tagConf = new Xpp3Dom("tag");
		tagConf.setValue(tag);
		configuration.addChild(tagConf);
		Xpp3Dom developmentVersionConf = new Xpp3Dom("developmentVersion");
		developmentVersionConf.setValue(developmentVersion);
		configuration.addChild(developmentVersionConf);
		Xpp3Dom usernameConf = new Xpp3Dom("username");
		usernameConf.setValue(username);
		configuration.addChild(usernameConf);
		Xpp3Dom passwordConf = new Xpp3Dom("password");
		passwordConf.setValue(password);
		configuration.addChild(passwordConf);
		try {
			executeMojo(mogoDescriptor, configuration);
		} catch (MojoExecutionException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void executeMojo(MojoDescriptor mojoDescriptor, Xpp3Dom configuration) throws MojoExecutionException {
		configuration = Xpp3DomUtils.mergeXpp3Dom(configuration, convertPlexusConfiguration(mojoDescriptor.getMojoConfiguration()));
        MojoExecution exec = new MojoExecution(mojoDescriptor, configuration);
        try {
			pluginManager.executeMojo(mavenSession, exec);
		} catch (MojoFailureException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (MojoExecutionException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PluginConfigurationException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PluginManagerException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private MojoDescriptor getMojoDescriptor(String goal) throws MojoExecutionException {
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.apache.maven.plugins");
		plugin.setArtifactId("maven-release-plugin");
		plugin.setVersion("2.3.2");
		PluginDescriptor pluginDescriptor = null;
		try {
			pluginDescriptor = pluginManager.loadPlugin(plugin, project.getRemotePluginRepositories(),
					mavenSession.getRepositorySession());
		} catch (PluginNotFoundException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PluginResolutionException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PluginDescriptorParsingException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (InvalidPluginDescriptorException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		return pluginDescriptor.getMojo(goal);
	}
	
	 private Xpp3Dom convertPlexusConfiguration(PlexusConfiguration config) {
	    Xpp3Dom xpp3DomElement = new Xpp3Dom(config.getName());
	    xpp3DomElement.setValue(config.getValue());
	    for (String name : config.getAttributeNames()) {
	      xpp3DomElement.setAttribute(name, config.getAttribute(name));
	    }
	    for (PlexusConfiguration child : config.getChildren()) {
	      xpp3DomElement.addChild(convertPlexusConfiguration(child));
	    }
	    return xpp3DomElement;
	 }
	 
}
