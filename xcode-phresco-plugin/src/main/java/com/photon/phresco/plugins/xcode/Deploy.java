package com.photon.phresco.plugins.xcode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.xcode.utils.SdkVerifier;
import com.photon.phresco.plugins.xcode.utils.XcodeUtil;

public class Deploy implements PluginConstants {
	
	/**
	 * command line tool for iphone simulator
	 * @parameter expression="${simulator.home}" default-value="/Developer/Platforms/iPhoneSimulator.platform/Developer/Applications/iPhone Simulator.app/Contents/MacOS/iPhone Simulator"
	 */
	private String simHome = "/Developer/Platforms/iPhoneSimulator.platform/Developer/Applications/iPhone Simulator.app/Contents/MacOS/iPhone Simulator";
	
	/**
	 * 
	 * @parameter experssion="${simulator.deploy.dir}" default-value=""
	 */
	private String appDeployHome;

	/**
	 * @parameter expression ="${action}" default-value="-SimulateApplication"
	 * 
	 * The possible actions are SimulateDevice, SimulateRestart, SessionOnLaunch, currentSDKRoot
	 */
	private String action = "-SimulateApplication";
	
	/**
	 * @parameter expression="${simulator.version}" default-value="5.0" 
	 */
	private String simVersion = "5.1";
	
	/**
	 * @parameter expression="${family}" default-value="iphone" 
	 */
	private String family = "iphone";

	/**
	 * @parameter expression="${device.deploy}"
	 */
	private boolean deviceDeploy;
	
	/**
	 * @parameter expression="${application.name}"
	 */
	private String appName;
	
	/**
	 * The maven project.
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
	
	/**
	 * @parameter expression="${buildNumber}" required="true"
	 */
	protected String buildNumber;
	
	/**
	 * @parameter expression ="${triggerSimulator}" default-value="true"
	 */
	private boolean triggerSimulator = true;
	
	private String appPath;
	
	private File simHomeAppLocation;
	
	private File buildInfoFile;
	
	private String home = "";
	
	private Log log;
	
	public void deploy(Configuration configuration, MavenProjectInfo mavenProjectInfo, final Log log) throws MojoExecutionException, MojoFailureException {
		
		log.info("iphone trigger value " + triggerSimulator);
		this.log = log;
		project = mavenProjectInfo.getProject();
		baseDir = mavenProjectInfo.getBaseDir();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		buildNumber = configs.get(USER_BUILD_NUMBER);
		try {
			if(!deviceDeploy && !SdkVerifier.isAvailable(simVersion)) {
				throw new MojoExecutionException("Selected version " +simVersion +" is not available!");
			}
		} catch (IOException e2) {
			throw new MojoExecutionException("SDK verification failed!");
		} catch (InterruptedException e2) {
			throw new MojoExecutionException("SDK verification interrupted!");
		}
		//get the correct simhome if xCode 4.3 is installed simhome is in /Application/Xcode.app/Contents
		//Fix for artf462004
		File simHomeFile = new File(simHome);
		log.info("simHomeFile " + simHomeFile);
		if(!simHomeFile.exists()) {
			log.info("simHomeFile doesn't exists " + simHomeFile);
			simHome = "/Applications/Xcode.app/Contents" + simHome; 
		}
		
		log.info("Simulator home" + simHome);
		log.info("copyAppToSimHome method ");
		//copy the files into simulation directory
		home = System.getProperty("user.home");
		// Have to get app path from build id
		if (StringUtils.isEmpty(buildNumber)) {
			throw new MojoExecutionException("Selected build is not available!");
		}
		log.info("Build id is " + buildNumber);
		log.info("Project Code " + baseDir.getName());
		
		BuildInfo buildInfo = getBuildInfo(Integer.parseInt(buildNumber));
		log.info("Build Name " + buildInfo);
		
		appPath = buildInfo.getBuildName();
		log.info("Application.path = " + appPath);
		log.info("triggerSimulator " + triggerSimulator);
		log.info("deviceDeploy " + !deviceDeploy);
		
		//when the device deploy is false, it will be deployed to the simulator.
		if(!deviceDeploy && triggerSimulator) {
			// phresco executes it. 
			// if its simulator deploy, we can deploy in many family(ipad, iphone sim) using WaxSim
			log.info("deployAppWithWaxSim started.... ");
			deployAppWithWaxSim();
		} else if (!deviceDeploy && !triggerSimulator) {
			// jenkins executes it.
			// This process copies the app file and places it in simulatos applications dir.
			log.info("copyAppToSimHome started.... ");
			copyAppToSimHome();
		}
		
		Runnable runnable = new Runnable() {
			public void run() {

				ProcessBuilder pb;
				if(deviceDeploy){
					pb = new ProcessBuilder("transporter_chief.rb");
					pb.command().add(appPath);
				} else {
					log.info("executing into device ");
					pb = new ProcessBuilder(simHome);
					// Include errors in output
					pb.redirectErrorStream(true);

					pb.command().add(action);
					pb.command().add(simHomeAppLocation+File.separator+appName.substring(0, appName.indexOf('.')));
				}
				//					pb.command().add(appName);
				log.info("List of commands"+pb.command());
				Process child;
				try {
//					if(ProcessHelper.isProcessRunning()) {
//						ProcessHelper.killSimulatorProcess();
//					}
					child = pb.start();
					// Consume subprocess output and write to stdout for debugging
					InputStream is = new BufferedInputStream(child.getInputStream());
					int singleByte = 0;
					while ((singleByte = is.read()) != -1) {
						System.out.write(singleByte);
					}
				} catch (IOException e) {
					log.error("error occured in launching simulator ");
					log.error(e);
				}
			}
		};

		log.info("triggerSimulator " + triggerSimulator);
		log.info("deviceDeploy " + deviceDeploy);
		if (triggerSimulator || deviceDeploy) {
			log.info("Triggering simulator started ");
			Thread t = new Thread(runnable, "iPhoneSimulator");
			t.start();
			try {
				t.join(5000);
			} catch (InterruptedException e1) {
				log.error("Triggering simulator failed.");
			}
		}

	}

