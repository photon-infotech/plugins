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
 * Configuration for the lint command. This class is only the definition of the parameters that are shadowed in
 * {@link com.photon.maven.plugins.android.standalonemojos.LintMojo} and used there.
 * 
 * @see com.photon.maven.plugins.android.standalonemojos.LintMojo
 */
public class Lint
{
    private Boolean failOnError;
    private Boolean skip;

    // ---------------
    // Enabled Checks
    // ---------------
    private Boolean ignoreWarnings;
    private Boolean warnAll;
    private Boolean warningsAsErrors;
    private String config;

    // ---------------
    // Output Options
    // ---------------
    private Boolean fullPath;
    private Boolean showAll;
    private Boolean disableSourceLines;
    private String url;

    private Boolean enableHtml;
    private String htmlOutputPath;
    private Boolean enableSimpleHtml;
    private String simpleHtmlOutputPath;
    private Boolean enableXml;
    private String xmlOutputPath;

    // ---------------
    // Project Options
    // ---------------
    private Boolean enableSources;
    private String sources;
    private Boolean enableClasspath;
    private String classpath;
    private Boolean enableLibraries;
    private String libraries;

    // ---------------
    // Getters
    // ---------------

    public final Boolean isFailOnError()
    {
        return failOnError;
    }

    public final Boolean isSkip()
    {
        return skip;
    }

    public final Boolean isIgnoreWarnings()
    {
        return ignoreWarnings;
    }

    public final Boolean isWarnAll()
    {
        return warnAll;
    }

    public final Boolean isWarningsAsErrors()
    {
        return warningsAsErrors;
    }

    public final String getConfig()
    {
        return config;
    }

    public final Boolean isFullPath()
    {
        return fullPath;
    }

    public final Boolean getShowAll()
    {
        return showAll;
    }

    public final Boolean isDisableSourceLines()
    {
        return disableSourceLines;
    }

    public final String getUrl()
    {
        return url;
    }

    public final Boolean isEnableHtml()
    {
        return enableHtml;
    }

    public final String getHtmlOutputPath()
    {
        return htmlOutputPath;
    }

    public final Boolean isEnableSimpleHtml()
    {
        return enableSimpleHtml;
    }

    public final String getSimpleHtmlOutputPath()
    {
        return simpleHtmlOutputPath;
    }

    public final Boolean isEnableXml()
    {
        return enableXml;
    }

    public final String getXmlOutputPath()
    {
        return xmlOutputPath;
    }

    public Boolean getEnableSources()
    {
        return enableSources;
    }

    public final String getSources()
    {
        return sources;
    }

    public Boolean getEnableClasspath()
    {
        return enableClasspath;
    }

    public final String getClasspath()
    {
        return classpath;
    }

    public Boolean getEnableLibraries()
    {
        return enableLibraries;
    }

    public final String getLibraries()
    {
        return libraries;
    }
}
