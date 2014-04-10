/**
 * Android Maven Plugin - android-maven-plugin
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
package com.photon.maven.plugins.android;

import com.android.ddmlib.IDevice;
import com.github.rtyley.android.screenshot.paparazzo.OnDemandScreenshotService;
import com.github.rtyley.android.screenshot.paparazzo.processors.AnimatedGifCreator;
import com.github.rtyley.android.screenshot.paparazzo.processors.ImageSaver;
import com.github.rtyley.android.screenshot.paparazzo.processors.ImageScaler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

import static com.github.rtyley.android.screenshot.paparazzo.processors.util.Dimensions.square;
import static com.photon.maven.plugins.android.common.DeviceHelper.getDescriptiveName;
import static org.apache.commons.io.FileUtils.forceMkdir;

/**
 * ScreenshotServiceWrapper wraps the feature to capture a screenshot during an instrumentation test run.
 */
public class ScreenshotServiceWrapper implements DeviceCallback
{

    private final DeviceCallback delegate;
    private final Log log;
    private final File screenshotParentDir;
    private static final int MAX_BOUNDS = 320;

    public ScreenshotServiceWrapper( DeviceCallback delegate, MavenProject project, Log log )
    {
        this.delegate = delegate;
        this.log = log;
        screenshotParentDir = new File( project.getBuild().getDirectory(), "screenshots" );
        create( screenshotParentDir );
    }


    @Override
    public void doWithDevice( final IDevice device ) throws MojoExecutionException, MojoFailureException
    {
        String deviceName = getDescriptiveName( device );

        File deviceGifFile = new File( screenshotParentDir, deviceName + ".gif" );
        File deviceScreenshotDir = new File( screenshotParentDir, deviceName );
        create( deviceScreenshotDir );


        OnDemandScreenshotService screenshotService = new OnDemandScreenshotService( device,
                new ImageSaver( deviceScreenshotDir ),
                new ImageScaler( new AnimatedGifCreator( deviceGifFile ), square( MAX_BOUNDS ) ) );

        screenshotService.start();

        delegate.doWithDevice( device );

        screenshotService.finish();
    }

    private void create( File dir )
    {
        try
        {
            forceMkdir( dir );
        }
        catch ( IOException e )
        {
            log.warn( "Unable to create screenshot directory: " + screenshotParentDir.getAbsolutePath(), e );
        }
    }
}
