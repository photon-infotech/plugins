/**
 * android-phresco-plugin
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
package com.photon.phresco.plugins.android;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.PhrescoBasePlugin;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class AndroidPlugin extends PhrescoBasePlugin {

	public AndroidPlugin(Log log) {
		super(log);
	}

	@Override
	public ExecutionStatus pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		writePhrescoBuildXml(configuration, mavenProjectInfo);
		Package pack = new Package();
		pack.pack(configuration, mavenProjectInfo, log);
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		Deploy deploy = new Deploy();
		try {
			deploy.deploy(configuration, mavenProjectInfo, log);
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus runUnitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException{
		UnitTest test = new UnitTest();
		test.unitTest(configuration, mavenProjectInfo);
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus runFunctionalTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		FunctionalTest functionalTest = new FunctionalTest();
		functionalTest.functionalTest(configuration, mavenProjectInfo);
		return new DefaultExecutionStatus();
	}
	
	@Override
	public ExecutionStatus runPerformanceTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		PerformanceTest performanceTest = new PerformanceTest();
		performanceTest.performanceTest(configuration, mavenProjectInfo);
		return new DefaultExecutionStatus();
	}
}
