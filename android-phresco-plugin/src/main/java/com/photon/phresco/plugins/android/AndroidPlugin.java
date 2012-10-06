package com.photon.phresco.plugins.android;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoAbstractPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.util.Utility;

public class AndroidPlugin extends PhrescoAbstractPlugin {

	public AndroidPlugin(Log log) {
		super(log);
	}

	public void pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Pack pack = new Pack();
		pack.pack(configuration, mavenProjectInfo, log);
	}

	public void deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Deploy deploy = new Deploy();
		deploy.deploy(configuration, mavenProjectInfo, log);
	}

}
