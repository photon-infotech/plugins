package com.photon.phresco.plugins;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class ThemeContentPlugin implements PhrescoPlugin {

	Log log;
	public ThemeContentPlugin(Log log) {
		this.log = log;
	}

	public void themeValidator(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("*****************************ThemeValidator Excecuted*************************");
		
	}

	public void themeConvertor(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("*************************ThemeConvertor Excecuted****************************");
		
	}

	public void contentValidator(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("*********************contentValidator Excecuted***************************");
		
	}

	public void contentConvertor(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("************************contentConvertor Excecuted**************************");
		
	}

	public void validate(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void startServer(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void stopServer(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void runUnitTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void runFunctionalTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void runPerformanceTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void startHub(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void stopHub(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void startNode(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void stopNode(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void runLoadTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void performCIPreBuildStep(String jobName, String goal,
			String phase, MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	public void generateReport(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}
}
