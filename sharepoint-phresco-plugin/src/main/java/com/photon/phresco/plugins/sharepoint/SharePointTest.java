/**
 * sharepoint-phresco-plugin
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
package com.photon.phresco.plugins.sharepoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.util.Constants;

public class SharePointTest
{
  private static String NUNIT_CMD = "nunit-console.exe";
  private static String STR_SPACE = " ";
  private static String NUNIT_REPORT_LOCATION = "\\source\\test\\target\\nunit-report\\";
  private static String TEST_FILE_LOC = "\\source\\test\\AllTest\\bin\\Release\\";
  private static String TEST_FILE_NAME = "AllTest.dll";
  private File baseDir;
  private MavenProject project;
  private String pomFile;
  
  public void runUnitTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
	  baseDir = mavenProjectInfo.getBaseDir();
	  project = mavenProjectInfo.getProject();
	  pomFile = project.getFile().getName();
	  init();
	  try {
		  fetchNUnit();
		  executeTest();
	  } catch (MojoExecutionException e) {
		  throw new PhrescoException(e);
	  }
  }

  private void init() {
    File temp = new File(new StringBuilder().append(this.baseDir.getPath()).append(NUNIT_REPORT_LOCATION).toString());
    if (!(temp.exists()))
      temp.mkdirs();
  }

  private void fetchNUnit() throws MojoExecutionException {
	    StringBuilder sb;
	    try {
	      sb = new StringBuilder();
	      sb.append(Constants.MVN_COMMAND);
	      sb.append(STR_SPACE);
	      sb.append(Constants.PHASE);
	      if(!Constants.POM_NAME.equals(pomFile)) {
	    	  sb.append(STR_SPACE);
		      sb.append(Constants.HYPHEN_F);
		      sb.append(STR_SPACE);
		      sb.append(pomFile);
	      }
	      
	      Commandline cl = new Commandline(sb.toString());
	      Process p = cl.execute();
	      BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
	      String line = null;
	      while ((line = in.readLine()) != null) {
	    	  System.out.println(line);
	      }
	    }
	    catch (Exception e) {
	      throw new MojoExecutionException(e.getMessage(), e);
	    }
	  }

  private void executeTest() throws MojoExecutionException {
    System.out.println("-----------------------------------------");
    System.out.println("T E S T S");
    System.out.println("-----------------------------------------");
    try
    {
      StringBuilder sb = new StringBuilder();
      sb.append(NUNIT_CMD);
      sb.append(STR_SPACE);
      sb.append("\"");
      sb.append(new StringBuilder().append(this.baseDir.getPath()).append(TEST_FILE_LOC).toString());
      sb.append(TEST_FILE_NAME);
      sb.append("\"");
      System.out.println("Command = " + sb.toString());
      Commandline cl = new Commandline(sb.toString());
      cl.setWorkingDirectory(new StringBuilder().append(this.baseDir.getPath()).append(NUNIT_REPORT_LOCATION).toString());
      Process execute = cl.execute();
      BufferedReader in = new BufferedReader(new InputStreamReader(execute.getInputStream()));
      String line = null;
      while ((line = in.readLine()) != null) {
    	  System.out.println(line);
      }
    }
    catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}