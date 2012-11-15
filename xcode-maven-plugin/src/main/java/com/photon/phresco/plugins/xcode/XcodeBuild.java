/*
 * ###
 * Xcodebuild Command-Line Wrapper
 * 
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
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
 * ###
 */
/*******************************************************************************
 * Copyright (c)  2012 Photon infotech.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Photon Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.photon.in/legal/ppl-v10.html
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 *  Contributors:
 *  	  Photon infotech - initial API and implementation
 ******************************************************************************/
package com.photon.phresco.plugins.xcode;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.collections.*;
import org.apache.commons.io.*;
import org.apache.commons.lang.*;
import org.apache.maven.plugin.*;
import org.apache.maven.project.*;
import org.codehaus.plexus.archiver.zip.*;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.photon.phresco.commons.*;
import com.photon.phresco.commons.model.*;
import com.photon.phresco.plugin.commons.*;
import com.photon.phresco.plugins.xcode.utils.*;
import com.photon.phresco.plugins.xcode.utils.XcodeUtil;
import com.photon.phresco.util.*;
import com.photon.phresco.util.IosSdkUtil.MacSdkType;

/**
 * Run the xcodebuild command line program
 * 
 * @goal xcodebuild
 * @phase compile
 */
public class XcodeBuild extends AbstractMojo {

	private static final String OUTPUT = "-output";

	private static final String CREATE = "-create";

	private static final String LIPO = "lipo";

	private static final String BUILD_FAILURE = "FAILURE";

	private static final String BUILD_SUCCESS = "SUCCESS";

	private static final String DSYM = "dSYM";

	private static final String APP = "app";

	private static final String IPHONE_SIMULATOR = "iphonesimulator";

	private static final String IPHONEOS = "iphoneos";

	private static final String HYPHEN = "-";

	private static final String CMD_BUILD = "build";

	private static final String CMD_CLEAN = "clean";

	private static final String LIB = "lib";

	private static final String FAT_FILE = "FatFile";

	private static final String SPACE = " ";

	private static final String DOT_A_EXT = ".a";

	private static final String STATIC_LIB_EXT = "a";

	private static final String SDK = "-sdk";

	private static final String CONFIGURATION = "-configuration";

	private static final String TARGET = "-target";

	private static final String SCHEME = "-scheme";

	private static final String PROJECT = "-project";

	private static final String WORKSPACE = "-workspace";

	private static final String DD_MMM_YYYY_HH_MM_SS = "dd/MMM/yyyy HH:mm:ss";

	private static final String POM_XML = "/pom.xml";

	private static final String PACKAGING_XCODE_STATIC_LIBRARY = "xcode-static-library";

	private static final String PACKAGING_XCODE = "xcode";
	
	private static final String PACKAGING_XCODE_WORLSAPCE = "xcode-workspace";

	private static final String PATH_TXT = "path.txt";

	private static final String DO_NOT_CHECKIN_BUILD = "/do_not_checkin/build";

	/**
	 * Location of the xcodebuild executable.
	 * 
	 * @parameter expression="${xcodebuild}" default-value="/usr/bin/xcodebuild"
	 */
	private File xcodeCommandLine;

	/**
	 * Project Name
	 * 
	 * @parameter
	 */
	private String xcodeProject;

	/**
	 * Target to be built
	 * 
	 * @parameter expression="${targetName}"
	 */
	private String xcodeTarget;
	
	/**
	 * @parameter expression="${encrypt}"
	 */
	private boolean encrypt;

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * @parameter expression="${basedir}"
	 */
	private String basedir;
	/**
	 * @parameter expression="${unittest}"
	 */
	private boolean unittest;
	/**
	 * Build directory.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File buildDirectory;

	/**
	 * @parameter expression="${configuration}" default-value="Debug"
	 */
	private String configuration;

	/**
	 * @parameter expression="${sdk}" default-value="iphonesimulator5.0"
	 */
	private String sdk;
	
	/**
	 * @parameter 
	 */
	protected String gccpreprocessor;

	/**
	 * The java sources directory.
	 * 
	 * @parameter default-value="${project.basedir}"
	 * 
	 * @readonly
	 */
	protected File baseDir;

	/**
	 * @parameter expression="${environmentName}" required="true"
	 */
	protected String environmentName;

	/**
	 * XML property list file. In this file the webserverName
	 * 
	 * @parameter expression="${plistfile}"
	 *            default-value="phresco-env-config.xml"
	 */
	protected String plistFile;
	
