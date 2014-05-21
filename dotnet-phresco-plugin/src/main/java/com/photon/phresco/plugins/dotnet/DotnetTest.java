package com.photon.phresco.plugins.dotnet;
import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.util.PomProcessor;

public class DotnetTest implements PluginConstants {
    private File baseDir;
    private MavenProject project;
    private File workingDirectory;
    private String subModule = "";
    private File pomFile;
    private String dotPhrescoDirName;
    private File dotPhrescoDir;
    private File srcDirectory;
    public void runTest(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException{
        try {
            baseDir = mavenProjectInfo.getBaseDir();
            if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
                subModule = mavenProjectInfo.getModuleName();
            }
            project = mavenProjectInfo.getProject();
            workingDirectory = new File(baseDir.getPath() + File.separator + subModule);
            PluginUtils pluginUtils = new PluginUtils();
            dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
            dotPhrescoDir = baseDir;
            if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
                dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
            }
            dotPhrescoDir = new File(dotPhrescoDir.getPath() + File.separatorChar + subModule);
            pomFile = pluginUtils.getPomFile(dotPhrescoDir, workingDirectory);
            File splitProjectDirectory = pluginUtils.getSplitProjectSrcDir(pomFile, dotPhrescoDir, subModule);
            srcDirectory = workingDirectory;
            if (splitProjectDirectory != null) {
                srcDirectory = splitProjectDirectory;
            }
            PomProcessor processor = new PomProcessor(new File(workingDirectory, pomFile.getName()));
            String dllfilename = processor.getProperty("phresco.unittest.dll");
            String untitestdir = processor.getProperty("phresco.unitTest.execute.dir");
            
            executeTest(dllfilename, untitestdir, srcDirectory,  new File(srcDirectory + untitestdir));
        } catch (Exception e) {
			e.printStackTrace();
            throw new PhrescoException(e); 
        }
    }

    private void executeTest(String dllfilename,  String untitestdir, File srcDirectory, File workingDirectory)throws PhrescoException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("mstest");
            sb.append(STR_SPACE);
            sb.append("/testcontainer:");
            sb.append(dllfilename); 
            sb.append(STR_SPACE);
            sb.append("/detail:testtype");
            System.out.println("COMMAND IS  " + sb.toString());
            boolean status = Utility.executeStreamconsumer(sb.toString(), workingDirectory.getPath(), srcDirectory.getPath(), UNIT);
            if(!status) {
                throw new MojoExecutionException(Constants.MOJO_ERROR_MESSAGE);
            }
        } catch (Exception e) {
            throw new  PhrescoException(e);
        }
    }

}