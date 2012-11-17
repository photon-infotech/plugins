package com.photon.phresco.plugins.xcode;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.*;
import org.codehaus.plexus.util.cli.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
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
			
			String buildNumber = configs.get(BUILD_NUMBER);
			String family = configs.get(FAMILY);
			String simVersion = configs.get(SIM_VERSION);
			String deviceType = configs.get(DEVICE_TYPE);
			
			StringBuilder sb = new StringBuilder();
			sb.append("mvn xcode:deploy");
			if (StringUtils.isEmpty(buildNumber)) {
				System.out.println("Build number is empty for deployment . ");
				throw new MojoExecutionException("Build number is empty for deployment . ");
			}
			
			if (StringUtils.isEmpty(deviceType)) {
				System.out.println("deviceType is not specified for deployment . ");
				throw new MojoExecutionException("deviceType is not specified for deployment . ");
			}
			
			if (StringUtils.isNotEmpty(buildNumber)) {
				sb.append(STR_SPACE);
				sb.append("-DbuildNumber=" + buildNumber);
			}
			
			if (StringUtils.isNotEmpty(family)) {
				sb.append(STR_SPACE);
				sb.append("-Dfamily=" + family);
			}
			
			if (StringUtils.isNotEmpty(simVersion)) {
				sb.append(STR_SPACE);
				sb.append("-Dsimulator.version=" + simVersion);
			}
			
			List<Parameter> parameters = config.getParameters().getParameter();
			for (Parameter parameter : parameters) {
				if (DEVICE_TYPE.equals(parameter.getKey())) {
					List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
					for (MavenCommand mavenCommand : mavenCommands) {
						if (mavenCommand.getKey().equals(deviceType)) {
							sb.append(STR_SPACE);
							sb.append(mavenCommand.getValue());
						}
					}
				}
			}
			
			System.out.println("Command " + sb.toString());
			Commandline cl = new Commandline(sb.toString());
			Process pb = cl.execute();
			
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				System.out.write(singleByte);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException("Deployment failed ", e);
		}
	}

}
