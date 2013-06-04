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
package com.photon.maven.plugins.android.manifmerger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * MergeStrategy for SDK Tools R20
 *
 */
public class MergeStrategyR20 implements MergeStrategy
{
    /**
     * The Mojo logger
     */
    Log log;

    /**
     * The method that does the merging.
     */
    Method processMethod;

    /**
     * The ManifestMerger class
     */
    Object merger;

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public MergeStrategyR20( Log log, File sdkPath ) throws MojoExecutionException
    {

        File mergerLib = new File( sdkPath + "/tools/lib/manifmerger.jar" );
        File sdkLibs = new File( sdkPath + "/tools/lib/sdklib.jar" );

        URLClassLoader mlLoader = null;
        Class manifestMergerClass = null;
        Class mergerLogClass = null;
        try
        {
            mlLoader = new URLClassLoader( new URL[] { mergerLib.toURI().toURL() },
                    ManifestMerger.class.getClassLoader() );
            manifestMergerClass = mlLoader.loadClass( "com.android.manifmerger.ManifestMerger" );
            log.debug( "ManifestMerger loaded " + manifestMergerClass );
            mergerLogClass = mlLoader.loadClass( "com.android.manifmerger.MergerLog" );
            log.debug( "ManifestMerger loaded " + mergerLogClass );
        }
        catch ( MalformedURLException e )
        {
            // This one cannot happen.
            throw new RuntimeException( "Cannot create a correct URL from file " + mergerLib.getAbsolutePath() );
        }
        catch ( ClassNotFoundException e )
        {
            log.error( "Cannot find required class", e );
            throw new MojoExecutionException( "Cannot find the required class", e );
        }

        // Loads the StdSdkLog class
        Class stdSdkLogClass = null;
        try
        {
            URLClassLoader child = new URLClassLoader( new URL[] { sdkLibs.toURI().toURL() }, mlLoader );
            stdSdkLogClass = child.loadClass( "com.android.sdklib.StdSdkLog" );
            log.debug( "StdSdkLog loaded " + stdSdkLogClass );
        }
        catch ( MalformedURLException e )
        {
            // This one cannot happen.
            throw new RuntimeException( "Cannot create a correct URL from file " + sdkLibs.getAbsolutePath() );
        }
        catch ( ClassNotFoundException e )
        {
            log.error( "Cannot find required class", e );
            throw new MojoExecutionException( "Cannot find the required class", e );
        }

        // In order to improve performance and to check that all methods are
        // available we cache used methods.
        try
        {
            processMethod = manifestMergerClass.getMethod( "process", File.class, File.class, File[].class );
        }
        catch ( Exception e )
        {
            log.error( "Cannot find required method", e );
            throw new MojoExecutionException( "Cannot find the required method", e );
        }

        try
        {
            Constructor stdSdkLogConstructor = stdSdkLogClass.getDeclaredConstructors()[0];
            Object sdkLog = stdSdkLogConstructor.newInstance();
            Method wrapSdkLogMethod = mergerLogClass.getMethod( "wrapSdkLog", stdSdkLogClass.getInterfaces()[0] );
            Object iMergerLog = wrapSdkLogMethod.invoke( null, sdkLog.getClass().getInterfaces()[0].cast( sdkLog ) );
            Constructor manifestMergerConstructor = manifestMergerClass.getDeclaredConstructors()[0];
            merger = manifestMergerConstructor.newInstance( iMergerLog );
        }
        catch ( InvocationTargetException e )
        {
            log.error( "Cannot create the ManifestMerger object", e.getCause() );
            throw new MojoExecutionException( "Cannot create the ManifestMerger object", e.getCause() );
        }
        catch ( Exception e )
        {
            log.error( "Cannot create the ManifestMerger object", e );
            throw new MojoExecutionException( "Cannot create the ManifestMerger object", e );
        }
    }

    /**
     * @see {@link MergeStrategy#process(File, File, File[])}
     */
    @Override
    public boolean process( File mergedFile, File apkManifest, File[] libraryManifests ) throws MojoExecutionException
    {
        try
        {
            return (Boolean) processMethod.invoke( merger, mergedFile, apkManifest, libraryManifests );
        }
        catch ( Exception e )
        {
            log.error( "Cannot merge the manifests", e );
            throw new MojoExecutionException( "Cannot merge the manifests", e );
        }
    }

}
