package com.photon.phresco.plugins.xcode;

import org.apache.maven.plugin.logging.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class XcodePlugin extends PhrescoBasePlugin {

	public XcodePlugin(Log log) {
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
	public void validate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		ClangCodeValidator validator = new ClangCodeValidator();
		validator.validate(configuration, mavenProjectInfo, getLog());
	}
	
	@Override
	public void runUnitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		UnitTest test = new UnitTest();
		test.unitTest(configuration, mavenProjectInfo);
	}
	
	@Override
	public void runFunctionalTest (Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		FunctionalTest test = new FunctionalTest();
		test.functionalTest(configuration, mavenProjectInfo);
	}
}
