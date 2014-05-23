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
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

/**
 * Base Interface to phresco defined maven goals
 * 
 * Subclasses need to implement the functionalities for each goal. AbstractPhrescoPlugin can be used.
 *
 */
public interface PhrescoPlugin {

	/**
	 * Validates the project
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus validate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for build operation
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for deploy operation.
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for starting the server
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus startServer(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for stoping the server
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus stopServer(MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for running the unit tests
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus runUnitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for running the component tests
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus runComponentTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for running the functional tests
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus runFunctionalTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for running performance tests
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus runPerformanceTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for running load tests
	 * 
	 * @param configuration project configuration
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus runLoadTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * Goal for running integration tests
	 * @param configuration
	 * @param mavenProjectInfo
	 * @return
	 * 
	 * @throws PhrescoException
	 */
	ExecutionStatus runIntegrationTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;

	/**
	 * @param generate Report
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */

	ExecutionStatus generateReport(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;
	
	/**
	 * @param Zap Test
	 * @param mavenProjectInfo customized Maven Project object
	 * @return ExecutionStatus
	 * 
	 * @throws PhrescoException
	 */
	
	ExecutionStatus zapTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException;



}