package com.photon.phresco.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.UUID;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.io.SettingsWriter;

import com.opensymphony.xwork2.util.ArrayUtils;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.FileUtil;
import com.photon.phresco.util.Utility;

/**
 * Goal which deploys the build to repository
 * 
 * @goal deploy-build
 * 
 */
public class PhrescoDeployBuild extends AbstractMojo implements PluginConstants {
	
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
     * @parameter expression="${repoUrl}"
     * @readonly
     */
    protected String repoUrl;
    
    /**
     * @parameter expression="${userName}"
     * @readonly
     */
    protected String userName;
    
    /**
     * @parameter expression="${password}"
     * @readonly
     */
    protected String password;
    
    /**
     * @parameter expression="${buildNo}"
     * @readonly
     */
    protected String buildNo;
    
    /**
     * @component role="org.apache.maven.settings.io.SettingsWriter"
     */
    private SettingsWriter settingsWriter;
    
    private File settingsXML;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		deployArtifact();
	}
	
	private void deployArtifact() throws MojoExecutionException {
		BufferedReader reader = Utility.executeCommand(createCommand(), project.getBasedir().getPath());
		String line = "";
		try {
			while ((line  = reader.readLine()) != null) {
				System.out.println(line); 
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} finally {
			if(settingsXML.exists()) {
				FileUtil.delete(settingsXML.getParentFile());
			}
		}
	}
	
	private String createCommand() throws MojoExecutionException {
		File buildFile = getBuildFile();
		File settingsXML = getSettingsXML();
		StringBuilder command = new StringBuilder(MVN_CMD).append(STR_SPACE).append(DEPLOY_FILE).append(STR_SPACE);
		command.append(HYPHEN_D).append(ATTR_FILE).append(EQUAL).append(WP_STR_DOUBLEQUOTES).
			append(buildFile.getPath()).append(WP_STR_DOUBLEQUOTES).append(STR_SPACE);
		command.append(HYPHEN_D).append(ARTIFACT_ID).append(EQUAL).append(project.getArtifactId()).append(STR_SPACE);
		command.append(HYPHEN_D).append(GROUP_ID).append(EQUAL).append(project.getGroupId()).append(STR_SPACE);
		command.append(HYPHEN_D).append(VERSION).append(EQUAL).append(project.getVersion()).append(STR_SPACE);
		command.append(HYPHEN_D).append(PACKAGING).append(EQUAL).append(project.getPackaging()).append(STR_SPACE);
		command.append(HYPHEN_D).append(POM_FILE).append(EQUAL).append(WP_STR_DOUBLEQUOTES).append(project.getFile().getPath()).
			append(WP_STR_DOUBLEQUOTES).append(STR_SPACE);
		command.append(HYPHEN_D).append(SHAREPOINT_STR_URL).append(EQUAL).append(repoUrl).append(STR_SPACE);
		command.append(HYPHEN_D).append(REPOID).append(EQUAL).append(project.getName()).append(STR_SPACE);
		command.append(SETTINGS_ARG).append(STR_SPACE).append(WP_STR_DOUBLEQUOTES).append(settingsXML.getPath()).append(WP_STR_DOUBLEQUOTES);
		return command.toString();
	}
	
	/**
	 * Create a settings file i
	 * @return
	 * @throws MojoExecutionException
	 */
	private File getSettingsXML() throws MojoExecutionException {
		Server server = new Server();
		server.setId(project.getName());
		server.setUsername(userName);
		server.setPassword(password);
		mavenSession.getSettings().addServer(server);
		String settingsPath = Utility.getPhrescoTemp().concat(UUID.randomUUID().toString());
		settingsXML = new File(settingsPath, MAVEN_DEF_SETTINGS);
		try {
			settingsWriter.write(settingsXML, null, mavenSession.getSettings());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		return settingsXML;
	}


	private File getBuildFile() throws MojoExecutionException {
		File deployFile = null;
		PluginUtils pluginUtils = new PluginUtils();
		BuildInfo buildInfo;
		try {
			buildInfo = pluginUtils.getBuildInfo(Integer.parseInt(buildNo), project.getBasedir().getPath());
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(project.getBasedir().getPath()).append(PluginConstants.BUILD_DIRECTORY);
			File buildDir = new File(stringBuilder.toString());
			stringBuilder.append(File.separator).append(buildInfo.getBuildName());
			File buildFile = new File(stringBuilder.toString());
			File tempDir = new File(buildDir.getPath() + PluginConstants.TEMP_DIR);
			tempDir.mkdirs();
			ArchiveUtil.extractArchive(buildFile.getPath(), tempDir.getPath(), ArchiveType.ZIP);
			String[] fileList = tempDir.list(new FileNameFilter(project.getPackaging()));
			if(ArrayUtils.isNotEmpty(fileList)) {
				for (String file : fileList) {
					if(file.equals(project.getBuild().getFinalName() + "." + project.getPackaging())) {
						deployFile = new File(tempDir.getPath() + "/" + file);
					} else {
						deployFile = new File(tempDir.getPath() + "/" + fileList[0]);
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (MojoExecutionException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		return deployFile;
	}
}

class FileNameFilter implements FilenameFilter {
	
	String packaging = "";
	
	public FileNameFilter(String packaging) {
		this.packaging = packaging;
	}
	
	public boolean accept(File dir, String name) {
		return name.endsWith(packaging);
	}

}