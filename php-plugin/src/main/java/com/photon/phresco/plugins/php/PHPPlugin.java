package com.photon.phresco.plugins.php;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoAbstractPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class PHPPlugin extends PhrescoAbstractPlugin {
   
	public PHPPlugin(Log log) {
		super(log);
	}

    public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
    	PhpPackage pack = new PhpPackage();
        pack.pack(configuration, mavenProjectInfo, getLog());
    }

    public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
        PhpDeploy deploy = new PhpDeploy();
        deploy.deploy(configuration, mavenProjectInfo, getLog());
    }
}