	/**
	 * @parameter expression="${buildNumber}" required="true"
	 */
	protected String buildNumber;
	
	/**
	 * @parameter expression="${applicationTest}" default-value="true"
	 */
	private boolean applicationTest;
	
	/**
	 * @parameter expression="${projectType}" default-value="xcode"
	 */
	private String projectType;
	
	protected int buildNo;
	private File srcDir;
	private File buildDirFile;
	private File buildInfoFile;
	private List<BuildInfo> buildInfoList;
	private int nextBuildNo;
	private Date currentDate;
	private String appFileName;
	private String dSYMFileName;
	private String deliverable;
	private Map<String, Object> sdkOptions;
	
	/**
	 * Execute the xcode command line utility.
	 */
	public void execute() throws MojoExecutionException {
		getLog().info("This is 2.000000000000000000000000");
		if (!xcodeCommandLine.exists()) {
			throw new MojoExecutionException("Invalid path, invalid xcodebuild file: "
					+ xcodeCommandLine.getAbsolutePath());
		}
		getLog().info("basedir " + basedir);
		getLog().info("baseDir Name" + baseDir.getName());
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
					throw new MojoExecutionException("Selected version " +sdk +" is not available!");
				}
			} catch (IOException e2) {
				throw new MojoExecutionException("SDK verification failed!");
			} catch (InterruptedException e2) {
				throw new MojoExecutionException("SDK verification interrupted!");
            }
			
			try {
			init();
			configure();
			
			// if this is static library , two .a files need to be generated and fat file generation
			if (PACKAGING_XCODE_STATIC_LIBRARY.equals(project.getPackaging()) && !unittest) {
				getLog().info("Static lib file generation started ");
				// generate .a file for device and sim and generate fat file and provide deliverables
				createFatFileDeliverables();
			} else {
				getLog().info("App file generation started ");
				// when packaging type is xcode , it should generate app file
				executeAppCreateCommads();
			}
			
			// when packaging type is xcode , it should cpme here to package app files
			if(!unittest && !PACKAGING_XCODE_STATIC_LIBRARY.equals(project.getPackaging())) {
				getLog().info("Creating app file deliverables ");
				//In case of unit testcases run, the APP files will not be generated.
				// if packagin is xcode go with following steps
				if (PACKAGING_XCODE.equals(project.getPackaging())) {
					getLog().info("xcode archiving started ");
					createdSYM();
					createApp(); 
				} else {
					throw new MojoExecutionException("Packaging is not defined!");
				}
			}
			/*
			 * child.waitFor();
			 * 
			 * InputStream in = child.getInputStream(); InputStream err =
			 * child.getErrorStream(); getLog().error(sb.toString());
			 */
		} catch (IOException e) {
			getLog().error("An IOException occured.");
			throw new MojoExecutionException("An IOException occured", e);
		} catch (InterruptedException e) {
			getLog().error("The clean process was been interrupted.");
			throw new MojoExecutionException("The clean process was been interrupted", e);
		} catch (MojoFailureException e) {
			throw new MojoExecutionException("An MojoFailure Exception occured", e);
		}
		File directory = new File(this.basedir + POM_XML);
		this.project.getArtifact().setFile(directory);
	}

	private void createFatFileDeliverables() throws IOException,
			InterruptedException, MojoExecutionException {
		// getting machine sim and device sdks
		List<String> iphoneSimSdks = IosSdkUtil.getMacSdks(MacSdkType.iphonesimulator);
		List<String> iphoneOSSdks = IosSdkUtil.getMacSdks(MacSdkType.iphoneos);
		
		// generate .a file for simulator
		File simDotAFile = null;
		File simDotAFileBaseFolder = null;
		if (CollectionUtils.isNotEmpty(iphoneSimSdks)) {
			getLog().info("Iphone static lib creation .");
			sdk = iphoneSimSdks.get(0);
			executeAppCreateCommads(); // executing command to generate .a file
			simDotAFile = getDotAFile();
			simDotAFileBaseFolder = getDotAFileBaseFolder();
			getLog().info("Simulator dot a file ..... " + simDotAFile);
		}

		// generate .a file for device
		File deviceDotAFile = null;
		File deviceDotAFileBaseFolder = null;
		if (CollectionUtils.isNotEmpty(iphoneOSSdks)) {
			getLog().info("Iphone device lib generation ");
			sdk = iphoneOSSdks.get(0);
			executeAppCreateCommads();  // executing command to generate .a file
			deviceDotAFile = getDotAFile();
			deviceDotAFileBaseFolder = getDotAFileBaseFolder();
			getLog().info("device dot a file ..... " + deviceDotAFile);
		}
		
		// if both the files are generated, genearte fat file and pzck it
		if (simDotAFile.exists() && deviceDotAFile.exists()) {
			getLog().info("sim and device library file created.. Copying to Build directory....." + project.getBuild().getFinalName());
			String buildName = project.getBuild().getFinalName() + '_' + getTimeStampForBuildName(currentDate);
			File baseFolder = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName);
			
			if (!baseFolder.exists()) {
				baseFolder.mkdirs();
				getLog().info("build output direcory created at " + baseFolder.getAbsolutePath());
			}
			
			// copy include folder and .a files to build directory
			getLog().info("simDotAFileBaseFolder of static library " + simDotAFileBaseFolder.getAbsolutePath());
			getLog().info("baseFolder of static library " + baseFolder.getAbsolutePath());
			XcodeUtil.copyFolder(simDotAFileBaseFolder, baseFolder); // sim copied to base folder
			
			// include dir is same for both simulator and device. so can copy either whole folder and we need to delete .a file. bcz we will create a fat file and we will plca here
			File aFile = getFile(STATIC_LIB_EXT, baseFolder);
			String fatFileName = "";
			if (aFile.exists()) {
				fatFileName = aFile.getName();
				FileUtils.deleteQuietly(aFile);
			}
			
			// pack and place generated .a file and delete old .a file
			generateFatFile(simDotAFile, deviceDotAFile, baseFolder, fatFileName);
			
			// generating zip file
			createDeliverables(buildName, baseFolder);
			
			// writing build info
			File outputlibFile = getDotAFile();
			File destFile = new File(baseFolder, outputlibFile.getName());
			appFileName = destFile.getAbsolutePath();
			getLog().info("static library app name ... " + destFile.getName());
			boolean isDeviceBuild = Boolean.FALSE;
			writeBuildInfo(true, appFileName, isDeviceBuild);
			
		// if both the files are not available, pack with 
		} else if (simDotAFile.exists()) {
			getLog().info("Simulator library file created.. Copying to Build directory. ");
			createStaticLibrary(simDotAFileBaseFolder);
		} else if (deviceDotAFile.exists()) {
			getLog().info("Device library file created.. Copying to Build directory. ");
			createStaticLibrary(deviceDotAFileBaseFolder);
		} else {
			throw new MojoExecutionException("Static library is not generated! ");
		}
	}

	private void generateFatFile (File simFile, File deviceFile, File outputLocation, String fatFileName) throws MojoExecutionException {
		getLog().info("Fat file generation started.");
		try {
			if (!simFile.exists()) {
				throw new MojoExecutionException("simulator static library not found ");
			}
			
			if (!deviceFile.exists()) {
				throw new MojoExecutionException("device static library not found ");
			}
			
			if (!outputLocation.exists()) {
				outputLocation.mkdirs();
				getLog().info("Fat file output folder create " + outputLocation.getAbsolutePath());
			}
			
			// output file name
			String fatFileLibName = "";
			if (StringUtils.isEmpty(fatFileName)) {
				fatFileLibName = LIB + project.getBuild().getFinalName();
			} else {
				fatFileLibName = FilenameUtils.removeExtension(fatFileName);
			}
			fatFileLibName = fatFileLibName + FAT_FILE + DOT_A_EXT;
			
			// excecuting lipo command to merge sim and device static library .a files
			// lipo -create libPhresco.a libPhresco.a -output libPhrescoFatFile.a 
			List<String> cmdargs = new ArrayList<String>();
			cmdargs.add(LIPO);
			cmdargs.add(CREATE);
			cmdargs.add(simFile.getAbsolutePath());
			cmdargs.add(deviceFile.getAbsolutePath());
			cmdargs.add(OUTPUT);
			cmdargs.add(outputLocation.getAbsolutePath() + File.separator + fatFileLibName);
			getLog().info("static library falt file generation command . " + cmdargs);
			ProcessBuilder pb = new ProcessBuilder(cmdargs);
			pb.redirectErrorStream(true);
			Process proc = pb.start();
			proc.waitFor();
			
			// Consume subprocess output and write to stdout for debugging
			InputStream is = new BufferedInputStream(proc.getInputStream());
			int singleByte = 0;
			while ((singleByte = is.read()) != -1) {
				System.out.write(singleByte);
			}
			getLog().info("Fat file generation completed .");
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to generate falt library file. ", e);
		}
	}
	
	private void executeAppCreateCommads() throws IOException, InterruptedException, MojoExecutionException {
		ProcessBuilder pb = new ProcessBuilder(xcodeCommandLine.getAbsolutePath());
		// Include errors in output
		pb.redirectErrorStream(true);

		List<String> commands = pb.command();
		if (xcodeProject != null) {
			// based on project type , it should be changed to -workspace
			if (PACKAGING_XCODE_WORLSAPCE.equals(projectType)) {
				commands.add(WORKSPACE);
			} else {
				commands.add(PROJECT);
			}
			commands.add(xcodeProject);
		}
		
		if (StringUtils.isNotBlank(xcodeTarget)) {
			// based on project type , it should be changed
			if (PACKAGING_XCODE_WORLSAPCE.equals(projectType)) {
				commands.add(SCHEME);
			} else {
				commands.add(TARGET);
			}
			commands.add(xcodeTarget);
		}
		
		if (StringUtils.isNotBlank(configuration)) {
			commands.add(CONFIGURATION);
			commands.add(configuration);
		}

		if (StringUtils.isNotBlank(sdk)) {
			commands.add(SDK);
			commands.add(sdk);
		}

		commands.add("OBJROOT=" + buildDirectory);
		commands.add("SYMROOT=" + buildDirectory);
		commands.add("DSTROOT=" + buildDirectory);

		if(StringUtils.isNotBlank(gccpreprocessor)) {
			commands.add("GCC_PREPROCESSOR_DEFINITIONS="+gccpreprocessor);
		}

		
		getLog().info("Unit test triggered from UI " + applicationTest);
		// if the user selects , logical test, we need to add clean test... for other test nothing will be added except target.
		if (unittest && !applicationTest) {
			getLog().info("Unit test for logical test triggered !!!!!!!!");
			commands.add(CMD_CLEAN);
//				commands.add("build");
		}

		commands.add(CMD_BUILD);
		
		getLog().info("List of commands" + pb.command());
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
		getLog().info("Exit Value: " + exitValue);
		if (exitValue != 0) {
			throw new MojoExecutionException("Compilation error occured. Resolve the error(s) and try again!");
		}
	}

	private void init() throws MojoExecutionException, MojoFailureException {

		try {
			//if it is logical test, no need to delete target directory
			// To Delete the buildDirectory if already exists
			if (buildDirectory.exists() && !applicationTest) {
				FileUtils.deleteDirectory(buildDirectory);
				buildDirectory.mkdirs();
			}

			buildInfoList = new ArrayList<BuildInfo>(); // initialization
			// srcDir = new File(baseDir.getPath() + File.separator +
			// sourceDirectory);
			buildDirFile = new File(baseDir, DO_NOT_CHECKIN_BUILD);
			if (!buildDirFile.exists()) {
				buildDirFile.mkdirs();
				getLog().info("Build directory created..." + buildDirFile.getPath());
			}
			buildInfoFile = new File(buildDirFile.getPath() + "/build.info");
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
	
	private void createStaticLibrary(File dotAFileBaseFolder) throws MojoExecutionException {
		File outputFile = dotAFileBaseFolder;
		if (outputFile == null) {
			getLog().error("xcodebuild failed. resultant library is not generated!");
			throw new MojoExecutionException("xcodebuild has been failed");
		}
		
		if (outputFile.exists()) {
			try {
				getLog().info("Completed " + outputFile.getAbsolutePath());
				getLog().info("Folder name ....." + baseDir.getName());
				getLog().info("library file created.. Copying to Build directory....." + project.getBuild().getFinalName());
				String buildName = project.getBuild().getFinalName() + '_' + getTimeStampForBuildName(currentDate);
				File baseFolder = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName);
				if (!baseFolder.exists()) {
					baseFolder.mkdirs();
					getLog().info("build output direcory created at " + baseFolder.getAbsolutePath());
				}
				
				// copy include folder and .a files to build directory
				getLog().info("outputFile of static library " + outputFile.getAbsolutePath());
				getLog().info("baseFolder of static library " + baseFolder.getAbsolutePath());
				XcodeUtil.copyFolder(outputFile, baseFolder);
				
				// generating zip file
				createDeliverables(buildName, baseFolder);
				
				// writing build info
				File outputlibFile = getDotAFile();
				File destFile = new File(baseFolder, outputlibFile.getName());
				appFileName = destFile.getAbsolutePath();
				getLog().info("static library app name ... " + destFile.getName());
				
				boolean isDeviceBuild = Boolean.FALSE;
				writeBuildInfo(true, appFileName, isDeviceBuild);
			} catch (IOException e) {
				throw new MojoExecutionException("Error in writing output..." + e.getLocalizedMessage());
			}
		} else {
			getLog().info("output directory not found");
		}
	}
	
	private void createApp() throws MojoExecutionException {
		File outputFile = getAppName();
		if (outputFile == null) {
			getLog().error("xcodebuild failed. resultant APP not generated!");
			throw new MojoExecutionException("xcodebuild has been failed");
		}
		
		if (outputFile.exists()) {
			try {
				getLog().info("Completed " + outputFile.getAbsolutePath());
				getLog().info("Folder name ....." + baseDir.getName());
				getLog().info("APP created.. Copying to Build directory....." + project.getBuild().getFinalName());
				String buildName = project.getBuild().getFinalName() + '_' + getTimeStampForBuildName(currentDate);
				File baseFolder = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName);
				if (!baseFolder.exists()) {
					baseFolder.mkdirs();
					getLog().info("build output direcory created at " + baseFolder.getAbsolutePath());
				}
				File destFile = new File(baseFolder, outputFile.getName());
				getLog().info("Destination file " + destFile.getAbsolutePath());
				XcodeUtil.copyFolder(outputFile, destFile);
				getLog().info("copied to..." + destFile.getName());
				appFileName = destFile.getAbsolutePath();

				createDeliverables(buildName, baseFolder);

				// writing build info
				boolean isDeviceBuild = Boolean.FALSE;
				if (sdk.startsWith(IPHONEOS)) {
					isDeviceBuild = Boolean.TRUE;
				}
				writeBuildInfo(true, appFileName, isDeviceBuild);
			} catch (IOException e) {
				throw new MojoExecutionException("Error in writing output..." + e.getLocalizedMessage());
			}

		} else {
			getLog().info("output directory not found");
		}
	}

	private void createdSYM() throws MojoExecutionException {
		File outputFile = getdSYMName();
		if (outputFile == null) {
			getLog().error("xcodebuild failed. resultant dSYM not generated!");
			throw new MojoExecutionException("xcodebuild has been failed");
		}
		if (outputFile.exists()) {

			try {
				getLog().info("dSYM created.. Copying to Build directory.....");
				String buildName = project.getBuild().getFinalName() + '_' + getTimeStampForBuildName(currentDate);
				File baseFolder = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName);
				if (!baseFolder.exists()) {
					baseFolder.mkdirs();
					getLog().info("build output direcory created at " + baseFolder.getAbsolutePath());
				}
				File destFile = new File(baseFolder, outputFile.getName());
				getLog().info("Destination file " + destFile.getAbsolutePath());
				XcodeUtil.copyFolder(outputFile, destFile);
				getLog().info("copied to..." + destFile.getName());
				dSYMFileName = destFile.getAbsolutePath();

				// create deliverables
				createDeliverables(buildName, baseFolder);

			} catch (IOException e) {
				throw new MojoExecutionException("Error in writing output..." + e.getLocalizedMessage());
			}

		} else {
			getLog().info("output directory not found");
		}
	}


	private void createDeliverables(String buildName, File baseFolder) throws MojoExecutionException {
		try {
			getLog().info("Creating deliverables.....");
			ZipArchiver zipArchiver = new ZipArchiver();
			zipArchiver.addDirectory(baseFolder);
			File deliverableZip = new File(baseDir + DO_NOT_CHECKIN_BUILD, buildName + ".zip");
			zipArchiver.setDestFile(deliverableZip);
			zipArchiver.createArchive();
			deliverable = deliverableZip.getAbsolutePath();
			getLog().info("Deliverables available at " + deliverableZip.getName());
		} catch (IOException e) {
			throw new MojoExecutionException("Error in writing output..." + e.getLocalizedMessage());
		}
	}
	
	private File getDotAFileBaseFolder() {
		String path = configuration + HYPHEN;
		if (sdk.startsWith(IPHONEOS)) {
			path = path + IPHONEOS;
		} else {
			path = path + IPHONE_SIMULATOR;
		}
		
		File baseFolder = new File(buildDirectory, path);
		File[] files = baseFolder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith(STATIC_LIB_EXT)) {
				return baseFolder;
			}
		}
		return null;
	}
	
	private File getDotAFile() {
		String path = configuration + HYPHEN;
		if (sdk.startsWith(IPHONEOS)) {
			path = path + IPHONEOS;
		} else {
			path = path + IPHONE_SIMULATOR;
		}
		
		File baseFolder = new File(buildDirectory, path);
		File[] files = baseFolder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith(STATIC_LIB_EXT)) {
				return file;
			}
		}
		return null;
	}
	
	private File getFile(String extension, File Folder) {
		File[] files = Folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith(STATIC_LIB_EXT)) {
				return file;
			}
		}
		return null;
	}
	
	private File getAppName() {
		String path = configuration + HYPHEN;
		if (sdk.startsWith(IPHONEOS)) {
			path = path + IPHONEOS;
		} else {
			path = path + IPHONE_SIMULATOR;
		}
		
		File baseFolder = new File(buildDirectory, path);
		File[] files = baseFolder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith(APP)) {
				return file;
			}
		}
		return null;
	}

	private File getdSYMName() {
		String path = configuration + HYPHEN;
		if (sdk.startsWith(IPHONEOS)) {
			path = path + IPHONEOS;
		} else {
			path = path + IPHONE_SIMULATOR;
		}

		File baseFolder = new File(buildDirectory, path);
		File[] files = baseFolder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith(DSYM)) {
				return file;
			}
		}
		return null;
	}

	private void writeBuildInfo(boolean isBuildSuccess, String appFileName1, boolean isDeviceBuild1) throws MojoExecutionException {
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
				buildInfo.setBuildStatus(BUILD_SUCCESS);
			} else {
				buildInfo.setBuildStatus(BUILD_FAILURE);
			}
			
			buildInfo.setBuildName(appFileName1);
			buildInfo.setDeliverables(deliverable);
			buildInfo.setEnvironments(envList);

			Map<String, Boolean> sdkOptions = new HashMap<String, Boolean>(2);
			sdkOptions.put(XCodeConstants.CAN_CREATE_IPA, isDeviceBuild1);
			sdkOptions.put(XCodeConstants.DEVICE_DEPLOY, isDeviceBuild1);
	        buildInfo.setOptions(sdkOptions);
	        
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
		SimpleDateFormat formatter = new SimpleDateFormat(DD_MMM_YYYY_HH_MM_SS);
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
		FileWriter writer = null;
		try {
			getLog().info("Configuring the project....");
			getLog().info("environment name :" + environmentName);
			getLog().info("base dir name :" + baseDir.getName());
			File srcConfigFile = null;
			String currentProjectPath = "";
			// pom.xml file have "/source" as as source directory , in that case we are getting only "/source" as string .
			// if pom.xml file has source directory as "source", we will get whole path of the file
			if (project.getBuild().getSourceDirectory().startsWith("/source")) {
				srcConfigFile = new File(baseDir, project.getBuild().getSourceDirectory() + File.separator + plistFile);
				currentProjectPath = baseDir.getAbsolutePath() + project.getBuild().getSourceDirectory();
			} else {
				srcConfigFile = new File(project.getBuild().getSourceDirectory() + File.separator + plistFile);
				currentProjectPath = project.getBuild().getSourceDirectory();
			}
			getLog().info("baseDir ... " + baseDir.getAbsolutePath());
			getLog().info("SourceDirectory ... " + project.getBuild().getSourceDirectory());
			getLog().info("Config file : " + srcConfigFile.getAbsolutePath() );
			String basedir = baseDir.getName();
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, basedir, srcConfigFile);
			pu.setDefaultEnvironment(environmentName, srcConfigFile);
			// if(encrypt) {
			// pu.encode(srcConfigFile);
			// }
			// write project source path inside source folder
			getLog().info("Project source path identification file... " + currentProjectPath);
			File projectSourceDir = new File(currentProjectPath, PATH_TXT);
			writer = new FileWriter(projectSourceDir, false);
			writer.write(currentProjectPath + File.separator);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					getLog().error(e);
				}
			}
		}
	}
}
