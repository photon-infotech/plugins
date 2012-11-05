package com.photon.phresco.plugins;

import java.io.*;

import org.apache.maven.plugin.logging.*;
import org.apache.maven.project.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.*;
import com.photon.phresco.plugins.util.*;

public class GenerateReport implements PluginConstants {
	private MavenProject project;
	private File baseDir;
	private Log log;
	private PluginsUtil util;

	public void generate(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		log.debug("Generate Report Generate Called ");
		try {
			this.log = log;
	        baseDir = mavenProjectInfo.getBaseDir();
	        project = mavenProjectInfo.getProject();
	        
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
