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

public class TestSuite {

	private String name;
	private String file;
	private String assertions;
	private float tests;
	private float failures;
	private float errors;
	private float total;
	private float testCoverage;
	private String time;
	private List<TestCase> testCases;

	public TestSuite() {
	}

	public TestSuite(String name, String file, String assertions, float tests,
			float failures, float errors, String time) {
		this.name = name;
		this.file = file;
		this.tests = tests;
		this.assertions = assertions;
		this.failures = failures;
		this.errors = errors;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getAssertions() {
		return assertions;
	}

	public void setAssertions(String assertions) {
		this.assertions = assertions;
	}

	public float getTests() {
		return tests;
	}

	public void setTests(float tests) {
		this.tests = tests;
	}

	public float getFailures() {
		return failures;
	}

	public void setFailures(float failures) {
		this.failures = failures;
	}

	public float getErrors() {
		return errors;
	}

	public void setErrors(float errors) {
		this.errors = errors;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<TestCase> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public float getTestCoverage() {
		return testCoverage;
	}

	public void setTestCoverage(float testCoverage) {
		this.testCoverage = testCoverage;
	}

}
