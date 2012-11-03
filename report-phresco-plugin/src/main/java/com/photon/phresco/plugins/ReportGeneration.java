package com.photon.phresco.plugins;

import java.io.*;

import org.apache.maven.plugin.*;
import org.apache.maven.project.*;

/**
 * Phresco Maven Plugin for executing ci prebuild step command of the plugins
 * @goal phrescoReport
 */
public class ReportGeneration {

	/**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
     * @parameter expression="${project.basedir}" required="true"
     * @readonly
     */
    protected File baseDir;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("Form Phresco Phresco Report generation Plugin");
    }

}
