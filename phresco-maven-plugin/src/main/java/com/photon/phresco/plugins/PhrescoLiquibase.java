/**
	 * Phresco Maven Plugin
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
package com.photon.phresco.plugins;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.liquibase.LiquibasePlugin; 
import com.photon.phresco.util.Constants;

/**
 * Goal which deploys the Liquibase project
 * 
 * @goal liquibase
 * 
 */
public class PhrescoLiquibase extends PhrescoAbstractMojo {
	
	private static final String DBDOC = "dbdoc";
	private static final String UPDATE = "update";
	private static final String INSTALL = "install";
	private static final String DIFF = "diff";
    private static final String STATUS = "status";
    private static final String ROLLBACK_COUNT = "rollbackCount";
    private static final String ROLLBACK_COUNT_DATE = "rollbackToDate";
    private static final String ROLLBACK_TAG = "rollback";
    private static final String TAG = "tag";
    private static final String LIQUIBASE_INFO_FILE = "phresco-liquibase-info.xml";
	/**
	* @parameter expression="${project.basedir}" required="true"
	* @readonly
	*/
	protected File baseDir;
	/**
	* The Maven project.
	* 
	* @parameter expression="${project}"
	* @required
	* @readonly
	*/
	protected MavenProject project;
	
	/**
	* The command.
	* 
	* @parameter expression="${command}"
	* @required
	* @readonly
	*/
	protected String command;
	
	 /**
     * <p>We can't autowire strongly typed RepositorySystem from Aether because it may be Sonatype (Maven 3.0.x)
     * or Eclipse (Maven 3.1.x/3.2.x) version, so we switch to service locator by autowiring entire {@link PlexusContainer}</p>
     *
     * <p>It's a bit of a hack but we have not choice when we want to be usable both in Maven 3.0.x and 3.1.x/3.2.x</p>
     *
     * @component
     * @required
     * @readonly
     */
     protected PlexusContainer container;
     
     /**
      * The current Maven session.
      *
      * @parameter default-value="${session}"
      * @parameter required
      * @readonly
      */
     private MavenSession mavenSession;
     
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info(baseDir.getPath());
	    try {
	    	String infoFile = baseDir + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + LIQUIBASE_INFO_FILE;	
	    	if(command.equalsIgnoreCase(DBDOC)) {
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, DBDOC), mavenSession, project, container);
	    		plugin.liquibaseDbDoc(getConfiguration(infoFile, DBDOC), getMavenProjectInfo(project));
	    	} else if(command.equalsIgnoreCase(UPDATE)) {
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, UPDATE), mavenSession, project, container);
	    		plugin.liquibaseUpdate(getConfiguration(infoFile, UPDATE), getMavenProjectInfo(project));
	    	} else if(command.equalsIgnoreCase(INSTALL)) {	
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, INSTALL), mavenSession, project, container);
	    		plugin.liquibaseInstall(getConfiguration(infoFile, INSTALL), getMavenProjectInfo(project));
	    	} else if(command.equalsIgnoreCase(DIFF)) {
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, DIFF), mavenSession, project, container);
	    		plugin.liquibaseDiff(getConfiguration(infoFile, DIFF), getMavenProjectInfo(project));	
	    	} else if(command.equalsIgnoreCase(STATUS)) {
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, STATUS), mavenSession, project, container);
	    		plugin.liquibaseStatus(getConfiguration(infoFile, STATUS), getMavenProjectInfo(project));
	    	} else if(command.equalsIgnoreCase(ROLLBACK_COUNT)) {
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, ROLLBACK_COUNT), mavenSession, project, container);
	    		plugin.liquibaseSelectedRollback(getConfiguration(infoFile, ROLLBACK_COUNT), getMavenProjectInfo(project));
	    	} else if(command.equalsIgnoreCase(ROLLBACK_COUNT_DATE)) {
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, ROLLBACK_COUNT_DATE), mavenSession, project, container);
	    		plugin.liquibaseRollbackToDate(getConfiguration(infoFile, ROLLBACK_COUNT_DATE), getMavenProjectInfo(project));
	    	} else if(command.equalsIgnoreCase(ROLLBACK_TAG)) {
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, ROLLBACK_TAG), mavenSession, project, container);
	    		plugin.liquibaseRollbackToTag(getConfiguration(infoFile, ROLLBACK_TAG), getMavenProjectInfo(project));
	    	} else if(command.equalsIgnoreCase(TAG)) {
	    		LiquibasePlugin plugin = (LiquibasePlugin) getPlugin(getDependency(infoFile, TAG), mavenSession, project, container);
	    		plugin.liquibaseTag(getConfiguration(infoFile, TAG), getMavenProjectInfo(project));
	    	} else {
        		getLog().info("No proper liquibase command passed");
        	}
	    } catch (PhrescoException e) {
	    	throw new MojoExecutionException(e.getMessage(), e);
	    }
    }
}