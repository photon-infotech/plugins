package com.photon.phresco.plugins.api;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public interface PhrescoPlugin {
    
    void validate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void startServer(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void stopServer(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void runUnitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void runFunctionalTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void runPerformanceTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void startHub(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void stopHub(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void startNode(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void stopNode(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void runLoadTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    void performCIPreBuildStep(String jobName, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    public void generateReport(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
}
