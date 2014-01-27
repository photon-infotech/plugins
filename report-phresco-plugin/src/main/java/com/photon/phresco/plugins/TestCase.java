package com.photon.phresco.plugins;

import java.util.List;

public class TestCase {

	private String name = "";
	private String testClass = "";
	private String file = "";
	private boolean isStepsAvailable;
	private String featureId = "";
	private String testCaseId = "";
	private String expectedResult = "";
	private String actualResult = "";
	private String status = "";
	private float line;
	private float assertions;
	private String time = "";;
	private TestCaseFailure testCaseFailure;
	private TestCaseError testCaseError;
	private String bugComment = "";
	private List<TestStep> steps;

	public TestCase() {

	}

	public TestCase(String name, String testClass, String file, float line,
			float assertions, String time) {
		this.name = name;
		this.testClass = testClass;
		this.file = file;
		this.line = line;
		this.assertions = assertions;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public float getLine() {
		return line;
	}

	public void setLine(float line) {
		this.line = line;
	}

	public float getAssertions() {
		return assertions;
	}

	public void setAssertions(float assertions) {
		this.assertions = assertions;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public TestCaseFailure getTestCaseFailure() {
		return testCaseFailure;
	}

	public void setTestCaseFailure(TestCaseFailure testCaseFailure) {
		this.testCaseFailure = testCaseFailure;
	}

	public TestCaseError getTestCaseError() {
		return testCaseError;
	}

	public void setTestCaseError(TestCaseError testCaseError) {
		this.testCaseError = testCaseError;
	}

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public String getActualResult() {
		return actualResult;
	}

	public void setActualResult(String actualResult) {
		this.actualResult = actualResult;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBugComment() {
		return bugComment;
	}

	public void setBugComment(String bugComment) {
		this.bugComment = bugComment;
	}

	public List<TestStep> getSteps() {
		return steps;
	}

	public void setSteps(List<TestStep> steps) {
		this.steps = steps;
	}

	public boolean isStepsAvailable() {
		return isStepsAvailable;
	}

	public void setStepsAvailable(boolean isStepsAvailable) {
		this.isStepsAvailable = isStepsAvailable;
	}
}
