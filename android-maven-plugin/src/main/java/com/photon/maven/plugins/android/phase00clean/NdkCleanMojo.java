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
package com.photon.maven.plugins.android.phase00clean;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @goal clean
 * @requiresProject true
 * @requiresOnline false
 * @phase clean
 */
public class NdkCleanMojo extends AbstractMojo
{

    /**
     * @parameter expression="${android.nativeBuildLibsOutputDirectory}" default-value="${project.basedir}/libs"
     */
    File ndkBuildLibsOutputDirectory;

    /**
     * @parameter expression="${android.nativeBuildObjOutputDirectory}" default-value="${project.basedir}/obj"
     */
    File ndkBuildObjOutputDirectory;

    /**
     * Forces the clean process to be skipped.
     *
     * @parameter expression="${android.nativeBuildSkipClean}" default-value="false"
     */
    boolean skipClean = false;

    /**
     * Specifies whether the deletion of the libs/ folder structure should be skipped.  This is by default set to
     * skip (true) to avoid unwanted deletions of libraries already present in this structure.
     *
     * @parameter expression="${android.nativeBuildSkipCleanLibsOutputDirectory}" default-value="true"
     */
    boolean skipBuildLibsOutputDirectory = true;

    /**
     * Specifies whether the obj/ build folder structure should be deleted.
     *
     * @parameter expression="${android.nativeBuildSkipCleanLibsOutputDirectory}" default-value="false"
     */
    boolean skipBuildObjsOutputDirectory = false;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( ndkBuildLibsOutputDirectory.exists() )
        {
            if ( ! skipBuildLibsOutputDirectory )
            {
                getLog().debug( "Cleaning out native library code directory : " + ndkBuildLibsOutputDirectory
                        .getAbsolutePath() );
                try
                {
                    FileUtils.deleteDirectory( ndkBuildLibsOutputDirectory );
                }
                catch ( IOException e )
                {
                    getLog().error( "Error deleting directory: " + e.getMessage(), e );
                }
            }
        }

        if ( ndkBuildObjOutputDirectory.exists() )
        {
            if ( ! skipBuildObjsOutputDirectory )
            {
                getLog().debug(
                        "Cleaning out native object code directory: " + ndkBuildObjOutputDirectory.getAbsolutePath() );
                try
                {
                    FileUtils.deleteDirectory( ndkBuildObjOutputDirectory );
                }
                catch ( IOException e )
                {
                    getLog().error( "Error deleting directory: " + e.getMessage(), e );
                }
            }
        }

    }

}
