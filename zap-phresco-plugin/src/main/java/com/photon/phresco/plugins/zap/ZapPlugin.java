package com.photon.phresco.plugins.zap;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoBasePlugin;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class ZapPlugin extends PhrescoBasePlugin {

	public ZapPlugin(Log log) {
		super(log);
	}

	@Override
	public ExecutionStatus zapStart(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		ZapStart start = new ZapStart();
		start.start(configuration, mavenProjectInfo, log);
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus zapTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		ZapAnalysis analysis = new ZapAnalysis();
		analysis.analysis(configuration, mavenProjectInfo, log);
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus zapStop(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		ZapStop zapStop = new ZapStop();
		zapStop.zapStop(configuration, mavenProjectInfo, log);
		return new DefaultExecutionStatus();
	}

}
