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
package com.photon.maven.plugins.android.standalonemojos;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.photon.maven.plugins.android.AbstractAndroidMojo;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.plugin.commons.PluginUtils;

/**
 * Deploys the built apk file, or another specified apk, to a connected device.<br/>
 * Automatically performed when running <code>mvn integration-test</code> (or <code>mvn install</code>) on a project
 * with instrumentation tests.
 *
 * @goal deploy
 * @requiresProject false
 * @phase pre-integration-test
 * @requiresDependencyResolution runtime
 */
public class DeployMojo extends AbstractAndroidMojo
{

    /**
     * Optionally used to specify a different apk file to deploy to a connected emulator or usb device, instead of the
     * built apk from this project.
     *
     * @parameter expression="${android.file}"
     */
    private File file;

    /**
     * Deploy the app to the attached devices and emulators.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    /**
	 * @parameter expression="${buildNumber}" required="true"
	 */
	protected String buildNumber;
    

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (StringUtils.isEmpty(buildNumber)) {
            deployBuiltApk();
        } else {
        	
    		PluginUtils pu = new PluginUtils();
    		BuildInfo buildInfo = pu.getBuildInfo(Integer.parseInt(buildNumber));
			getLog().info("Build Name " + buildInfo);
			
        	file = new File(baseDir,"/do_not_checkin/build/"+ buildInfo.getBuildName());
            deployApk(file);
        }
    }


}
