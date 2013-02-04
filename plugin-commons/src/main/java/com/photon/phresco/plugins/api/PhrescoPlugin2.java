package com.photon.phresco.plugins.api;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

/**
 * Additional methods to Phresco Plugin
 *
 */
public interface PhrescoPlugin2 extends PhrescoPlugin {

	ExecutionStatus processBuild(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
	
	/**
     * @param mavenProjectInfo customized Maven Project object
	 * @return TODO
     * 
     * @throws PhrescoException
     */
    ExecutionStatus themeValidator(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    /**
     * @param mavenProjectInfo customized Maven Project object
     * @return TODO
     * 
     * @throws PhrescoException
     */
    ExecutionStatus themeConvertor(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    /**
     * @param mavenProjectInfo customized Maven Project object
     * @return TODO
     * 
     * @throws PhrescoException
     */
    ExecutionStatus contentValidator(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
    /**
     * @param mavenProjectInfo customized Maven Project object
     * @return TODO
     * 
     * @throws PhrescoException
     */
    ExecutionStatus contentConvertor(MavenProjectInfo mavenProjectInfo) throws PhrescoException;
}
