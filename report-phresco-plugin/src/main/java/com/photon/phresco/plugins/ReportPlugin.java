package com.photon.phresco.plugins;

import org.apache.maven.plugin.logging.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class ReportPlugin  extends PhrescoBasePlugin  {

	public ReportPlugin(Log log) {
		super(log);
	}

	@Override
	public ExecutionStatus generateReport(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		GenerateReport report = new GenerateReport();
		report.generate(configuration, mavenProjectInfo, getLog());
		return new DefaultExecutionStatus();
	}
	
}
