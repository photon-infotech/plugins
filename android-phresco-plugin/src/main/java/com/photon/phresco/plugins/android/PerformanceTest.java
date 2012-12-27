package com.photon.phresco.plugins.android;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.*;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.*;
import com.photon.phresco.util.Constants;

public class PerformanceTest implements PluginConstants {

	public void performanceTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String deviceList= configs.get(DEVICES_LIST);
			String signing = configs.get(SIGNING);
			
			if (StringUtils.isEmpty(deviceList)) {
				System.out.println("Device list is empty . ");
				throw new PhrescoException("Device list is empty . ");
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_CLEAN);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_INSTALL);
			
			Boolean isSigning = Boolean.valueOf(signing);
			System.out.println("isSigning . " + isSigning);
			//signing
			if (isSigning) {
				sb.append(STR_SPACE);
				sb.append(PSIGN);
			}
			
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + DEVICES_LIST + EQUAL + deviceList);
			
			MavenProject project = mavenProjectInfo.getProject();
			String workingDir = project.getProperties().getProperty(Constants.POM_PROP_KEY_PERFORMANCETEST_DIR);
			
			System.out.println("Command " + sb.toString());
			Commandline commandline = new Commandline(sb.toString());
			String baseDir = mavenProjectInfo.getBaseDir().getPath();
			if (StringUtils.isNotEmpty(workingDir)) {
				commandline.setWorkingDirectory(baseDir + workingDir);
			}
			
			Process pb = commandline.execute();
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				System.out.write(singleByte);
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
}
