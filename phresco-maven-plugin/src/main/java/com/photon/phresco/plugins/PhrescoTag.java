package com.photon.phresco.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.impl.util.FrameworkUtil;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

/**
 * Goal which tag the project
 * 
 * @goal tag
 * 
 */
public class PhrescoTag extends AbstractMojo {

	/**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
     * @parameter expression="${tag}"
     * @readonly
     */
    protected String tag;
    
    /**
     * @parameter expression="${comment}"
     * @readonly
     */
    protected String comment;
    
    /**
     * @parameter expression="${appDirName}"
     * @readonly
     */
    protected String appDirName;
    
    /**
     * @parameter expression="${releaseVersion}"
     * @readonly
     */
    protected String version;
    
    /**
     * @parameter expression="${currentBranch}"
     * @readonly
     */
    protected String currentBranch;
    
    /**
     * @parameter expression="${skipTests}"
     * @readonly
     */
    protected String skipTests;
    
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		tag(appDirName, comment, currentBranch, tag, version);
	}
	
	public void tag(String appDirName, String comment, String currentBranch, String tagName, String version) throws MojoExecutionException {
		
		String phrescoTemp = Utility.getPhrescoTemp();
		List<String> workingDirList = new ArrayList<String>();
		PluginUtils pluginUtils = new PluginUtils();
		String username = "";
		String password = "";
		Boolean skip = Boolean.valueOf(skipTests);
		try {
			File pomFile = project.getFile();
			PomProcessor processor = new PomProcessor(pomFile);
			List<String> repoUrls = new ArrayList<String>();
			String dotPhrescoRepoUrl = processor.getProperty(Constants.POM_PROP_KEY_PHRESCO_REPO_URL);
			String srcRepoUrl = processor.getProperty(Constants.POM_PROP_KEY_SRC_REPO_URL);
			String testRepoUrl = processor.getProperty(Constants.POM_PROP_KEY_TEST_REPO_URL);
			if (StringUtils.isNotEmpty(dotPhrescoRepoUrl)) {
				repoUrls.add(dotPhrescoRepoUrl);
			} 
			if (StringUtils.isNotEmpty(srcRepoUrl)) {
				repoUrls.add(srcRepoUrl);
			} 
			if (StringUtils.isNotEmpty(testRepoUrl)) {
				repoUrls.add(testRepoUrl);
			} 
			for (String repoUrl : repoUrls) {
				Map<String, String> credential = FrameworkUtil.getCredential(repoUrl);
				if (MapUtils.isNotEmpty(credential)) {
					username = credential.get(FrameworkConstants.REQ_USER_NAME);
					String encryptedPassword = credential.get(FrameworkConstants.REQ_PASSWORD);
					password = FrameworkUtil.getdecryptedPassword(encryptedPassword);
				}
				String uuid = UUID.randomUUID().toString();
				String workingDir = phrescoTemp + uuid;
				workingDirList.add(workingDir);
				pluginUtils.checkout(repoUrl, currentBranch, phrescoTemp, uuid, false, username, password);
				// Construct command for branch
				StringBuilder builder = new StringBuilder();
				builder.append(Constants.MVN_COMMAND)
				.append(Constants.STR_BLANK_SPACE)
				.append(Constants.RELEASE_PLUGIN).append(Constants.STR_COLON).append(Constants.SCM_PREPARE)
				.append(Constants.STR_BLANK_SPACE)
				.append(Constants.SCM_HYPHEN_D).append(Constants.SCM_USERNAME)
				.append(Constants.STR_EQUALS).append(username)
				.append(Constants.STR_BLANK_SPACE)
				.append(Constants.SCM_HYPHEN_D).append(Constants.SCM_PASSWORD)
				.append(Constants.STR_EQUALS).append(password)
				.append(Constants.STR_BLANK_SPACE)
				.append(Constants.SCM_HYPHEN_D).append(Constants.SCM_TAG).append(Constants.STR_EQUALS)
				.append(tagName)
				.append(Constants.STR_BLANK_SPACE)
				.append(Constants.SCM_HYPHEN_D).append(Constants.SCM_RELEASE_VERSION).append(Constants.STR_EQUALS)
				.append(version)
				.append(Constants.STR_BLANK_SPACE)
				.append(Constants.SCM_HYPHEN_D)
				.append(Constants.SCM_UPDATE_WORKING_COPY_VERSIONS + Constants.STR_EQUALS + false);
				if (skip) {
					builder.append(Constants.STR_BLANK_SPACE)
					.append("-Darguments=-DskipTests");
				}
				builder.append(Constants.STR_BLANK_SPACE)
				.append("-DignoreSnapshots=" + true)
				.append(Constants.STR_BLANK_SPACE)
				.append(Constants.SCM_HYPHEN_D).append(Constants.SCM_COMMENT_PREFIX).append(Constants.STR_EQUALS)
				.append("\"" + comment + "\"");
				File pom = new File(workingDir + File.separatorChar + FrameworkConstants.POM_XML);
				if (!pom.exists()) {
					pom = new File(workingDir + File.separatorChar + FrameworkConstants.PHR_POM_XML);
				}
				builder.append(Constants.STR_BLANK_SPACE)
				.append(Constants.SCM_HYPHEN_D)
				.append(Constants.SCM_POM_FILE_NAME)
				.append(Constants.STR_EQUALS)
				.append(pom.getName())
				.append(Constants.STR_BLANK_SPACE)
				.append(Constants.HYPHEN_F).append(Constants.STR_BLANK_SPACE).append(pom.getName());
				Utility.executeStreamconsumer(builder.toString(), workingDir, "", "");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		finally {
			try {
				if (CollectionUtils.isNotEmpty(workingDirList)) {
					for (String workingDir : workingDirList) {
						FileUtils.deleteDirectory(new File(workingDir));
					}
				}
			} catch (IOException e) {
				
			}
		}
	}

}
