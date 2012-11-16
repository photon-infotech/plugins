package com.photon.phresco.plugins.xcode;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.*;
import org.codehaus.plexus.util.cli.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.*;

public class Deploy implements PluginConstants {
	
	private Log log;
	/**
	 * Execute the xcode command line utility for iphone deployment.
	 * @throws PhrescoException 
	 */
	public void deploy(Configuration config, MavenProjectInfo mavenProjectInfo, final Log log) throws MojoExecutionException, MojoFailureException {
		try {
			System.out.println("Deployment started ");
			this.log = log;
			Map<String, String> configs = MojoUtil.getAllValues(config);
			
			String buildNumber = configs.get("buildNumber");
			String family = configs.get("family");
			String simVersion = configs.get("sdkVersion");
			StringBuilder sb = new StringBuilder();
			sb.append("mvn xcode:deploy");
			sb.append(STR_SPACE);
			sb.append("-DbuildNumber=" + buildNumber);
			sb.append(STR_SPACE);
			sb.append("-Dfamily=" + family);
			sb.append(STR_SPACE);
			sb.append("-Dsimulator.version=" + simVersion);
			System.out.println("Command================================> " + sb.toString());
			Commandline cl = new Commandline(sb.toString());
			Process pb = cl.execute();
			
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				System.out.write(singleByte);
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Deployment failed ", e);
		}
	}

}
