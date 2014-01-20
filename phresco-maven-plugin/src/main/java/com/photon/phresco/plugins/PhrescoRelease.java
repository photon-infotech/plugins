package com.photon.phresco.plugins;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.SettingsWriter;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

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
     * @parameter expression="${repoUserName}"
     * @readonly
     */
    protected String repoUserName;
    
    
    /**
     * @parameter expression="${jobName}"
     * @readonly
     */
    protected String jobName;
    
    
    /**
     * @parameter expression="${repoPassword}"
     * @readonly
     */
    protected String repoPassword;
    
    /** SCM Manager component to be injected.
     * @component
     */
    private ScmManager scmManager;
    
    /** SettingsWriter component to be injected.
     * @component
     */
    private SettingsWriter settingsWriter;
    
    /**
     * The current Maven session.
     *
     * @parameter default-value="${session}"
     * @parameter required
     * @readonly
     */
    private MavenSession mavenSession;
    
    private File sourceCheckOutDir;
    private File phresoCheckOutDir;
    private File testCheckOutDir;
    private boolean isSplittedProject;
    private boolean hasSplitSource;
    private boolean hasSplitTest;
    private String sourceRepoUrl;
    private String testRepoURL;
    private File sourcePomFile;
    private String dotPhrescoRepoURL;
    private String projectName;
    private String projHome;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if (StringUtils.isNotEmpty(jobName)) {
				projHome = Utility.getJenkinsHome().concat(FrameworkConstants.WORKSPACE_DIR).concat(File.separator).concat(jobName).concat("###").concat(appDirName);
			} else {
				projHome = Utility.getProjectHome().concat(appDirName);
			}
		ProjectInfo projectInfo = Utility.getProjectInfo(projHome, "");
		projectName = projectInfo.getName();
		sourcePomFile = Utility.getPomFileLocation(projHome, "");
		checkoutApplication();
		prepareRelease(sourcePomFile);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private void checkoutApplication() throws MojoExecutionException {
		isSplitted();
		String phrescoTemp = Utility.getPhrescoTemp();
		sourceCheckOutDir = new File(phrescoTemp, UUID.randomUUID().toString());
		sourceCheckOutDir.mkdirs();
		checkout(sourceRepoUrl, sourceCheckOutDir);
		if(isSplittedProject && hasSplitSource) {
			phresoCheckOutDir = new File(phrescoTemp, UUID.randomUUID().toString());
			phresoCheckOutDir.mkdirs();
			checkout(dotPhrescoRepoURL, phresoCheckOutDir);
		}
		if(isSplittedProject && hasSplitTest) {
			testCheckOutDir = new File(phrescoTemp, UUID.randomUUID().toString());
			testCheckOutDir.mkdirs();
			checkout(testRepoURL, testCheckOutDir);
		}
	}

	private void checkout(String repoURL, File checkOutdir) throws MojoExecutionException {
		ScmRepository scmRepository;
		String urlWithScm = createScmURL(repoURL);
		try {
			scmRepository = scmManager.makeScmRepository(urlWithScm);
			scmManager.checkOut(scmRepository, new ScmFileSet(checkOutdir), getScmVersion());
		} catch (ScmRepositoryException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (NoSuchScmProviderException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (ScmException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private String createScmURL(String repoUrl) {
		String repoType = "";
		if (repoUrl.startsWith("bk")) {
			repoType = FrameworkConstants.BITKEEPER;
		} else if (repoUrl.endsWith(".git") || repoUrl.contains("gerrit") || repoUrl.startsWith("ssh")) {
			repoType = FrameworkConstants.GIT;
		} else if (repoUrl.contains("svn")) {
			repoType = FrameworkConstants.SVN;
		}
		return "scm:".concat(repoType).concat(":").concat(repoUrl);
	}
	
	private void isSplitted() {
		dotPhrescoRepoURL = project.getProperties().getProperty(Constants.POM_PROP_KEY_PHRESCO_REPO_URL);
		testRepoURL = project.getProperties().getProperty(Constants.POM_PROP_KEY_TEST_REPO_URL);
		sourceRepoUrl = project.getProperties().getProperty(Constants.POM_PROP_KEY_SRC_REPO_URL);
		if(StringUtils.isNotEmpty(testRepoURL)) {
			isSplittedProject = true;
			hasSplitTest = true;
		}
		if(StringUtils.isNotEmpty(dotPhrescoRepoURL)) {
			isSplittedProject = true;
			hasSplitSource = true;
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
	
	private String createPerformCommand(String pomName) throws MojoExecutionException {
		StringBuilder command = new StringBuilder("mvn org.apache.maven.plugins:maven-release-plugin:2.4:perform ");
		command.append("-DpomFileName=").append(pomName).append(" ");
		command.append("-f ").append(pomName);
		File settingsFile = new File(Utility.getPhrescoTemp(), "settings-" + projectName);
		try {
			String repoId = "";
			Server server = new Server();
			DistributionManagement distributionManagement = project.getDistributionManagement();
			if(distributionManagement != null) {
				DeploymentRepository repository = distributionManagement.getRepository();
				if(repository != null) {
					repoId = repository.getId();
					server.setId(repoId);
					server.setUsername(repoUserName);
					byte[] decodeBase64 = Base64.decodeBase64(repoPassword);
					server.setPassword(new String(decodeBase64));
					Settings settings = mavenSession.getSettings();
					settings.addServer(server);
					settingsWriter.write(settingsFile, null, settings);
				}
			}
			if(settingsFile.exists()) {
				command.append(" ").append("-s").append("\"").append(settingsFile.getPath()).append("\"");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		return command.toString();
	}
	
	private void prepareRelease(File pomFile) throws MojoExecutionException {
		boolean performFlag =  false;
		performFlag = Utility.executeStreamconsumer(createPrepareCommand("pom.xml"), sourceCheckOutDir.getPath(), "", "");
		if(isSplittedProject && hasSplitSource) {
			Utility.executeStreamconsumer(createPrepareCommand("phresco-pom.xml"), phresoCheckOutDir.getPath(), "", "");
		}
		if(isSplittedProject && hasSplitTest) {
			Utility.executeCommand(createPrepareCommand("pom.xml"), testCheckOutDir.getPath());
		}
		
		if(performFlag) {
			perFormRelease();
		}
	}
	
	private String createPrepareCommand(String pomName) {
		StringBuilder command = new StringBuilder("mvn org.apache.maven.plugins:maven-release-plugin:2.4:prepare ");
		command.append("-DreleaseVersion=").append(releaseVersion).append(" ");
		command.append("-Dtag=").append(tag).append(" ");
		command.append("-DdevelopmentVersion=").append(developmentVersion).append(" ");
		command.append("-Dusername=").append(username).append(" ");
		command.append("-Dpassword=").append(password).append(" ");
		command.append("-DscmCommentPrefix=").append(message).append(" ");
		command.append("-DpomFileName=").append(pomName).append(" ");
		command.append("-f ").append(pomName);
		return command.toString();
	}
	 
}
