package com.photon.phresco.plugins.xcode;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.lang.*;
import org.apache.maven.plugin.logging.*;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.*;

public class ClangCodeValidator implements PluginConstants {
	
	private Log log;
	/**
	 * Execute the xcode command line utility for iphone code validation.
	 * @throws PhrescoException 
	 */
	public void validate(Configuration config, MavenProjectInfo mavenProjectInfo, final Log log) throws PhrescoException {
		try {
			this.log = log;
			log.debug("Iphone code validation started ");
			Map<String, String> configs = MojoUtil.getAllValues(config);
			
			String target = configs.get(TARGET);
			String projectType = configs.get(PROJECT_TYPE);
			
			if (StringUtils.isEmpty(target)) {
				System.out.println("Target is empty for code validation . ");
				throw new PhrescoException("Target is empty for code validation .");
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(XCODE_CODE_VALIDATOR_COMMAND);
			
			sb.append(STR_SPACE);
//			sb.append("-DprojectType=" + projectType);
			sb.append(HYPHEN_D + PROJECT_TYPE + EQUAL + projectType);
			
			sb.append(STR_SPACE);
//			sb.append("-Dscheme=" + target);
			sb.append(HYPHEN_D + SCHEME + EQUAL + target);
			
			log.debug("Command " + sb.toString());
			Commandline commandline = new Commandline(sb.toString());
			Process pb = commandline.execute();
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				System.out.write(singleByte);
			}
		} catch (CommandLineException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
	}
}
