package com.photon.phresco.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.impl.PHPPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;

/**
 * Phresco Maven Plugin for executing package command of the plugins
 * @goal package
 */
public class PhrescoPackage extends PhrescoAbstractMojo {
    
    /**
     * @parameter expression="${project.basedir}" required="true"
     * @readonly
     */
    protected File baseDir;
    
    /**
     * File pointing to the Meta Info
     * @parameter
     */
    private File metaInfoFile = new File(PHRESCO_PLUGIN_META_INFO_XML);

    /**
     * File pointing to selected Info
     * @parameter
     */
    private File selectedInfoFile = new File(PHRESCO_PLUGIN_SELECTED_INFO_XML);
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Form Phresco Plugin");
        getLog().info("Hello Phresco");
        getLog().info(baseDir.getPath());
        //Read the selected info file
        //Convert it into Java Bean Objects using JAXB
        //Find the implementation class based on the technology
        //execute package method
        try {
            PhrescoPlugin plugin = getPlugin(com.photon.phresco.plugins.impl.PHPPlugin.class.getName());
            plugin.pack(getConfiguration());
        } catch (PhrescoException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
    
    private Configuration getConfiguration() throws PhrescoException {
        MojoProcessor processor = new MojoProcessor(new File(baseDir, PHRESCO_PLUGIN_SELECTED_INFO_XML));
        return processor.getConfiguration();
    }
}
