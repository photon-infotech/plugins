package com.photon.phresco.plugins.xcode;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoAbstractPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class XcodePlugin extends PhrescoAbstractPlugin {

	public XcodePlugin(Log log) {
		super(log);
	}

	public void pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Package pack = new Package();
		pack.pack(configuration, mavenProjectInfo, log);
		
	}

	public void deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
			System.out.println("WAXSIM_HOME... " + System.getenv("PHRESCO_HOME"));
			System.out.println("WAXSIM_HOME... " + System.getenv("waxsim_home"));
			Deploy deploy = new Deploy();
			try {
				deploy.deploy(configuration, mavenProjectInfo, log);
			} catch (MojoExecutionException e) {
			} catch (MojoFailureException e) {
			}
	}

}
