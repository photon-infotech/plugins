package com.photon.phresco.plugins;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;

public abstract class PhrescoAbstractPlugin implements PhrescoPlugin {

    public Log log;
    
    public PhrescoAbstractPlugin(Log log) {
        this.log = log;
    }
    
    protected final Log getLog() {
        return log;
    }
    
    public void validate() throws PhrescoException {
        // TODO Auto-generated method stub
    }
    
    public void runAgainstSource() throws PhrescoException {
        
    }

    public void runUnitTest() throws PhrescoException {
        // TODO Auto-generated method stub
    }

    public void runFunctionalTest() throws PhrescoException {
        // TODO Auto-generated method stub
        
    }

    public void runPerformanceTest() throws PhrescoException {
        // TODO Auto-generated method stub
        
    }

    public void runLoadTest() throws PhrescoException {
        // TODO Auto-generated method stub
        
    }

}
