package com.photon.phresco.plugins.xcode;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.*;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.util.Constants;

public class XcodePlugin extends PhrescoBasePlugin {

	public XcodePlugin(Log log) {
		super(log);
	}

	@Override
	public ExecutionStatus pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Package pack = new Package();
		pack.pack(configuration, mavenProjectInfo, getLog());
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Deploy deploy = new Deploy();
		deploy.deploy(configuration, mavenProjectInfo, getLog());
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus validate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		ClangCodeValidator validator = new ClangCodeValidator();
		validator.validate(configuration, mavenProjectInfo, getLog());
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus runUnitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		UnitTest test = new UnitTest();
		test.unitTest(configuration, mavenProjectInfo);
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus runFunctionalTest (Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		FunctionalTest test = new FunctionalTest();
		try {
			test.functionalTest(configuration, mavenProjectInfo);
		} catch (MojoExecutionException e) {
			throw new PhrescoException(Constants.MOJO_ERROR_MESSAGE);
		}
		return new DefaultExecutionStatus();
	}
}
