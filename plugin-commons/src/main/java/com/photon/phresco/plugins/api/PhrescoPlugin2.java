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
