/**
 * JsTest Maven Plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
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
package net.awired.jstest.mojo;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.awired.jstest.executor.RunnerExecutor;
import net.awired.jstest.mojo.inherite.AbstractJsTestMojo;
import net.awired.jstest.resource.ResourceDirectory;
import net.awired.jstest.resource.ResourceResolver;
import net.awired.jstest.result.RunResult;
import net.awired.jstest.server.JsTestServer;
import net.awired.jstest.server.handler.JsTestHandler;
import net.awired.jstest.server.handler.ResultHandler;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;

/**
 * @component
 * @goal test
 * @phase test
 * @execute lifecycle="jstest-lifecycle" phase="process-test-resources""
 */
public class TestMojo extends AbstractJsTestMojo {

    private static final String ERROR_MSG = "There are test failures.\n\nPlease refer to %s for the individual test results.";

    @Override
    public void run() throws MojoExecutionException, MojoFailureException {
        if (isSkipTests()) {
            getLog().info("Skipping JsTest");
            return;
        }
        
        JsTestServer jsTestServer = new JsTestServer(getLog(), getTestPort(), isTestPortFindFree());
        RunnerExecutor executor = null;
        try {
        	String testSourceDir = buildTestResourceDirectory().getDirectory().getPath();
        	File testSourcedir = new File(testSourceDir);
        
        	// skipping jstest when source js directory or test js directory not exists 
        	
        	if (!testSourcedir.exists() || !getSourceDir().exists()) {
        		getLog().info("Source js files does not exist");
        		return;
        	}
            ResourceResolver resourceResolver = new ResourceResolver(getLog(), buildCurrentSrcDir(false),
                    buildTestResourceDirectory(), buildOverlaysResourceDirectories(),
                    new ArrayList<ResourceDirectory>());
            ResultHandler resultHandler = new ResultHandler(getLog(), getPreparedReportDir(), buildTestType(resourceResolver));
            
            JsTestHandler jsTestHandler = new JsTestHandler(resultHandler, getLog(), resourceResolver,
                    buildAmdRunnerType(), buildTestType(resourceResolver), false, getLog().isDebugEnabled(),
                    getAmdPreloads(), getTargetSourceDirectory());
            
            List<Handler> handlers = new ArrayList<Handler>(2);
            handlers.add(jsTestHandler);
            
            HandlerCollection handlerCollect = new HandlerCollection();
            handlerCollect.setHandlers(handlers.toArray(new Handler[handlers.size()]));
            jsTestServer.startServer(handlerCollect);

            if (isEmulator()) {
                executor = new RunnerExecutor();
                executor.execute(new URL("http://localhost:" + getDevPort() + "/?emulator=true"));
            }

            // let browsers detect that server is back
            Thread.sleep(7000);
            if (!resultHandler.waitAllResult(10000, 1000)) {
                throw new MojoFailureException("Do not receive all test results from clients");
            }

            RunResult buildAggregatedResult = resultHandler.getRunResults().buildAggregatedResult();
            if (buildAggregatedResult.findErrors() > 0 || buildAggregatedResult.findFailures() > 0) {
                String message = String.format(ERROR_MSG, getPreparedReportDir());
                if (isIgnoreFailure()) {
                    getLog().error(message);
                } else {
                    throw new MojoFailureException(message);
                }
            }
//          jsTestServer.join();
        } catch (MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("JsTest execution failure", e);
        } finally {
            if (executor != null) {
                executor.close();
            }
            jsTestServer.close();
        }
    }
}
