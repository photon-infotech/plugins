package com.photon.phresco.plugins.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MojoUtil;

public class RunAndroidTest implements PluginConstants {
	
	public static void runAndroidTest(Configuration configuration, MavenProjectInfo mavenProjectInfo, String workingDir) throws PhrescoException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_CLEAN);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_INSTALL);
			
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String deviceValue = configs.get(DEVICES);
			String signing = configs.get(SIGNING);
			
			Boolean isSigning = Boolean.valueOf(signing);
			System.out.println("isSigning . " + isSigning);
			//signing
			if (isSigning) {
				sb.append(STR_SPACE);
				sb.append(PSIGN);
			}
			
			String otherDiviceValue = configs.get(deviceValue);
			List<Parameter> parameters = configuration.getParameters().getParameter();
			for (Parameter parameter : parameters) {
				if (parameter.getPluginParameter() != null && parameter.getPluginParameter().equals(PluginConstants.PLUGIN_PARAMETER)) {
					List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
					for (MavenCommand mavenCommand : mavenCommands) {
						if(mavenCommand.getKey().equalsIgnoreCase(deviceValue)) {
							sb.append(STR_SPACE);
							sb.append(mavenCommand.getValue());
						} 
					}
				}
				if(parameter.getKey().equalsIgnoreCase(deviceValue)) {
					sb.append(STR_SPACE);
					sb.append(HYPHEN_D + ANDROID_DEVICE + EQUAL + otherDiviceValue);
				}
			}
			sb.append(STR_SPACE);
			sb.append(HYPHEN_D + ANDROID_EMULATOR + EQUAL + DEFAULT);
			
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
		} catch (CommandLineException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
	}
}
