package com.photon.phresco.plugins.impl;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;

public class PHPPlugin extends PhrescoAbstractPlugin {
    
    public PHPPlugin(Log log) {
        super(log);
    }

    public void pack() throws PhrescoException {
        getLog().info("Inside PHP package");
    }

    public void deploy() throws PhrescoException {
        // TODO Auto-generated method stub
        
    }

}
