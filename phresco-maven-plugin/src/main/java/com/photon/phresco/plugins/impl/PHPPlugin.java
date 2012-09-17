package com.photon.phresco.plugins.impl;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.phpplugin.PhpPackage;

public class PHPPlugin extends PhrescoAbstractPlugin implements PluginConstants {
    
    private PhpPackage pack;
    
    public PHPPlugin(Log log) {
        super(log);
    }
    
    public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
        pack = new PhpPackage();
        pack.pack(configuration, mavenProjectInfo, log);
    }

    public void deploy() throws PhrescoException {
        pack = new PhpPackage();
    }
}
