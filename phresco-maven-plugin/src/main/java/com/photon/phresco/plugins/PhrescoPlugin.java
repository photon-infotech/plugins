package com.photon.phresco.plugins;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public interface PhrescoPlugin {
    
    void validate() throws PhrescoException;
    
    void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void runAgainstSource() throws PhrescoException;
    
    void runUnitTest() throws PhrescoException;
    
    void runFunctionalTest() throws PhrescoException;
    
    void runPerformanceTest() throws PhrescoException;
    
    void runLoadTest() throws PhrescoException;
    
}
