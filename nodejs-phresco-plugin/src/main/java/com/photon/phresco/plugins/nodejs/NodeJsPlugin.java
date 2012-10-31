package com.photon.phresco.plugins.nodejs;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoBasePlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class NodeJsPlugin extends PhrescoBasePlugin {

	public NodeJsPlugin(Log log) {
		super(log);
	}

	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Package pack = new Package();
		pack.pack(configuration, mavenProjectInfo, log);
	}

	public void startServer(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Start start = new Start();
		start.start(configuration, mavenProjectInfo, log);
	}

	public void stopServer(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Stop stop = new Stop();
		stop.stop(mavenProjectInfo, log);
	}
}