package com.photon.phresco.plugins.seo;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoBasePlugin;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class SeoPlugin extends PhrescoBasePlugin {

	public SeoPlugin(Log log) {
		super(log);
	}
	
	@Override
	public ExecutionStatus seoTest(Configuration configuration,	MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		SeoTest seoTest = new SeoTest();
		seoTest.seoTest(configuration, mavenProjectInfo, log);
		return  new DefaultExecutionStatus();
	}

	
}
