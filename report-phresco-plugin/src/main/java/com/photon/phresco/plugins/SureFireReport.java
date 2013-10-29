/**
 * report-phresco-plugin
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
package com.photon.phresco.plugins;

import java.util.List;

public class SureFireReport {
	//custom report
	private List<AllTestSuite> allTestSuites;
	//detail report
	private List<TestSuite> testSuites;
	
	//js custom report
	private List<AllTestSuite> jsAllTestSuites;
	//js detail report
	private List<TestSuite> jsTestSuites;
	
	public List<AllTestSuite> getAllTestSuites() {
		return allTestSuites;
	}
	public void setAllTestSuites(List<AllTestSuite> allTestSuites) {
		this.allTestSuites = allTestSuites;
	}
	public List<TestSuite> getTestSuites() {
		return testSuites;
	}
	public void setTestSuites(List<TestSuite> testSuites) {
		this.testSuites = testSuites;
	}
	public List<AllTestSuite> getJsAllTestSuites() {
		return jsAllTestSuites;
	}
	public void setJsAllTestSuites(List<AllTestSuite> jsAllTestSuites) {
		this.jsAllTestSuites = jsAllTestSuites;
	}
	public List<TestSuite> getJsTestSuites() {
		return jsTestSuites;
	}
	public void setJsTestSuites(List<TestSuite> jsTestSuites) {
		this.jsTestSuites = jsTestSuites;
	}
}
