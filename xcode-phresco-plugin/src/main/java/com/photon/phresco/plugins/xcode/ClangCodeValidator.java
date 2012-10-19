package com.photon.phresco.plugins.xcode;

import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class ClangCodeValidator implements PluginConstants {
	
	public void validate(Configuration configuration) {
		StringBuilder sb = new StringBuilder();
		sb.append(XCODE_CODE_VALIDATOR_COMMAND);
	}
}
