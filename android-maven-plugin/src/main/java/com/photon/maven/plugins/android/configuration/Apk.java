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
package com.photon.maven.plugins.android.configuration;

/**
 * Embedded configuration of {@link com.photon.maven.plugins.android.phase09package.ApkMojo}.
 *
 */
@SuppressWarnings( "unused" )
public class Apk
{

    /**
     * Mirror of {@link com.photon.maven.plugins.android.phase09package.ApkMojo#apkMetaIncludes}.
     */
    private String[] metaIncludes;

    /**
     * Mirror of {@link com.photon.maven.plugins.android.phase09package.ApkMojo#apkDebug}.
     */
    private Boolean debug;

    /**
     * Mirror of {@link com.photon.maven.plugins.android.phase09package.ApkMojo#apkNativeToolchain}.
     */
    private String nativeToolchain;
}
