package com.photon.phresco.plugins.api;

import com.photon.phresco.exception.PhrescoException;

public interface PhrescoPlugin {
    
    void validate() throws PhrescoException;
    
    void pack() throws PhrescoException;
    
    void deploy() throws PhrescoException;
    
    void runAgainstSource() throws PhrescoException;
    
    void runUnitTest() throws PhrescoException;
    
    void runFunctionalTest() throws PhrescoException;
    
    void runPerformanceTest() throws PhrescoException;
    
    void runLoadTest() throws PhrescoException;
    
}
