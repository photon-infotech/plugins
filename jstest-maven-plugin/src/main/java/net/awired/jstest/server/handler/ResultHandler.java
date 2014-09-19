/**
 * JsTest Maven Plugin
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
package net.awired.jstest.server.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.awired.jscoverage.result.CoverageResult;
import net.awired.jscoverage.result.LcovWriter;
import net.awired.jstest.common.io.FileUtilsWrapper;
import net.awired.jstest.common.io.IOUtilsWrapper;
import net.awired.jstest.report.MultiReport;
import net.awired.jstest.result.RunResult;
import net.awired.jstest.result.RunResults;
import net.awired.jstest.result.SuiteResult;
import net.awired.jstest.result.SuiteResults;
import net.awired.jstest.result.TestResult;
import net.awired.jstest.runner.TestType;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.jetty.server.Request;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yahoo.platform.yuitest.coverage.report.CoverageReportGenerator;
import com.yahoo.platform.yuitest.coverage.report.CoverageReportGeneratorFactory;
import com.yahoo.platform.yuitest.coverage.results.SummaryCoverageReport;

public class ResultHandler {

    private final Log log;
    private ObjectMapper mapper = new ObjectMapper();
    private final RunResults runResults = new RunResults();
    private long lastAction;

    private final MultiReport report;
    private final TestType testType;
    private File reportDir;

    public ResultHandler(Log log, File reportDir, TestType testType) {
        this.log = log;
        this.reportDir = reportDir;
        System.out.println("reportDir " + reportDir);
        this.report = new MultiReport(log, reportDir);
        this.testType = testType;
    }

    public synchronized void handle(String target, Request baseRequest, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        lastAction = new Date().getTime();
        ServletInputStream inputStream = request.getInputStream();

        String testRunner = request.getParameter("testRunner");
        int browserId = Integer.valueOf(request.getParameter("browserId"));
        RunResult runResult = findRunResult(request, browserId);

        boolean handled = false;
        if (target.equals("/result/test")) {
            TestResult testResult = mapper.readValue(inputStream, TestResult.class);
            log.debug(testResult.toString());
            handled = true;
        } else if (target.equals("/result/suite")) {
    		SuiteResult suiteResult = mapper.readValue(inputStream, SuiteResult.class);
    		suiteResult.setRunResult(runResult);
    		runResult.addSuite(suiteResult);
    		report.reportSuite(suiteResult);
    		handled = true;
        } else if (target.equals("/result/run")) {
        	if (TestType.YUITEST.equals(testType)){
	        	File outDir = new File(reportDir, runResult.userAgentToString() + '-' + runResult.getBrowserId());
	        	InputStreamReader reader = new InputStreamReader(inputStream);
				try {
					SummaryCoverageReport fullReport = new SummaryCoverageReport(reader);
		        	reader.close();
		        	CoverageReportGenerator generator = CoverageReportGeneratorFactory.getGenerator("lcov", outDir.getAbsolutePath(), false);
		            generator.generate(fullReport);
		            CoverageResult coverageResult = new CoverageResult();
				} catch (Exception e) {
//					e.printStackTrace();
				}
	            runResult.setCoverageResult(null);
//	            runResults.put(browserId, runResult);
	            
	            runResult.setDuration(1000);
	            report.reportRun(runResult);
	            handled = true;
//				handled = true;
        	} else {	
	    		PartialRunResult runRes = mapper.readValue(inputStream, PartialRunResult.class);
	    		runResult.setDuration(runRes.getDuration());
	    		runResult.setCoverageResult(runRes.getCoverageResult());
	    		report.reportRun(runResult);
	            handled = true;
        	}
        }  else if (target.equals("/log")) {
        	BufferedReader reader = null;
        	try {
        		String line;
        		StringBuilder sb = new StringBuilder();
        		reader = new BufferedReader(new InputStreamReader(inputStream));
        		while ((line = reader.readLine()) != null) {
        			sb.append(line); 
        		}
        		log.info(sb);
        		handled = true;
        	} catch (Exception e) {
        		e.printStackTrace();
        	} finally {
        		if (reader != null) {
        			reader.close();
        		}
        	}
        }
        
        if (handled) {
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
        }
    }
    
    private RunResult findRunResult(HttpServletRequest request, int browserId) {
        RunResult runResult = runResults.get(browserId);
        if (runResult == null) {
            runResult = new RunResult(browserId);
            runResult.setBrowserType(request.getHeader("User-Agent"));
            String parameter = request.getParameter("emulator");
            if (parameter != null) {
                runResult.setEmulator(true);
            }
            runResults.put(browserId, runResult);
        }
        return runResult;
    }

    /**
     * @return true on success, false on timeout
     */
    public boolean waitAllResult(long timeoutNoActions, long poolWait, String runnerTypeName) {
        lastAction = new Date().getTime();
        while (lastAction + timeoutNoActions > new Date().getTime()) {
            if (runResults.isFullyFinished()) {
                report.reportGlobal(runResults, runnerTypeName);
                return true;
            }
            try {
                Thread.sleep(poolWait);
            } catch (InterruptedException e) {
                throw new RuntimeException("Result wait interrupted", e);
            }
        }
        return false;
    }

    public RunResults getRunResults() {
        return runResults;
    }

    /////////
    //////////
    ///////////

    public static final String BUILD_REPORT_JS = "/lib/buildReport.js";
    public static final String CREATE_JUNIT_XML = "/lib/createJunitXml.js";

    private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
    private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
    private LcovWriter lcovWriter = new LcovWriter();
    String jsGetCoverageScript = "JSCOV.storeCurrentRunResult();"
            + "return JSON.stringify(JSCOV.getStoredRunResult());";
    private TypeReference<List<CoverageResult>> typeRef = new TypeReference<List<CoverageResult>>() {
    };

    //
    //    public void toto(File junitXmlReport, boolean coverage, File coverageReportFile) {
    //        JasmineResult jasmineResult = new JasmineResult();
    //        jasmineResult.setDetails(buildReport(executor, format));
    //        fileUtilsWrapper.writeStringToFile(junitXmlReport, buildJunitXmlReport(executor, debug), "UTF-8");
    //        if (coverage) {
    //            JsRunResult coverageReport = buildCoverageReport(executor);
    //            lcovWriter.write(coverageReportFile, coverageReport);
    //        }
    //
    //        driver.quit();
    //
    //    }
    //
    //    private String buildReport(JavascriptExecutor driver, String format) throws IOException {
    //        String script = ioUtilsWrapper.toString(BUILD_REPORT_JS)
    //                + "return jasmineMavenPlugin.printReport(window.reporter,{format:'" + format + "'});";
    //        Object report = driver.executeScript(script);
    //        return report.toString();
    //    }
    //
    //    private String buildJunitXmlReport(JavascriptExecutor driver, boolean debug) throws IOException {
    //        Object junitReport = driver.executeScript(ioUtilsWrapper.toString(CREATE_JUNIT_XML)
    //                + "return junitXmlReporter.report(reporter," + debug + ");");
    //        return junitReport.toString();
    //    }
    //
    //    private JsRunResult buildCoverageReport(JavascriptExecutor driver) {
    //        Object junitReport = driver.executeScript(jsGetCoverageScript);
    //        try {
    //            List<JsRunResult> runResults = mapper.readValue((String) junitReport, typeRef);
    //            if (runResults.size() == 0) {
    //                throw new IllegalStateException("No coverage report found ");
    //            }
    //            return runResults.get(0);
    //        } catch (Exception e) {
    //            throw new IllegalStateException("Cannot parse coverage result", e);
    //        }
    //    }

}
