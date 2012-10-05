package com.photon.phresco.plugins.xcode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.commons.XCodeConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.xcode.utils.SdkVerifier;
import com.photon.phresco.plugins.xcode.utils.XcodeUtil;

public class Package {
	
	private static final String DO_NOT_CHECKIN_BUILD = "/do_not_checkin/build";

	private File xcodeCommandLine;
	private String xcodeProject = "./source/Phresco.xcodeproj";
	private String xcodeTarget;
	private MavenProject project;
	private String basedir;
	private boolean unittest;
	private File buildDirectory;
	private String configuration = "Debug";
	private String sdk = "iphonesimulator5.1";
	private String gccpreprocessor;
	private File baseDir;
	private String environmentName;
	private String plistFile = "phresco-env-config.xml";
	private String buildNumber;
	private int buildNo;
	private File buildDirFile;
	private File buildInfoFile;
	private List<BuildInfo> buildInfoList;
	private int nextBuildNo;
	private Date currentDate;
	private String appFileName;
	private String dSYMFileName;
	private String deliverable;
	private Log log;
	
	/**
	 * Execute the xcode command line utility.
	 * @throws PhrescoException 
	 */
	public void pack(Configuration config, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		project = mavenProjectInfo.getProject();
        Map<String, String> configs = MojoUtil.getAllValues(config);
        environmentName = configs.get("environmentName");
        basedir = baseDir.getPath();
        xcodeCommandLine = new File("/usr/bin/xcodebuild");
        buildDirectory = new File(project.getBuild().getDirectory());
        buildNumber = configs.get("userBuildNumber");
        
		if (!xcodeCommandLine.exists()) {
			throw new PhrescoException("Invalid path, invalid xcodebuild file: "
					+ xcodeCommandLine.getAbsolutePath());
		}
		log.info("basedir " + basedir);
		log.info("baseDir Name" + baseDir.getName());
		/*
		 * // Compute archive name String archiveName =
		 * project.getBuild().getFinalName() + ".cust"; File finalDir = new
		 * File(buildDirectory, archiveName);
		 * 
		 * // Configure archiver MavenArchiver archiver = new MavenArchiver();
		 * archiver.setArchiver(jarArchiver); archiver.setOutputFile(finalDir);
		 */

			try {
				if(!SdkVerifier.isAvailable(sdk)) {
					throw new PhrescoException("Selected version " +sdk +" is not available!");
				}
			} catch (IOException e) {
				throw new PhrescoException("SDK verification failed!");
			} catch (InterruptedException e) {
				throw new PhrescoException("SDK verification interrupted!");
            }
			
			try {
			init();
			configure();
			ProcessBuilder pb = new ProcessBuilder(xcodeCommandLine.getAbsolutePath());
			// Include errors in output
			pb.redirectErrorStream(true);

			List<String> commands = pb.command();
			if (xcodeProject != null) {
				commands.add("-project");
				commands.add(xcodeProject);
			}
			if (StringUtils.isNotBlank(configuration)) {
				commands.add("-configuration");
				commands.add(configuration);
			}

			if (StringUtils.isNotBlank(sdk)) {
				commands.add("-sdk");
				commands.add(sdk);
			}

			commands.add("OBJROOT=" + buildDirectory);
			commands.add("SYMROOT=" + buildDirectory);
			commands.add("DSTROOT=" + buildDirectory);

			if (StringUtils.isNotBlank(xcodeTarget)) {
				commands.add("-target");
				commands.add(xcodeTarget);
			}
			
			if(StringUtils.isNotBlank(gccpreprocessor)) {
				commands.add("GCC_PREPROCESSOR_DEFINITIONS="+gccpreprocessor);
			}

			if (unittest) {
				commands.add("clean");
				commands.add("build");
			}

			log.info("List of commands" + pb.command());
			// pb.command().add("install");
			pb.directory(new File(basedir));
			Process child = pb.start();

			// Consume subprocess output and write to stdout for debugging
			InputStream is = new BufferedInputStream(child.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				// output.write(buffer, 0, bytesRead);
				System.out.write(singleByte);
			}

			child.waitFor();
			int exitValue = child.exitValue();
			log.info("Exit Value: " + exitValue);
			if (exitValue != 0) {
				throw new PhrescoException("Compilation error occured. Resolve the error(s) and try again!");
			}
			
			if(!unittest) {
				//In case of unit testcases run, the APP files will not be generated.
				createdSYM();
				createApp(); 
			}
			/*
			 * child.waitFor();
			 * 
			 * InputStream in = child.getInputStream(); InputStream err =
			 * child.getErrorStream(); log.error(sb.toString());
			 */
		} catch (IOException e) {
			log.error("An IOException occured.");
			throw new PhrescoException("An IOException occured" +  e);
		} catch (InterruptedException e) {
			log.error("The clean process was been interrupted.");
			throw new PhrescoException("The clean process was been interrupted" +  e);
		} catch (MojoFailureException e) {
			throw new PhrescoException(e);
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		File directory = new File(this.basedir + "/pom.xml");
		this.project.getArtifact().setFile(directory);
	}

	private void init() throws MojoExecutionException, MojoFailureException {

		try {
			// To Delete the buildDirectory if already exists
			if (buildDirectory.exists()) {
				FileUtils.deleteDirectory(buildDirectory);
				buildDirectory.mkdirs();
			}

			buildInfoList = new ArrayList<BuildInfo>(); // initialization
			// srcDir = new File(baseDir.getPath() + File.separator +
			// sourceDirectory);
			buildDirFile = new File(baseDir, DO_NOT_CHECKIN_BUILD);
			if (!buildDirFile.exists()) {
				buildDirFile.mkdirs();
				log.info("Build directory created..." + buildDirFile.getPath());
			}
			buildInfoFile = new File(buildDirFile.getPath() + "/build.info");
			System.out.println("file created " + buildInfoFile);
			nextBuildNo = generateNextBuildNo();
			currentDate = Calendar.getInstance().getTime();
		}
		catch (IOException e) {
			throw new MojoFailureException("APP could not initialize " + e.getLocalizedMessage());
		 }
	}

	private int generateNextBuildNo() throws IOException {
		int nextBuildNo = 1;
		if (!buildInfoFile.exists()) {	
   			return nextBuildNo;
			}
		
		BufferedReader read = new BufferedReader(new FileReader(buildInfoFile));
		String content = read.readLine();
		Gson gson = new Gson();
		java.lang.reflect.Type listType = new TypeToken<List<BuildInfo>>() {
		}.getType();
		buildInfoList = (List<BuildInfo>) gson.fromJson(content, listType);
		if (buildInfoList == null || buildInfoList.size() == 0) {
			return nextBuildNo;
		}
		int buildArray[] = new int[buildInfoList.size()];
		int count = 0;
		for (BuildInfo buildInfo : buildInfoList) {
			buildArray[count] = buildInfo.getBuildNo();
			count++;
		}

		Arrays.sort(buildArray); // sort to the array to find the max build no

		nextBuildNo = buildArray[buildArray.length - 1] + 1; // increment 1 to
																// the max in
																// the build
																// list
		return nextBuildNo;

		
	}
	

	private void createApp() throws MojoExecutionException {
		File outputFile = getAppName();
		if (outputFile == null) {
			log.error("xcodebuild failed. resultant APP not generated!");
			throw new MojoExecutionException("xcodebuild has been failed");
		}
		if (outputFile.exists()) {

			try {
				System.out.println("Completed " + outputFile.getAbsolutePath());
				log.info("Folder name ....." + baseDir.getName());
				log.info("APP created.. Copying to Build directory....." + project.getBuild().getFinalName());
				String buildName = project.getBuild().getFinalName() + '_' + getTimeStampForBuildName(currentDate);
				File baseFolder = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName);
				if (!baseFolder.exists()) {
					baseFolder.mkdirs();
					log.info("build output direcory created at " + baseFolder.getAbsolutePath());
				}
				File destFile = new File(baseFolder, outputFile.getName());
				log.info("Destination file " + destFile.getAbsolutePath());
				XcodeUtil.copyFolder(outputFile, destFile);
				log.info("copied to..." + destFile.getName());
				appFileName = destFile.getAbsolutePath();

				log.info("Creating deliverables.....");
				ZipArchiver zipArchiver = new ZipArchiver();
				zipArchiver.addDirectory(baseFolder);
				File deliverableZip = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName + ".zip");
				zipArchiver.setDestFile(deliverableZip);
				zipArchiver.createArchive();

				deliverable = deliverableZip.getAbsolutePath();
				log.info("Deliverables available at " + deliverableZip.getName());
				writeBuildInfo(true);
			} catch (IOException e) {
				throw new MojoExecutionException("Error in writing output..." + e.getLocalizedMessage());
			}

		} else {
			log.info("output directory not found");
		}
	}

	private void createdSYM() throws MojoExecutionException {
		File outputFile = getdSYMName();
		if (outputFile == null) {
			log.error("xcodebuild failed. resultant dSYM not generated!");
			throw new MojoExecutionException("xcodebuild has been failed");
		}
		if (outputFile.exists()) {

			try {
				System.out.println("Completed " + outputFile.getAbsolutePath());
				log.info("dSYM created.. Copying to Build directory.....");
				String buildName = project.getBuild().getFinalName() + '_' + getTimeStampForBuildName(currentDate);
				File baseFolder = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName);
				if (!baseFolder.exists()) {
					baseFolder.mkdirs();
					log.info("build output direcory created at " + baseFolder.getAbsolutePath());
				}
				File destFile = new File(baseFolder, outputFile.getName());
				log.info("Destination file " + destFile.getAbsolutePath());
				XcodeUtil.copyFolder(outputFile, destFile);
				log.info("copied to..." + destFile.getName());
				dSYMFileName = destFile.getAbsolutePath();

				log.info("Creating deliverables.....");
				ZipArchiver zipArchiver = new ZipArchiver();
				zipArchiver.addDirectory(baseFolder);
				File deliverableZip = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName + ".zip");
				zipArchiver.setDestFile(deliverableZip);
				zipArchiver.createArchive();

				deliverable = deliverableZip.getAbsolutePath();
				log.info("Deliverables available at " + deliverableZip.getName());

			} catch (IOException e) {
				throw new MojoExecutionException("Error in writing output..." + e.getLocalizedMessage());
			}

		} else {
			log.info("output directory not found");
		}
	}

	private File getAppName() {
		String path = configuration + "-";
		if (sdk.startsWith("iphoneos")) {
			path = path + "iphoneos";
		} else {
			path = path + "iphonesimulator";
		}

		File baseFolder = new File(buildDirectory, path);
		File[] files = baseFolder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith("app")) {
				return file;
			}
		}
		return null;
	}

	private File getdSYMName() {
		String path = configuration + "-";
		if (sdk.startsWith("iphoneos")) {
			path = path + "iphoneos";
		} else {
			path = path + "iphonesimulator";
		}

		File baseFolder = new File(buildDirectory, path);
		File[] files = baseFolder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith("dSYM")) {
				return file;
			}
		}
		return null;
	}

	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		try {
			if (buildNumber != null) {
				buildNo = Integer.parseInt(buildNumber);
			}
			PluginUtils pu = new PluginUtils();
			BuildInfo buildInfo = new BuildInfo();
			List<String> envList = pu.csvToList(environmentName);
			if (buildNo > 0) {
				buildInfo.setBuildNo(buildNo);
			} else {
				buildInfo.setBuildNo(nextBuildNo);
			}
			buildInfo.setTimeStamp(getTimeStampForDisplay(currentDate));
			if (isBuildSuccess) {
				buildInfo.setBuildStatus("SUCCESS");
			} else {
				buildInfo.setBuildStatus("FAILURE");
			}
			buildInfo.setBuildName(appFileName);
			buildInfo.setDeliverables(deliverable);
			buildInfo.setEnvironments(envList);

			Map<String, Boolean> sdkOptions = new HashMap<String, Boolean>(2);
			boolean isDeviceBuild = Boolean.FALSE;
			if (sdk.startsWith("iphoneos")) {
				isDeviceBuild = Boolean.TRUE;
			}
			sdkOptions.put(XCodeConstants.CAN_CREATE_IPA, isDeviceBuild);
			sdkOptions.put(XCodeConstants.DEVICE_DEPLOY, isDeviceBuild);
	        buildInfo.setOptions(sdkOptions);
	        
//			Gson gson2 = new Gson();	       
//	        String json = gson2.toJson(sdkOptions);
//	        System.out.println("json = " + json);
			
			buildInfoList.add(buildInfo);
			Gson gson2 = new Gson();
			FileWriter writer = new FileWriter(buildInfoFile);
			//gson.toJson(buildInfoList, writer);
			String json = gson2.toJson(buildInfoList);
			System.out.println("json = " + json);
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private String getTimeStampForDisplay(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
		String timeStamp = formatter.format(currentDate.getTime());
		return timeStamp;
	}

	private String getTimeStampForBuildName(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
		String timeStamp = formatter.format(currentDate.getTime());
		return timeStamp;
	}

	private void configure() throws MojoExecutionException {
		if (StringUtils.isEmpty(environmentName)) {
			return;
		}
	try {
			log.info("Configuring the project....");
			log.info("environment name :" + environmentName);
			log.info("base dir name :" + baseDir.getName());
			File srcConfigFile = new File(baseDir, project.getBuild().getSourceDirectory() + File.separator + plistFile);
			log.info("Config file :" + srcConfigFile.getAbsolutePath() );
			String basedir = baseDir.getName();
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, basedir, srcConfigFile);
			pu.setDefaultEnvironment(environmentName, srcConfigFile);
			// if(encrypt) {
			// pu.encode(srcConfigFile);
			// }
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

}
