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
package com.photon.maven.plugins.android.standalonemojos;

/**
 *
 */
public class SupportsScreens
{

    private String resizeable;
    private String smallScreens, normalScreens, largeScreens, xlargeScreens;
    private String anyDensity;
    private String requiresSmallestWidthDp;
    private String compatibleWidthLimitDp;
    private String largestWidthLimitDp;

    public String getResizeable()
    {
        return resizeable;
    }

    public void setResizeable( String resizable )
    {
        this.resizeable = resizable;
    }

    public String getSmallScreens()
    {
        return smallScreens;
    }

    public void setSmallScreens( String smallScreens )
    {
        this.smallScreens = smallScreens;
    }

    public String getNormalScreens()
    {
        return normalScreens;
    }

    public void setNormalScreens( String normalScreens )
    {
        this.normalScreens = normalScreens;
    }

    public String getLargeScreens()
    {
        return largeScreens;
    }

    public void setLargeScreens( String largeScreens )
    {
        this.largeScreens = largeScreens;
    }

    public String getXlargeScreens()
    {
        return xlargeScreens;
    }

    public void setXlargeScreens( String xlargeScreens )
    {
        this.xlargeScreens = xlargeScreens;
    }

    public String getAnyDensity()
    {
        return anyDensity;
    }

    public void setAnyDensity( String anyDensity )
    {
        this.anyDensity = anyDensity;
    }

    public String getRequiresSmallestWidthDp()
    {
        return requiresSmallestWidthDp;
    }

    public void setRequiresSmallestWidthDp( String requiresSmallestWidthDp )
    {
        this.requiresSmallestWidthDp = requiresSmallestWidthDp;
    }

    public String getCompatibleWidthLimitDp()
    {
        return compatibleWidthLimitDp;
    }

    public void setCompatibleWidthLimitDp( String compatibleWidthLimitDp )
    {
        this.compatibleWidthLimitDp = compatibleWidthLimitDp;
    }

    public String getLargestWidthLimitDp()
    {
        return largestWidthLimitDp;
    }

    public void setLargestWidthLimitDp( String largestWidthLimitDp )
    {
        this.largestWidthLimitDp = largestWidthLimitDp;
    }

}
