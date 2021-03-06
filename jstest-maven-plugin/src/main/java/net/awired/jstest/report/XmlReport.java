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
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.awired.jscoverage.result.LcovWriter;
import net.awired.jstest.result.RunResult;
import net.awired.jstest.result.RunResults;
import net.awired.jstest.result.SuiteResult;
import com.google.common.base.Preconditions;

public class XmlReport implements Report {

    private static final String TEST_FILE_PREFIX = "TEST-";
    private static final String TEST_FILE_SUFFIX = ".xml";
    private static final String COVERAGE_FILE_NAME = "coverage.dat";

    private JAXBContext reportContext;
    private File reportDir;
    private LcovWriter lcovWriter = new LcovWriter();

    public XmlReport(File reportDir) {
        this.reportDir = reportDir;
        try {
            reportContext = JAXBContext.newInstance(SuiteResult.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reportSuite(SuiteResult suiteResult) {
        Preconditions.checkNotNull(suiteResult.getRunResult(), "RunResult must be set in SuiteResult");
        reportSuite(suiteResult, getCreatedAgentOutputDir(suiteResult.getRunResult()));
    }

    @Override
    public void reportRun(RunResult runResult) {
        reportRun(runResult, getCreatedAgentOutputDir(runResult));
    }

    @Override
    public void reportGlobal(RunResults runResults, String runnerTypeName) {
        RunResult aggregatedResult = runResults.buildAggregatedResult();
        for (SuiteResult suiteResult : aggregatedResult.getSuiteResults()) {
            reportSuite(suiteResult, reportDir);
        }
        reportRun(aggregatedResult, reportDir);
    }

    ///////////////////////

    private void reportRun(RunResult runResult, File outDir) {
        if (runResult.getCoverageResult() != null) {
            try {
                lcovWriter.write(new File(outDir, COVERAGE_FILE_NAME), runResult.getCoverageResult());
            } catch (IOException e) {
                throw new RuntimeException("Cannot write coverage result file", e);
            }
        }
    }

    private void reportSuite(SuiteResult suiteResult, File outDir) {

        try {
            Marshaller marshaller = reportContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(suiteResult, new File(outDir, TEST_FILE_PREFIX + suiteResult.getName()
                    + TEST_FILE_SUFFIX));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private File getCreatedAgentOutputDir(RunResult runResult) {
        File outDir = new File(reportDir, runResult.userAgentToString() + '-' + runResult.getBrowserId());
        outDir.mkdirs();
        return outDir;
    }

}
