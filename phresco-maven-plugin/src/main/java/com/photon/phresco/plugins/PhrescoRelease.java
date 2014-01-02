package com.photon.phresco.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.LifecycleExecutor;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
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
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.repository.RemoteRepository;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.model.Model.PluginRepositories;

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
    protected MavenProject project;
    
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
     * @parameter expression="${branchName}"
     * @readonly
     */
    protected String branchName;
    
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
     * @parameter expression="${message}"
     * @readonly
     */
    protected String message;
    
    /**
     * @parameter expression="${appDirName}"
     * @readonly
     */
    protected String appDirName;
    
    /**
     * @parameter expression="${component.org.apache.maven.lifecycle.LifecycleExecutor}"
     */
    private LifecycleExecutor lifecycleExecutor;
    
    /** SCM Manager component to be injected.
     * @component
     */
    private ScmManager scmManager;
    
    private File sourceCheckOutDir;
    private File phresoCheckOutDir;
    private File testCheckOutDir;
    private MavenProject mavenProject;
    private boolean isSplittedProject;
    private boolean hasSplitDotPhresco;
    private boolean hasSplitTest;
    private String phrescorepoUrl;
    private String testRepoURL;
    private File sourcePomFile;
    private File dotPhrescoPomFile;
    private File testPomFile;
    private String sourceRepoURL;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		sourceRepoURL = project.getScm().getDeveloperConnection();
		checkoutApplication();
		prepareRelease();
	}
	
	private void checkoutApplication() throws MojoExecutionException {
		String phrescoTemp = Utility.getPhrescoTemp();
		sourceCheckOutDir = new File(phrescoTemp, UUID.randomUUID().toString());
		sourceCheckOutDir.mkdirs();
		checkout(sourceRepoURL, sourceCheckOutDir);
			try {
				sourcePomFile = Utility.getPomFileLocation(Utility.getProjectHome().concat(appDirName), "");
			} catch (PhrescoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initMavenProject(sourcePomFile);
			isSplitted();
		if(isSplittedProject && hasSplitDotPhresco) {
			phresoCheckOutDir = new File(phrescoTemp, UUID.randomUUID().toString());
			phresoCheckOutDir.mkdirs();
			checkout(phrescorepoUrl, phresoCheckOutDir);
			dotPhrescoPomFile = new File(phresoCheckOutDir, "phresco-pom.xml");
		}
		if(isSplittedProject && hasSplitTest) {
			testCheckOutDir = new File(phrescoTemp, UUID.randomUUID().toString());
			testCheckOutDir.mkdirs();
			checkout(testRepoURL, testCheckOutDir);
			testPomFile = new File(testCheckOutDir, "pom.xml");
		}
	}

	private void checkout(String repoURL, File checkOutdir) throws MojoExecutionException {
		ScmRepository scmRepository;
		try {
			scmRepository = scmManager.makeScmRepository(repoURL);
			scmManager.checkOut(scmRepository, new ScmFileSet(checkOutdir), getScmVersion());
		} catch (ScmRepositoryException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (NoSuchScmProviderException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (ScmException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private MavenProject initMavenProject(File pomFile) throws MojoExecutionException {
    	MavenXpp3Reader mavenreader = new MavenXpp3Reader();
    	Model model;
		try {
			FileReader reader = new FileReader(pomFile);
			model = mavenreader.read(reader);
			model.setPomFile(pomFile);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (XmlPullParserException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		return mavenProject = new MavenProject(model);
	}
	
	private void isSplitted() {
		phrescorepoUrl = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_PHRESCO_REPO_URL);
		if(StringUtils.isNotEmpty(phrescorepoUrl)) {
			isSplittedProject = true;
			hasSplitDotPhresco = true;
		}
		testRepoURL = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_TEST_REPO_URL);
		if(StringUtils.isNotEmpty(testRepoURL)) {
			isSplittedProject = true;
			hasSplitTest = true;
		}
	}
	
	private ScmVersion getScmVersion() {
		return new ScmVersion() {
			
			public void setName(String paramString) {
			}
			
			public String getType() {
				String type = "";
				if(StringUtils.isNotEmpty(branchName)) {
					type = "branch";
				}
				return type;
			}
			
			public String getName() {
				String name = "";
				if(StringUtils.isNotEmpty(branchName)) {
					name = branchName;
				}
				return name;
			}
		};
	}
	
	private void perFormRelease() throws MojoExecutionException {
		Utility.executeStreamconsumer(createPerformCommand("pom.xml"), sourceCheckOutDir.getPath(), "", "");
	}
	
	private String createPerformCommand(String pomName) {
		StringBuilder command = new StringBuilder("mvn org.apache.maven.plugins:maven-release-plugin::2.4:perform ");
		command.append("-f ").append(pomName);
		return command.toString();
	}
	
//	private void perFormRelease() throws MojoExecutionException {
//		try {
//			MojoDescriptor mogoDescriptor = getMojoDescriptor("perform");
//			Xpp3Dom configuration = new Xpp3Dom("configuration");
//			Xpp3Dom usernameConf = new Xpp3Dom("username");
//			usernameConf.setValue(username);
//			configuration.addChild(usernameConf);
//			Xpp3Dom passwordConf = new Xpp3Dom("password");
//			passwordConf.setValue(password);
//			configuration.addChild(passwordConf);
//			mavenSession.getRequest().setPom(sourcePomFile);
//			executeMojo(mogoDescriptor, configuration, mavenSession);
//		} catch (MojoExecutionException e) {
//			throw new MojoExecutionException(e.getMessage());
//		}
//	}

	private void prepareRelease() throws MojoExecutionException {
		boolean executeStreamconsumer = Utility.executeStreamconsumer(createPrepareCommand("pom.xml"), sourceCheckOutDir.getPath(), "", "");
		if(executeStreamconsumer) {
			perFormRelease();
		}
		if(isSplittedProject && hasSplitDotPhresco) {
			Utility.executeCommand(createPrepareCommand("phresco-pom.xml"), phresoCheckOutDir.getPath());
		}
		if(isSplittedProject && hasSplitDotPhresco) {
			Utility.executeCommand(createPrepareCommand("pom.xml"), testCheckOutDir.getPath());
		}
	}
	
	private String createPrepareCommand(String pomName) {
		StringBuilder command = new StringBuilder("mvn org.apache.maven.plugins:maven-release-plugin::2.4:prepare ");
		command.append("-DreleaseVersion=").append(releaseVersion).append(" ");
		command.append("-Dtag=").append(tag).append(" ");
		command.append("-DdevelopmentVersion=").append(developmentVersion).append(" ");
		command.append("-Dusername=").append(username).append(" ");
		command.append("-Dpassword=").append(password).append(" ");
		command.append("-DscmCommentPrefix=").append(message).append(" ");
		command.append("-f ").append(pomName);
		return command.toString();
	}
	
//	private void prepareRelease() throws MojoExecutionException {
//		MojoDescriptor mogoDescriptor = getMojoDescriptor("prepare");
//		Xpp3Dom configuration = new Xpp3Dom("configuration");
//		Xpp3Dom releaseVersionConf = new Xpp3Dom("releaseVersion");
//		releaseVersionConf.setValue(releaseVersion);
//		configuration.addChild(releaseVersionConf);
//		Xpp3Dom tagConf = new Xpp3Dom("tag");
//		tagConf.setValue(tag);
//		configuration.addChild(tagConf);
//		Xpp3Dom developmentVersionConf = new Xpp3Dom("developmentVersion");
//		developmentVersionConf.setValue(developmentVersion);
//		configuration.addChild(developmentVersionConf);
//		Xpp3Dom usernameConf = new Xpp3Dom("username");
//		usernameConf.setValue(username);
//		configuration.addChild(usernameConf);
//		Xpp3Dom passwordConf = new Xpp3Dom("password");
//		passwordConf.setValue(password);
//		configuration.addChild(passwordConf);
//		Xpp3Dom commentConf = new Xpp3Dom("scmCommentPrefix");
//		commentConf.setValue(message);
//		configuration.addChild(commentConf);
//		try {
//			mavenSession.getRequest().setPom(sourcePomFile);
//			mavenSession.getRequest().setBaseDirectory(sourceCheckOutDir);
//			mavenSession.setCurrentProject(initMavenProject(new File(sourceCheckOutDir, "pom.xml")));
////			mavenSession.setProjects(Collections.singletonList(mavenProject));
//			executeMojo(mogoDescriptor, configuration, mavenSession);
//			if(isSplittedProject && hasSplitDotPhresco) {
//				mavenSession.getRequest().setPom(dotPhrescoPomFile);
//				executeMojo(mogoDescriptor, configuration, mavenSession);
//			}
//			if(isSplittedProject && hasSplitTest) {
//				mavenSession.getRequest().setPom(testPomFile);
//				executeMojo(mogoDescriptor, configuration, mavenSession);
//			}
//		} catch (MojoExecutionException e) {
//			e.printStackTrace();
//			throw new MojoExecutionException(e.getMessage());
//		}
//	}

	private void executeMojo(MojoDescriptor mojoDescriptor, Xpp3Dom configuration, MavenSession mavenSession) throws MojoExecutionException {
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
	 
//		private void releaseClean() throws MojoExecutionException {
//		MojoDescriptor mogoDescriptor = getMojoDescriptor("clean");
//		Xpp3Dom configuration = new Xpp3Dom("configuration");
//		executeMojo(mogoDescriptor, configuration);
//	}
//
//	private void rollbackRelease() throws MojoExecutionException {
//		MojoDescriptor mogoDescriptor = getMojoDescriptor("rollback");
//		Xpp3Dom configuration = new Xpp3Dom("configuration");
//		try {
//			executeMojo(mogoDescriptor, configuration);
//		} catch (MojoExecutionException e) {
//			releaseClean();
//		}
//	}
	 
}
