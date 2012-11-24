package com.photon.phresco.plugins;

import java.util.List;

public class XmlReport {
	private String fileName;
	private List<TestSuite> testSuites;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<TestSuite> getTestSuites() {
		return testSuites;
	}
	public void setTestSuites(List<TestSuite> testSuites) {
		this.testSuites = testSuites;
	}
}