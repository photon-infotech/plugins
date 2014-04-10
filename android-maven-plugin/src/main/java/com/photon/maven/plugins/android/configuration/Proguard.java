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
package com.photon.maven.plugins.android.configuration;

/**
 * Configuration container for proguard without default values.
 *
 * @see com.photon.maven.plugins.android.phase04processclasses.ProguardMojo
 */
public class Proguard
{
    /**
     * Whether ProGuard is enabled or not.
     */
    private Boolean skip;
    /**
     * Path to the ProGuard configuration file (relative to project root).
     */
    private String config;
    private String[] configs;
    private String proguardJarPath;
    private String outputDirectory;
    private String[] jvmArguments;
    private Boolean filterMavenDescriptor;
    private Boolean filterManifest;
    private Boolean includeJdkLibs;

    public Boolean isSkip()
    {
        return skip;
    }

    public String getConfig()
    {
        return config;
    }

    public String[] getConfigs()
    {
        return configs;
    }

    public String getProguardJarPath()
    {
        return proguardJarPath;
    }
    
    public String getOutputDirectory()
    {
        return outputDirectory;
    }
   
    public String[] getJvmArguments()
    {
        return jvmArguments;
    }

    public Boolean isFilterMavenDescriptor()
    {
        return filterMavenDescriptor;
    }

    public Boolean isFilterManifest()
    {
        return filterManifest;
    }
    
    public Boolean isIncludeJdkLibs()
    {
        return includeJdkLibs;
    }
}
