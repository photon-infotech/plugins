package com.photon.phresco.plugins.api;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;

public interface CIPlugin {

	 
    /**
     * @param jobName
     * @param goal
     * @param phase
     * @param mavenProjectInfo
     * 
     * @return ExecutionStatus
     * 
     * @throws PhrescoException
     */
    ExecutionStatus performCIPreBuildStep(String jobName, String goal, String phase, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
}
