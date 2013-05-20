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
package com.photon.maven.plugins.android.manifmerger;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * This class is a front for the ManifestMerger contained in the
 * {@code manifmerger.jar}. It is dynamically loaded and reflection is used to
 * delegate the methods
 * 
 */
public class ManifestMerger
{
    /**
     * The Manifest Merger instance
     */
    private static MergeStrategy merger;

    /**
     * Before being able to use the ManifestMerger, an initialization is
     * required.
     * 
     * @param log the Mojo Logger
     * @param sdkPath the path to the Android SDK
     * @param sdkMajorVersion the major mergerLib the File pointing on {@code manifmerger.jar}
     * @throws MojoExecutionException if the ManifestMerger class cannot be
     *         loaded
     */
    public void initialize( Log log, File sdkPath, int sdkMajorVersion ) throws MojoExecutionException
    {
        if ( merger != null )
        {
            // Already initialized
            return;
        }

        merger = MergerInitializerFactory.getInitializer( log, sdkMajorVersion, sdkPath );

    }

    /**
     * Creates a new ManifestMerger. The class must be initialized before
     * calling this constructor.
     */
    public ManifestMerger( Log log, File sdkPath, int sdkMajorVersion ) throws MojoExecutionException
    {
        initialize( log, sdkPath, sdkMajorVersion );
    }

    /**
     * Merge the AndroidManifests
     * 
     * @param mergedFile The destination File for the merged content
     * @param apkManifest The original AndroidManifest to merge into
     * @param libraryManifests The array of APKLIB manifests to merge
     * @return
     * @throws MojoExecutionException if there is a problem merging
     */
    public boolean process( File mergedFile, File apkManifest, File[] libraryManifests ) throws MojoExecutionException
    {
        return merger.process( mergedFile, apkManifest, libraryManifests );
    }
}
