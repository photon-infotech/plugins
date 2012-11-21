package com.photon.phresco.plugins.php;

import java.io.File;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class StartHub implements PluginConstants {
	private MavenProject project;
	private Integer port;
	private Integer newSessionTimeout;
	private String servlets;
	private String prioritizer;
	private String capabilityMatcher;
	private boolean throwOnCapabilityNotPresent;
	private Integer nodePolling;
	private Integer cleanUpCycle;
	private Integer timeout;
	private Integer browserTimeout;
	private Integer maxSession;
	private File baseDir;
	private String funcDir;

	public void startHub(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log)
			throws PhrescoException {
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		port = Integer.parseInt(configs.get("port"));
		newSessionTimeout = Integer.parseInt(configs.get("newSessionWaitTimeout"));
		servlets = configs.get("servlets");
		prioritizer = configs.get("prioritizer");
		capabilityMatcher = configs.get("capabilityMatcher");
		throwOnCapabilityNotPresent = Boolean.parseBoolean(configs.get("throwOnCapabilityNotPresent"));
		nodePolling = Integer.parseInt(configs.get("nodePolling"));
		cleanUpCycle = Integer.parseInt(configs.get("cleanUpCycle"));
		timeout = Integer.parseInt(configs.get("timeout"));
		browserTimeout = Integer.parseInt(configs.get("browserTimeout"));
		maxSession = Integer.parseInt(configs.get("maxSession"));
		try {
			File pomFile = project.getFile();
			PomProcessor processor = new PomProcessor(pomFile);
			funcDir = processor.getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
			PluginUtils plugniutil = new PluginUtils();
			plugniutil.updateHubConfigInfo(baseDir, funcDir, port, newSessionTimeout, servlets, prioritizer, capabilityMatcher,
					throwOnCapabilityNotPresent, nodePolling, cleanUpCycle, timeout, browserTimeout, maxSession);
			log.info("Starting the Hub...");
			plugniutil.startHub(baseDir);
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}
}
