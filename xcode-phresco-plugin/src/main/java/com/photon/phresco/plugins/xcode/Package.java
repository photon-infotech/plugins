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
			String sdk = configs.get(SDK);
			String target = configs.get(TARGET);
			String configuration = configs.get(MODE);
			String encrypt = configs.get(ENCRYPT);
			String plistFile = configs.get(PLIST_FILE);
			String projectType = configs.get(PROJECT_TYPE);
			
			if (StringUtils.isEmpty(environmentName)) {
				System.out.println("Environment Name is empty . ");
				throw new PhrescoException("Environment Name is empty . ");
			}
			
			if (StringUtils.isEmpty(sdk)) {
				System.out.println("SDK is empty . ");
				throw new PhrescoException("SDK is empty . ");
			}
			
			if (StringUtils.isEmpty(target)) {
				System.out.println("Target is empty for deployment . ");
				throw new PhrescoException("Target is empty for deployment .");
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(XCODE_BUILD_COMMAND);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + ENVIRONMENT_NAME + EQUAL + environmentName);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + PROJECT_TYPE + EQUAL + projectType);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + SDK + EQUAL + sdk);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + TARGET_NAME + EQUAL + target);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + CONFIGURATION + EQUAL + configuration);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + ENCRYPT + EQUAL + encrypt);
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + PLIST_FILE + EQUAL + plistFile);
			
			System.out.println("Command " + sb.toString());
			Commandline cl = new Commandline(sb.toString());

			Process pb = cl.execute();
			// Consume subprocess output and write to stdout for debugging
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				System.out.write(singleByte);
			}
		} catch (CommandLineException e) {
			System.out.println("Packaging failed ");
			e.printStackTrace();
			throw new PhrescoException(e);
		} catch (IOException e) {
			System.out.println("Packaging failed ");
			e.printStackTrace();
			throw new PhrescoException(e);
		}

	}

}
