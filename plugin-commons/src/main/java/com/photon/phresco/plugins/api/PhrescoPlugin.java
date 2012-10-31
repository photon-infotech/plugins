package com.photon.phresco.plugins.api;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public interface PhrescoPlugin {
    
    void validate(Configuration configuration) throws PhrescoException;
    
    void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void startServer(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void stopServer(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void runUnitTest(Configuration configuration) throws PhrescoException;
    
    void runFunctionalTest(Configuration configuration) throws PhrescoException;
    
    void runPerformanceTest() throws PhrescoException;
    
    void runLoadTest() throws PhrescoException;
    
    void performCIPreBuildStep(String jobName, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
}
