package net.awired.jstest.server.handler;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.awired.jscoverage.result.CoverageResult;
import net.awired.jscoverage.result.LcovWriter;
import net.awired.jstest.common.io.FileUtilsWrapper;
import net.awired.jstest.common.io.IOUtilsWrapper;
import net.awired.jstest.report.MultiReport;
import net.awired.jstest.result.RunResult;
import net.awired.jstest.result.RunResults;
import net.awired.jstest.result.SuiteResult;
import net.awired.jstest.result.TestResult;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.jetty.server.Request;

public class ResultHandler {

    private final Log log;
    private ObjectMapper mapper = new ObjectMapper();
    private final RunResults runResults = new RunResults();
    private long lastAction;

    private final MultiReport report;

    public ResultHandler(Log log, File reportDir) {
        this.log = log;
        this.report = new MultiReport(log, reportDir);
    }

    public synchronized void handle(String target, Request baseRequest, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        lastAction = new Date().getTime();
        ServletInputStream inputStream = request.getInputStream();

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
            PartialRunResult runRes = mapper.readValue(inputStream, PartialRunResult.class);
            runResult.setDuration(runRes.getDuration());
            runResult.setCoverageResult(runRes.getCoverageResult());
            report.reportRun(runResult);
            handled = true;
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
    public boolean waitAllResult(long timeoutNoActions, long poolWait) {
        lastAction = new Date().getTime();
        while (lastAction + timeoutNoActions > new Date().getTime()) {
            if (runResults.isFullyFinished()) {
                report.reportGlobal(runResults);
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
