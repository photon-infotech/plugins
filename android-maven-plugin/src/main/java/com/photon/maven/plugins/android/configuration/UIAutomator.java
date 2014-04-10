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
 * Configuration for the ui automator test runs. This class is only the definition of the parameters that are shadowed
 * in {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo} and used there.
 * 
 */
public class UIAutomator
{
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#testSkip}
     */
    private Boolean skip;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#jarFile}
     */
    private String jarFile;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#testClassOrMethods}
     */
    private String[] testClassOrMethods;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#noHup}
     */
    private Boolean noHup = false;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#debug}
     */
    private Boolean debug = false;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#useDump}
     */
    private Boolean useDump = false;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#dumpFilePath}
     */
    private String dumpFilePath;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#createReport}
     */
    private Boolean createReport;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#takeScreenshotOnFailure}
     */
    private Boolean takeScreenshotOnFailure;
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.UIAutomatorMojo#screenshotsPathOnDevice}
     */
    private String screenshotsPathOnDevice;

    public Boolean isSkip()
    {
        return skip;
    }

    public String getJarFile()
    {
        return jarFile;
    }

    public String[] getTestClassOrMethods()
    {
        return testClassOrMethods;
    }

    public Boolean getNoHup()
    {
        return noHup;
    }

    public Boolean getDebug()
    {
        return debug;
    }

    public Boolean getUseDump()
    {
        return useDump;
    }

    public String getDumpFilePath()
    {
        return dumpFilePath;
    }

    public Boolean isCreateReport()
    {
        return createReport;
    }

    public Boolean isTakeScreenshotOnFailure()
    {
        return takeScreenshotOnFailure;
    }

    public String getScreenshotsPathOnDevice()
    {
        return screenshotsPathOnDevice;
    }
}
