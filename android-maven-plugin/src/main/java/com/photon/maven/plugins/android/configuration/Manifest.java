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

import com.photon.maven.plugins.android.standalonemojos.CompatibleScreen;
import com.photon.maven.plugins.android.standalonemojos.SupportsScreens;

import java.util.List;
import java.util.Properties;

/**
 * Configuration for the manifest update. This class is only the definition of the parameters that are shadowed in
 * {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo} and used there.
 *
 */
public class Manifest
{
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestVersionName}.
     */
    protected String versionName;

    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestVersionCode}.
     */
    protected Integer versionCode;

    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo
     * #manifestVersionCodeAutoIncrement}.
     */
    private Boolean versionCodeAutoIncrement;

    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo
     * #manifestVersionCodeUpdateFromVersion}.
     */
    protected Boolean versionCodeUpdateFromVersion;

    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestApplicationIcon}.
     */
    protected String applicationIcon;

    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestApplicationLabel}.
     */
    protected String applicationLabel;    
    
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestApplicationTheme}.
     */
    protected String applicationTheme;    
    
    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestSharedUserId}.
     */
    protected String sharedUserId;

    /**
     * Mirror of {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestDebuggable}.
     */
    protected Boolean debuggable;

    /**
     * Mirror of
     * {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestSupportsScreens}
     * .
     */
    protected SupportsScreens supportsScreens;

    /**
     * Mirror of
     * {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestCompatibleScreens}
     * .
     */
    protected List<CompatibleScreen> compatibleScreens;

    /**
     * Mirror of
     * {@link com.photon.maven.plugins.android.standalonemojos.ManifestUpdateMojo#manifestProviderAuthorities}
     * .
     */
    protected Properties providerAuthorities;

    public String getVersionName()
    {
        return versionName;
    }

    public Integer getVersionCode()
    {
        return versionCode;
    }

    public Boolean getVersionCodeAutoIncrement()
    {
        return versionCodeAutoIncrement;
    }

    public Boolean getVersionCodeUpdateFromVersion()
    {
        return versionCodeUpdateFromVersion;
    }

    public String getApplicationIcon() 
    {
    return applicationIcon; 
    }
    
    public String getApplicationLabel() 
    {
        return applicationLabel;
    }

    public String getApplicationTheme() 
    {
        return applicationTheme;
    }

    public String getSharedUserId()
    {
        return sharedUserId;
    }

    public Boolean getDebuggable()
    {
        return debuggable;
    }

    public SupportsScreens getSupportsScreens()
    {
        return supportsScreens;
    }

    public List<CompatibleScreen> getCompatibleScreens()
    {
        return compatibleScreens;
    }

    public Properties getProviderAuthorities()
    {
        return providerAuthorities;
    }
}
