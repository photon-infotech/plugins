package com.photon.phresco.plugins;

import java.util.List;

public class SureFireReport {
	//custom report
	private List<AllTestSuite> allTestSuites;
	//detail report
	private List<TestSuite> testSuites;
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
}
