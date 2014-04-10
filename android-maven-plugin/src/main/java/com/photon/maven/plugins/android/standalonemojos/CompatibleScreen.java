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
 * CompatibleScreen abstracts the AndroidManifest element.
 */
public class CompatibleScreen
{

    private String screenSize;
    private String screenDensity;

    public String getScreenSize()
    {
        return screenSize;
    }

    public void setScreenSize( String screenSize )
    {
        this.screenSize = screenSize;
    }

    public String getScreenDensity()
    {
        return screenDensity;
    }

    public void setScreenDensity( String screenDensity )
    {
        this.screenDensity = screenDensity;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof CompatibleScreen )
        {
            CompatibleScreen that = ( CompatibleScreen ) obj;
            return this.screenDensity.equals( that.screenDensity ) && this.screenSize.equals( that.screenSize );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return ( screenDensity + screenSize ).hashCode();
    }

    @Override
    public String toString()
    {
        return screenSize + ":" + screenDensity;
    }
}
