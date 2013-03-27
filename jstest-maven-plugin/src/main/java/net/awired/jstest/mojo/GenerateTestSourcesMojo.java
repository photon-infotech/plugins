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
