package com.photon.phresco.plugins.wordpress;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoBasePlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class WordPressPlugin extends PhrescoBasePlugin {

	public WordPressPlugin(Log log) {
		super(log);
	}

	@Override
	public void pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Package pack = new Package();
		pack.pack(configuration, mavenProjectInfo, getLog());
	}

	@Override
	public void deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Deploy deploy = new Deploy();
		deploy.deploy(configuration, mavenProjectInfo, getLog());
	}
	
	@Override
    public void startHub(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		StartHub startHub = new StartHub();
		startHub.startHub(configuration, mavenProjectInfo, getLog());
    }

	@Override
    public void startNode(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		StartNode startNode = new StartNode();
		startNode.startNode(configuration, mavenProjectInfo, getLog());
    }
}
