package com.photon.phresco.plugins;

import java.util.List;

public class LoadTestReport {
	private String fileName;
	private List<TestResult> testResults;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<TestResult> getTestResults() {
		return testResults;
	}
	public void setTestResults(List<TestResult> testResults) {
		this.testResults = testResults;
	}

}