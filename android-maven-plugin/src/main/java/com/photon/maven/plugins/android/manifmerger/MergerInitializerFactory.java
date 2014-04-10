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
package com.photon.maven.plugins.android.manifmerger;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Factory for building MergeStrategies
 */
public class MergerInitializerFactory
{

    /**
     * Constant for SDK Tools R20
     */
    private static final int R20 = 20;
    /**
     * Constant for SDK Tools R21
     */
    private static final int R21 = 21;

    /**
     * Returns the MergeStrategy for the specified version of the SDK Tools.
     * Currently supports Revisions: 20, 21.
     * 
     * @param log The Mojo Log
     * @param sdkMajorVersion The major version of the SDK Tools
     * @param sdkPath The path to the Android SDK
     * @return
     * @throws MojoExecutionException
     */
    public static MergeStrategy getInitializer( Log log, int sdkMajorVersion, File sdkPath )
            throws MojoExecutionException
    {
        switch ( sdkMajorVersion )
        {
        case R20:
            return new MergeStrategyR20( log, sdkPath );
        case R21:
            return new MergeStrategyR21( log, sdkPath );
        default:
          log.info( "ATTENTION! Your Android SDK is outdated and not supported for the AndroidManifest merge feature" );
          log.info( "Supported major versions are " + R20 + " and " + R21 + ". You are using " + sdkMajorVersion );
          log.info( "Execution proceeding using merge procedure from " + R20 );
          return new MergeStrategyR20( log, sdkPath );
        }
    }
}