	private void deployAppWithWaxSim() {
		log.info("deployAppWithWaxSim method ");
		try {
			log.info("WAXSIM_HOME... " + System.getenv(WAXSIM_HOME));
			String waxsim_home = System.getenv(WAXSIM_HOME);
			if(StringUtils.isEmpty(waxsim_home)) {
				throw new MojoExecutionException("waxsim_home is not found!");
			}
			
			Runnable deployRunnable = new Runnable() {
				public void run() {
					
					List<String> command = new ArrayList<String>();
					command.add(System.getenv(WAXSIM_HOME));
					command.add("-s");
					log.info("simVersion " + simVersion);
					command.add(simVersion);
					log.info("family " + family);
					command.add("-f");
					command.add(family);
					command.add(appPath);
					log.info("command " + command.toString());

					try {
						ProcessBuilder pb = new ProcessBuilder(command);
						log.info("baseDir... " + baseDir.getCanonicalPath());
						pb.directory(baseDir);
						Process child = pb.start();
						InputStream is = new BufferedInputStream(child.getInputStream());
						int singleByte = 0;
						while ((singleByte = is.read()) != -1) {
							System.out.write(singleByte);
						}
					} catch (IOException e) {
						log.error("error occured in launching simulator ");
						log.error(e);
					}
				}
			};

			log.error("deviceDeploy " + deviceDeploy);
			Thread t = new Thread(deployRunnable, "iPhoneSimulator");
			t.start();
			try {
				t.join(5000);
			} catch (InterruptedException e1) {
				log.error("Triggering simulator failed.");
			}
			
		} catch (Exception e) {
			log.error("error occured in deployAppWithWaxSim ");
			log.error(e);
		}
	}

//	public static void main(String[] args) {
//		try {
//			new AppDeploy().deployAppWithWaxSim();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	private void copyAppToSimHome() throws MojoExecutionException {
		
		log.info("Application.path = " + appPath);
		appName = getAppFileName(appPath);
		log.info("Application name = "+ appName);
		String deployHome = "";
		if(StringUtils.isNotBlank(appDeployHome)){
			deployHome = appDeployHome;
			log.info("if deployHome = " + deployHome);
		} else {
			deployHome = home + "/Library/Application Support/iPhone Simulator/" + simVersion +"/Applications";
			log.info("else deployHome = " + deployHome);
		}
		simHomeAppLocation = new File(deployHome + File.separator + project.getName() + File.separator + appName);
		if(!simHomeAppLocation.exists()){
			log.info("directory created");
			simHomeAppLocation.mkdirs();
		}
		log.info("Simulator home Desired location "+ simHomeAppLocation.getAbsolutePath());
		try {
			String alignedPath = alignedPath(appPath);
			log.info("path to copy source :" + alignedPath);
			XcodeUtil.copyFolder(new File(alignedPath), simHomeAppLocation);
			log.info("copy the application " + appPath +" to "+ simHomeAppLocation);
		} catch (IOException e1) {
			log.error("couldn't copy the application " + appPath +" to "+ simHomeAppLocation);
		}
	}
	
	private String getAppFileName(String appPath2) {
		if(StringUtils.isNotBlank(appPath2)){
			if(appPath2.endsWith("app")) {
				return appPath2.substring(appPath2.lastIndexOf('/')+1);
			} else {
				File folder = new File(appPath2);
				if(folder.exists()) {
					File[] files = folder.listFiles();
					for (int i = 0; i < files.length; i++) {
						File file = files[i];
						if(file.getName().endsWith("app")) {
							return file.getName();
						}
					}
				}
			}
		}
		return appPath2;
		
	}
	
	private String alignedPath(String appPath2) {
		if(StringUtils.isNotBlank(appPath2)){
			if(appPath2.endsWith("app")) {
				return appPath2;
			}	else {
				return appPath2+File.separator+getAppFileName(appPath2);
			}
		}
		return appPath2;
	}
	
	private BuildInfo getBuildInfo(int buildNumber) throws MojoExecutionException {
		ProjectAdministrator administrator;
		try {
			administrator = PhrescoFrameworkFactory.getProjectAdministrator();
		} catch (PhrescoException e) {
			throw new MojoExecutionException("Project administrator object creation error!");
		}
		buildInfoFile = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY + BUILD_INFO_FILE);
		if (!buildInfoFile.exists()) {
			throw new MojoExecutionException("Build info is not available!");
		}
		try {
			List<BuildInfo> buildInfos = administrator.readBuildInfo(buildInfoFile);
			
			 if (CollectionUtils.isEmpty(buildInfos)) {
				 throw new MojoExecutionException("Build info is empty!");
			 }

			 for (BuildInfo buildInfo : buildInfos) {
				 if (buildInfo.getBuildNo() == buildNumber) {
					 return buildInfo;
				 }
			 }

			 throw new MojoExecutionException("Build info is empty!");
		} catch (Exception e) {
			throw new MojoExecutionException(e.getLocalizedMessage());
		}
	}

}
