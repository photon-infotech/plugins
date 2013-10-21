package com.photon.phresco.plugins;

import java.util.ArrayList;
import java.util.List;

public class MultiModuleReports {
	private String applicationName; // projectCode this.projectCode = appInfo.getName();
	private String projectName; //projName
	private Boolean isRootModule;
	private String applicationLabel;
	private String technologyName;
	private String version;
	
	private String reportType;
	private Boolean isAndroidSpecialHandling;
	private Boolean isClassEmpty; // this is for only SureFireReport Object
	private Boolean isClangReport;
	
	private String logo = null;
	// TODO: crisp and detail, design and Page number impl, separate jrxmls for this one
	// TODO: Order preserving on multi module objects, root, multi object
	
	// KNOWN: if it is iPhone code validation as a second module, iphone codevalidation report will be generated at the end (This wont come on multi module) 
	// KNOWN: js and java report should be separated on unit test (Unit test check) - enhancement
	
	// TODO: Remove existing multi module implementation
	// TODO: Page No
	
	// unit test obj
	private SureFireReport unitTestReport;
	
	// functional test obj
	private SureFireReport functionalTestReport;
	
	// component test obj
	private SureFireReport componentTestReport;
	
	// manual test obj
	private SureFireReport manualTestReport;
	
	// perforamce test obj
	List<AndroidPerfReport> androidPerformaceTestReport;
	
	List<JmeterTypeReport> performanceTestReport;
	// load test obj
	List<JmeterTypeReport> loadTestReport;
	
	// code validation report
	List<SonarReport> codeValidationReports;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Boolean getIsRootModule() {
		return isRootModule;
	}

	public void setIsRootModule(Boolean isRootModule) {
		this.isRootModule = isRootModule;
	}

	public String getApplicationLabel() {
		return applicationLabel;
	}

	public void setApplicationLabel(String applicationLabel) {
		this.applicationLabel = applicationLabel;
	}

	public String getTechnologyName() {
		return technologyName;
	}

	public void setTechnologyName(String technologyName) {
		this.technologyName = technologyName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public Boolean getIsAndroidSpecialHandling() {
		return isAndroidSpecialHandling;
	}

	public void setIsAndroidSpecialHandling(Boolean isAndroidSpecialHandling) {
		this.isAndroidSpecialHandling = isAndroidSpecialHandling;
	}

	public Boolean getIsClassEmpty() {
		return isClassEmpty;
	}

	public void setIsClassEmpty(Boolean isClassEmpty) {
		this.isClassEmpty = isClassEmpty;
	}

	public Boolean getIsClangReport() {
		return isClangReport;
	}

	public void setIsClangReport(Boolean isClangReport) {
		this.isClangReport = isClangReport;
	}

	public SureFireReport getUnitTestReport() {
		return unitTestReport;
	}

	public void setUnitTestReport(SureFireReport unitTestReport) {
		this.unitTestReport = unitTestReport;
	}

	public SureFireReport getFunctionalTestReport() {
		return functionalTestReport;
	}

	public void setFunctionalTestReport(SureFireReport functionalTestReport) {
		this.functionalTestReport = functionalTestReport;
	}

	public SureFireReport getComponentTestReport() {
		return componentTestReport;
	}

	public void setComponentTestReport(SureFireReport componentTestReport) {
		this.componentTestReport = componentTestReport;
	}

	public SureFireReport getManualTestReport() {
		return manualTestReport;
	}

	public void setManualTestReport(SureFireReport manualTestReport) {
		this.manualTestReport = manualTestReport;
	}

	public List<AndroidPerfReport> getAndroidPerformaceTestReport() {
		return androidPerformaceTestReport;
	}

	public void setAndroidPerformaceTestReport(
			List<AndroidPerfReport> androidPerformaceTestReport) {
		this.androidPerformaceTestReport = androidPerformaceTestReport;
	}

	public List<JmeterTypeReport> getPerformanceTestReport() {
		return performanceTestReport;
	}

	public void setPerformanceTestReport(
			List<JmeterTypeReport> performanceTestReport) {
		this.performanceTestReport = performanceTestReport;
	}

	public List<JmeterTypeReport> getLoadTestReport() {
		return loadTestReport;
	}

	public void setLoadTestReport(List<JmeterTypeReport> loadTestReport) {
		this.loadTestReport = loadTestReport;
	}

	public List<SonarReport> getCodeValidationReports() {
		return codeValidationReports;
	}

	public void setCodeValidationReports(List<SonarReport> codeValidationReports) {
		this.codeValidationReports = codeValidationReports;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
}
