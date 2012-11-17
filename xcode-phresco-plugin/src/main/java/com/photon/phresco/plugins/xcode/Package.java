package com.photon.phresco.plugins.xcode;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.logging.*;
import org.codehaus.plexus.util.cli.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.*;

public class Package implements PluginConstants {

	private String environmentName;
	private Log log;
	/**
	 * Execute the xcode command line utility.
	 * @throws PhrescoException 
	 */
	public void pack(Configuration config, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		try {
			this.log = log;
			Map<String, String> configs = MojoUtil.getAllValues(config);
			environmentName = configs.get(ENVIRONMENT_NAME);
			String sdk = configs.get("sdk");
			String targetName = configs.get("target");
			String configuration = configs.get("mode");
			String encrypt = configs.get("encrypt");
			String plistFile = configs.get("plistFile");
			String projectType = configs.get("projectType");
			StringBuilder sb = new StringBuilder();
			sb.append("mvn xcode:xcodebuild");
			sb.append(STR_SPACE);
			sb.append("-DenvironmentName=" + environmentName);
			sb.append(STR_SPACE);
			sb.append("-DprojectType=" + projectType);
			sb.append(STR_SPACE);
			sb.append("-Dsdk=" + sdk);
			sb.append(STR_SPACE);
			sb.append("-DtargetName=" + targetName);
			sb.append(STR_SPACE);
			sb.append("-Dconfiguration=" + configuration);
			sb.append(STR_SPACE);
			sb.append("-Dencrypt=" + encrypt);
			sb.append(STR_SPACE);
			sb.append("-Dplistfile=" + plistFile);
			System.out.println("Command " + sb.toString());
			Commandline cl = new Commandline(sb.toString());

			Process pb = cl.execute();
			// Consume subprocess output and write to stdout for debugging
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				//output.write(buffer, 0, bytesRead);
				System.out.write(singleByte);
			}
		} catch (CommandLineException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
