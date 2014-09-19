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

import java.io.PrintStream;
import java.text.DecimalFormat;

import net.awired.jstest.common.TestPluginConstants;
import net.awired.jstest.result.RunResult;
import net.awired.jstest.result.RunResults;
import net.awired.jstest.result.SuiteResult;

import org.apache.maven.plugin.logging.Log;

import com.google.common.base.Preconditions;

public class OutputReport implements Report, TestPluginConstants {

    private static final String HEADER = "\n" //
            + "-------------------------------------------------------\n" //
            + " J S   T E S T S\n" //
            + "-------------------------------------------------------";

    private boolean firstReceived;
    private PrintStream out = System.out;
    private final Log log;
    private DecimalFormat coverageFormat = new DecimalFormat("#.#");

    public OutputReport(Log log) {
        this.log = log;
    }

    @Override
    public void reportSuite(SuiteResult suiteResult) {
        Preconditions.checkNotNull(suiteResult.getRunResult(), "RunResult must be set in SuiteResult");
        checkPrintHeader();

        StringBuilder builder = new StringBuilder();
        builder.append("Run suite: ");
        builder.append(suiteResult.getName());
        builder.append(", Agent: ");
        builder.append(suiteResult.getRunResult().userAgentToString());
        builder.append("\nTests run: ");
        builder.append(suiteResult.getTests().size());
        builder.append(", Failures: ");
        builder.append(suiteResult.getFailures());
        builder.append(", Errors: ");
        builder.append(suiteResult.getErrors());
        builder.append(", Skipped: ");
        builder.append(suiteResult.getSkipped());
        builder.append(", Time elapsed: ");
        builder.append(suiteResult.getDuration());
        builder.append(" ms");
        out.println(builder);
    }

    private void checkPrintHeader() {
        if (!firstReceived) {
            out.println(HEADER);
            firstReceived = true;
        }
    }

    @Override
    public void reportRun(RunResult runresult) {
    }

    @Override
    public void reportGlobal(RunResults runResults, String runnerTypeName) {
        StringBuilder builder = new StringBuilder();
        boolean firstParsed = false;

        if (JASMINE2.equalsIgnoreCase(runnerTypeName)) {
        	builder.append("[INFO] For coverage, check with sonar");
        } else {
        	builder.append("\nResults :\n\n");
        	for (RunResult result : runResults.values()) {
                if (!firstParsed) {
                    builder.append("Run: ");
                    builder.append(result.findTests());
                    builder.append(", Failures: ");
                    builder.append(result.findFailures());
                    builder.append(", Errors: ");
                    builder.append(result.findErrors());
                    builder.append(", Skipped: ");
                    builder.append(result.findSkipped());
                    builder.append(", Time elapsed: ");
                    builder.append(result.getDuration());
                    builder.append("ms");
                    if (result.getCoverageResult() != null) {
                        builder.append(", Coverage for executed source scripts: ");
                        builder.append(coverageFormat.format(result.getCoverageResult().findCoveragePercent()));
                        builder.append('%');
                    }
                    builder.append(", Agent: ");
                    builder.append(result.userAgentToString());
                    builder.append("\n");
    				builder.append("[INFO] For total coverage check with sonar");
                    builder.append("\n");
                }
            }
        }
                
        out.println(builder);
    }

}
