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
package net.awired.jstest.report;

import java.io.File;
import net.awired.jstest.result.RunResult;
import net.awired.jstest.result.RunResults;
import net.awired.jstest.result.SuiteResult;
import org.apache.maven.plugin.logging.Log;

public class MultiReport implements Report {

    private OutputReport outputReport;
    private XmlReport xmlReport;

    public MultiReport(Log log, File reportDir) {
        outputReport = new OutputReport(log);
        xmlReport = new XmlReport(reportDir);
    }

    @Override
    public void reportSuite(SuiteResult suiteResult) {
        outputReport.reportSuite(suiteResult);
        xmlReport.reportSuite(suiteResult);
    }

    @Override
    public void reportRun(RunResult runResult) {
        outputReport.reportRun(runResult);
        xmlReport.reportRun(runResult);
    }

    @Override
    public void reportGlobal(RunResults runResults, String runnerTypeName) {
        outputReport.reportGlobal(runResults, runnerTypeName);
        xmlReport.reportGlobal(runResults, runnerTypeName);
    }

}
