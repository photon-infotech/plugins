package net.awired.jstest.mojo;

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
 * @goal pack-before-test
 * @phase integration-test
 * @execute lifecycle="jstest-lifecycle" phase="integration-test"
 */
public class PackBeforeTestMojo extends AbstractJsTestMojo {

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
            ResourceResolver resourceResolver = new ResourceResolver(getLog(), buildCurrentSrcDir(false),
                    buildTestResourceDirectory(), buildOverlaysResourceDirectories(),
                    new ArrayList<ResourceDirectory>(), isAddOverlaysToSourceMap());
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
                executor.execute("http://localhost:" + getDevPort() + "/?emulator=true");
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
