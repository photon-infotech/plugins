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

import com.android.ddmlib.IDevice;
import com.photon.maven.plugins.android.AbstractAndroidMojo;
import com.photon.maven.plugins.android.DeviceCallback;
import com.photon.maven.plugins.android.common.DeviceHelper;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * DevicesMojo lists all attached devices and emulators found with the android debug bridge. It uses the same
 * naming convention for the emulator as used in other places in the Android Maven Plugin and adds the status
 * of the device in the list.
 * <p/>
 * TODO The goal is very simple and could be enhanced for better display, a verbose option to display and to take the
 * android.device paramter into account.
 *
 * @goal devices
 * @requiresProject false
 */
public class DevicesMojo extends AbstractAndroidMojo
{
    /**
     * Display a list of attached devices.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        doWithDevices( new DeviceCallback()
        {
            public void doWithDevice( final IDevice device ) throws MojoExecutionException
            {
                getLog().info( DeviceHelper.getDescriptiveNameWithStatus( device ) );
            }
        } );
    }
}
