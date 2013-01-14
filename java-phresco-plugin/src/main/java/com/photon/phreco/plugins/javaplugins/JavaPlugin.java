package com.photon.phreco.plugins.javaplugins;

import org.apache.maven.plugin.logging.Log;

import com.photon.phreco.plugins.javaplugins.JavaPackage;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoAbstractPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class JavaPlugin extends PhrescoAbstractPlugin {

	public JavaPlugin(Log log) {
		super(log);
	}

	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
        JavaPackage pack = new JavaPackage();
        pack.pack(configuration, mavenProjectInfo, log);
    }

	public void deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		JavaDeploy deploy = new JavaDeploy();
		deploy.deploy(configuration, mavenProjectInfo, log);
		
	}
}
