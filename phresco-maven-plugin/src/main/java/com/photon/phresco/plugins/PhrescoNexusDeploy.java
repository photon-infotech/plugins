package com.photon.phresco.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.SettingsWriter;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

/**
 * Goal which releases the project
 * 
 * @goal nexusDeploy
 * 
 */
public class PhrescoNexusDeploy extends AbstractMojo implements PluginConstants {

	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

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
	 * @parameter expression="${jobName}"
	 * @readonly
	 */
	protected String jobName;

	/**
	 * @parameter expression="${appDirName}"
	 * @readonly
	 */
	protected String appDirName;
	
	
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

	private String projHome;
	private File sourcePomFile;
	private File pomFile;
	private String version;
	private String repoUrl;
	private String repoId;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if (StringUtils.isNotEmpty(jobName)) {
				projHome = Utility.getJenkinsHome().concat(FrameworkConstants.WORKSPACE_DIR).concat(File.separator)
						.concat(jobName).concat("###").concat(appDirName);
			} else {
				projHome = Utility.getProjectHome().concat(appDirName);
			}
			sourcePomFile = Utility.getPomFileLocation(projHome, "");
			pomFile = project.getFile();
			PomProcessor pom = new PomProcessor(pomFile);
			version = project.getVersion();
			if (pom.getModel().getDistributionManagement() == null) {
				throw new MojoExecutionException("Project Distribution Management Should Not Be Empty.....");
			}
			if (version.contains("SNAPSHOT")) {
				repoUrl = pom.getModel().getDistributionManagement().getSnapshotRepository().getUrl();
				repoId = pom.getModel().getDistributionManagement().getSnapshotRepository().getId();
			} else {
				repoUrl = pom.getModel().getDistributionManagement().getRepository().getUrl();
				repoId = pom.getModel().getDistributionManagement().getRepository().getId();
			}
			checkFileType(sourcePomFile);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());

		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage());
		}

	}

	private void checkFileType(File sourcePomFile) throws MojoExecutionException {
		File deployFile = null;
		String[] subList = null;
		String packType = "";
		try {
			File buildDir = new File(sourcePomFile.getParent() + PluginConstants.BUILD_DIRECTORY);
			File tempDir = new File(buildDir.getPath() + PluginConstants.TEMP_DIR);
			tempDir.mkdirs();
			String[] list = buildDir.list(new PhrescoFileNameFilter("zip"));
			if (list.length > 0) {
				File zipFile = new File(buildDir.getPath() + File.separator + list[0]);
				ArchiveUtil.extractArchive(zipFile.getPath(), tempDir.getPath(), ArchiveType.ZIP);
				subList = tempDir.list(new PhrescoFileNameFilter("war"));
				if (subList.length > 0) {
					deployFile = new File(tempDir.getPath() + File.separator + subList[0]);
					packType = "war";
				}
				if (subList.length <= 0) {
					subList = tempDir.list(new PhrescoFileNameFilter("jar"));
					if (subList.length > 0) {
						deployFile = new File(tempDir.getPath() + File.separator + subList[0]);
						packType = "jar";
					}
				}

				if (subList.length <= 0) {
					subList = tempDir.list(new PhrescoFileNameFilter("apk"));
					if (subList.length > 0) {
						deployFile = new File(tempDir.getPath() + File.separator + subList[0]);
						packType = "apk";
					}
				}

				if (subList.length <= 0) {
					subList = buildDir.list(new PhrescoFileNameFilter("zip"));
					if (subList.length > 0) {
						deployFile = new File(buildDir.getPath() + File.separator + subList[0]);
						packType = "zip";
					}
				}
				executeCommand(packType, deployFile);
			}
		} catch (PhrescoException e) {
			e.printStackTrace();
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void executeCommand(String packType, File deployFile) throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		try {
			ProjectInfo projectInfo = Utility.getProjectInfo(projHome, "");
			ApplicationInfo applicationInfo = projectInfo.getAppInfos().get(0);
			File sourceFolderLocation = Utility.getSourceFolderLocation(projectInfo, projHome, "");
			String pomFile = sourceFolderLocation.getPath() + File.separator + applicationInfo.getPomFile();
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append("org.apache.maven.plugins");
			sb.append(COLON);
			sb.append("maven-deploy-plugin");
			sb.append(COLON);
			sb.append("2.7");
			sb.append(COLON);
			sb.append("deploy-file");

			sb.append(STR_SPACE);
			sb.append("-Durl=");
			sb.append("\"" + repoUrl + "\"");
			sb.append(STR_SPACE);
			sb.append("-DrepositoryId=");
			sb.append(repoId);
			sb.append(STR_SPACE);
			sb.append("-Dfile=");
			sb.append("\"" + deployFile.getPath() + "\"");
			sb.append(STR_SPACE);

			sb.append("-DgroupId=");
			sb.append(project.getGroupId());
			sb.append(STR_SPACE);

			sb.append("-DartifactId=");
			sb.append(project.getArtifactId());
			sb.append(STR_SPACE);

			sb.append("-Dversion=");
			sb.append(project.getVersion());
			sb.append(STR_SPACE);

			sb.append("-Dpackaging=");
			sb.append(packType);
			sb.append(STR_SPACE);

			sb.append("-DgeneratePom=");
			sb.append("true");
			sb.append(STR_SPACE);

			sb.append("-DpomFile=");
			sb.append("\"" + pomFile + "\"");
			String addRepoInfo = addRepoInfo(sb, projectInfo.getName());
			bufferedReader = Utility.executeCommand(addRepoInfo, project.getBasedir().getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("[ERROR]")) {
					System.out.println(line); // do not use getLog() here as this line already contains the log type.
				}
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	private String addRepoInfo(StringBuilder command, String projectName) throws MojoExecutionException {
		File settingsFile = new File(Utility.getPhrescoTemp(), "settings-" + projectName);
		try {
			Server server = new Server();
					server.setId(repoId);
					server.setUsername(username);
					server.setPassword(password);
					Settings settings = mavenSession.getSettings();
					settings.addServer(server);
					settingsWriter.write(settingsFile, null, settings);
			if(settingsFile.exists()) {
				command.append(" ").append("-s").append("\"").append(settingsFile.getPath()).append("\"");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		return command.toString();
	}
}

class PhrescoFileNameFilter implements FilenameFilter {
	private String fileExtension;

	public PhrescoFileNameFilter(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(fileExtension);
	}
}
