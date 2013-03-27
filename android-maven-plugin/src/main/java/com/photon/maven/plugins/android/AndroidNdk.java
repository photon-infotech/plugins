/**
 * Android Maven Plugin - android-maven-plugin
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
package com.photon.maven.plugins.android;

import java.io.File;

import com.photon.maven.plugins.android.phase05compile.NdkBuildMojo;

/**
 * Represents an Android NDK.
 *
 */
public class AndroidNdk {
    
    public static final String PROPER_NDK_HOME_DIRECTORY_MESSAGE = "Please provide a proper Android NDK directory path as configuration parameter <ndk><path>...</path></ndk> in the plugin <configuration/>. As an alternative, you may add the parameter to commandline: -Dandroid.ndk.path=... or set environment variable " + NdkBuildMojo.ENV_ANDROID_NDK_HOME + ".";

    private final File ndkPath;

    public AndroidNdk( File ndkPath ) {
        assertPathIsDirectory( ndkPath );
        this.ndkPath = ndkPath;
    }

    private void assertPathIsDirectory( final File path ) {
        if ( path == null ) {
            throw new InvalidNdkException(PROPER_NDK_HOME_DIRECTORY_MESSAGE);
        }
        if ( !path.isDirectory() ) {
            throw new InvalidNdkException( "Path \"" + path + "\" is not a directory. " + PROPER_NDK_HOME_DIRECTORY_MESSAGE);
        }
    }

    /**
     * Returns the complete path for the ndk-build tool, based on this NDK.
     *
     * @return the complete path as a <code>String</code>, including the tool's filename.
     */
    public String getNdkBuildPath() {
        return new File( ndkPath, "/ndk-build" ).getAbsolutePath();
    }
}
