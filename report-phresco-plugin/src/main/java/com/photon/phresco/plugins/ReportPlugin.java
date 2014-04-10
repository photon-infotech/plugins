/**
 * report-phresco-plugin
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
package com.photon.phresco.plugins;

import org.apache.maven.plugin.logging.*;

import com.photon.phresco.exception.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class ReportPlugin  extends PhrescoBasePlugin  {

	public ReportPlugin(Log log) {
		super(log);
	}

	@Override
	public ExecutionStatus generateReport(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		GenerateReport report = new GenerateReport();
		report.generate(configuration, mavenProjectInfo, getLog());
		return new DefaultExecutionStatus();
	}
	
}
