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

/**
 * MergeStrategy interface
 * 
 */
public interface MergeStrategy
{
    /**
     * Merges the APKLIB manifests with the APK manifest
     * 
     * @param mergedFile The final merged AndroidManifest file.
     * @param apkManifest The original AndroidManifest file of the APK.
     * @param libraryManifests Array of AndroidManifests for the APKLIBs
     * @return
     * @throws MojoExecutionException
     */
    boolean process( File mergedFile, File apkManifest, File[] libraryManifests ) throws MojoExecutionException;
}
