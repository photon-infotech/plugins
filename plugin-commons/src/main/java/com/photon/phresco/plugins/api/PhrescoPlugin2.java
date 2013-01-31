package com.photon.phresco.plugins.api;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public interface PhrescoPlugin2 extends PhrescoPlugin {

	void processBuild(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
}
