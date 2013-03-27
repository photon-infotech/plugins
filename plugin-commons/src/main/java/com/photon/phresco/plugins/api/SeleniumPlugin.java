/**
 * Phresco Plugin Commons
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
