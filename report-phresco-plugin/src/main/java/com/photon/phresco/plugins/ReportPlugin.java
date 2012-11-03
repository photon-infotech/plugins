package com.photon.phresco.plugins;

import org.apache.maven.plugin.logging.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class ReportPlugin  extends PhrescoBasePlugin  {

	public ReportPlugin(Log log) {
		super(log);
	}

	@Override
	public void generateReport(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
//		Deploy deploy = new Deploy();
//      deploy.deploy(configuration, mavenProjectInfo, getLog());
	}
	
}
