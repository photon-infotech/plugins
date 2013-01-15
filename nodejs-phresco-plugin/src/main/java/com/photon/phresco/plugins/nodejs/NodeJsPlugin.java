package com.photon.phresco.plugins.nodejs;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoAbstractPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class NodeJsPlugin extends PhrescoAbstractPlugin {

	public NodeJsPlugin(Log log) {
		super(log);
	}

	public void pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Package pack = new Package();
		pack.pack(configuration, mavenProjectInfo, log);
	}

	public void deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
	}

}
