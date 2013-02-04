package com.photon.phresco.plugins.api;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public interface SeleniumPlugin {
	
	 /**
     * Start the hub for seleninum grid
     * 
     * @param configuration project configuration
     * @param mavenProjectInfo customized Maven Project object
     * @return ExecutionStatus
     * 
     * @throws PhrescoException
     */
    ExecutionStatus startHub(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    /**
     * Stop the hub
     * 
     * @param mavenProjectInfo customized Maven Project object
     * @return ExecutionStatus
     * 
     * @throws PhrescoException
     */
    ExecutionStatus stopHub(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    /**
     * Start the node
     * 
     * @param configuration project configuration
     * @param mavenProjectInfo customized Maven Project object
     * @return ExecutionStatus
     * 
     * @throws PhrescoException
     */
    ExecutionStatus startNode(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    /**
     * Stop the node
     * 
     * @param mavenProjectInfo customized Maven Project object
     * @return ExecutionStatus
     * 
     * @throws PhrescoException
     */
    ExecutionStatus stopNode(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
   

}
