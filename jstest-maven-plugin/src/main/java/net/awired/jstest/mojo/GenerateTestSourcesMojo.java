package net.awired.jstest.mojo;

import net.awired.jstest.mojo.inherite.AbstractJsTestMojo;
import net.awired.jstest.resource.overlay.OverlayExtractor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.jetty.util.log.Log;

/**
 * @component
 * @goal generateTestSources
 * @phase generate-test-sources
 * @requiresDependencyResolution test
 */
public class GenerateTestSourcesMojo extends AbstractJsTestMojo {

    @Override
    protected void run() throws MojoExecutionException, MojoFailureException {
        if (isSkipTestsCompile()) {
            getLog().debug("Skipping generating test sources");
            return;
        }
        Log.info("Extracting overlays for jsTest");
        OverlayExtractor extractor = new OverlayExtractor(getLog(), archiverManager);
        extractor.extract(getOverlayDirectory(), getMavenProject());
    }

}
