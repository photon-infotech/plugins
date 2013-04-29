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
/**
 * 
 */
package com.photon.phresco.plugins.impl;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.api.PhrescoPlugin2;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

/**
 * Abstract implementation of PhrescoPlugin. Subclasses can extend this class and override the functions.
 */
public abstract class AbstractPhrescoPlugin2 implements PhrescoPlugin2 {

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#validate(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus validate(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#pack(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#deploy(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#startServer(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus startServer(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#stopServer(com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus stopServer(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#runUnitTest(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus runUnitTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}
	
	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#runComponentTest(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus runComponentTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#runFunctionalTest(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus runFunctionalTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#runPerformanceTest(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus runPerformanceTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#runLoadTest(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus runLoadTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin#generateReport(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus generateReport(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin2#processBuild(com.photon.phresco.plugins.model.Mojos.Mojo.Configuration, com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus processBuild(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin2#themeValidator(com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus themeValidator(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin2#themeConvertor(com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus themeConvertor(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin2#contentValidator(com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus contentValidator(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		return new DefaultExecutionStatus();

	}

	/* (non-Javadoc)
	 * @see com.photon.phresco.plugins.api.PhrescoPlugin2#contentConvertor(com.photon.phresco.plugin.commons.MavenProjectInfo)
	 */
	public ExecutionStatus contentConvertor(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		return new DefaultExecutionStatus();
	}

}
