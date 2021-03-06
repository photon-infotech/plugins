/**
 * Phresco Plugin Commons
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
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

public interface CIPlugin extends PhrescoPlugin {

	 
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
    ExecutionStatus performCIPreBuildStep(String jobName, String goal, String phase, String creationType, String id, String continuousDeliveryName, String moduleName, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
    
}
