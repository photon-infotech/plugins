package com.photon.phresco.plugins.xcode;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class ClangCodeValidator implements PluginConstants {
	
	public void validate(Configuration configuration) throws PhrescoException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(XCODE_CODE_VALIDATOR_COMMAND);
			Commandline commandline = new Commandline(sb.toString());
			Process pb = commandline.execute();
			InputStream is = new BufferedInputStream(pb.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				//output.write(buffer, 0, bytesRead);
				System.out.write(singleByte);
			}
		} catch (CommandLineException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
	}
}
