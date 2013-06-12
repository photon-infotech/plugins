package com.photon.phresco.plugins;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

/**
 * Goal which deploys the PHP project
 * 
 * @goal eclipse
 * 
 */
public class PhrescoEclipse extends PhrescoAbstractMojo {

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
	 * @parameter expression="${phresco.project.code}" required="true"
	 */
	protected String projectCode;

	public void execute() throws MojoExecutionException, MojoFailureException {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.MVN_COMMAND);
		sb.append(Constants.STR_BLANK_SPACE);
		sb.append(Constants.MVN_GOAL_ECLIPSE);
		if(!Constants.POM_NAME.equals(project.getFile().getName())) {
			sb.append(Constants.STR_BLANK_SPACE);
			sb.append(Constants.HYPHEN_F);
			sb.append(Constants.STR_BLANK_SPACE);
			sb.append(project.getFile().getName());
		}
		try {
			Commandline commandLine = new Commandline(sb.toString());
			commandLine.setWorkingDirectory(baseDir);
			String processName = ManagementFactory.getRuntimeMXBean().getName();
    		String[] split = processName.split("@");
    		String processId = split[0].toString();
    		Utility.writeProcessid(baseDir.getPath(), Constants.KEY_ECLIPSE, processId);
			CommandLineUtils.executeCommandLine(commandLine, new StreamConsumer() {
				public void consumeLine(String line) {
//					System.out.println(line);
				}
			}, new StreamConsumer() {
				public void consumeLine(String line) {
//					System.out.println(line);
				}
			});
		} catch (CommandLineException e) {
			e.printStackTrace();
		} 
	}
}
