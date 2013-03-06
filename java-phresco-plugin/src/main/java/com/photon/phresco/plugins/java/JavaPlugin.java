package com.photon.phresco.plugins.java;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoBasePlugin;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class JavaPlugin extends PhrescoBasePlugin {

	public JavaPlugin(Log log) {
		super(log);
	}

	@Override
	public ExecutionStatus pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			File targetDir = new File(mavenProjectInfo.getBaseDir() + DO_NOT_CHECKIN_FOLDER + File.separator + TARGET);
			if (targetDir.exists()) {
				FileUtils.deleteDirectory(targetDir);
				log.info("Target Folder Deleted Successfully");
			}
			writePhrescoBuildXml(configuration, mavenProjectInfo);
			Package pack = new Package();
			pack.pack(configuration, mavenProjectInfo, log);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Deploy deploy = new Deploy();
		deploy.deploy(configuration, mavenProjectInfo, log);
		return new DefaultExecutionStatus();
		
	}

	@Override
	public ExecutionStatus startServer(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Start start = new Start();
		start.start(configuration, mavenProjectInfo, log);
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus stopServer(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Stop stop = new Stop();
		stop.stop(mavenProjectInfo, log);
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus runUnitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		JavaTest unitTest = new JavaTest();
		unitTest.runTest(configuration, mavenProjectInfo);
		return new DefaultExecutionStatus();
	}
	
}
