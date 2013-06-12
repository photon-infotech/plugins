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
package com.photon.maven.plugins.android.standalonemojos;

import static com.photon.maven.plugins.android.common.AndroidExtension.APK;
import static com.photon.maven.plugins.android.common.AndroidExtension.APKLIB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.maven.plugins.android.AbstractAndroidMojo;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.FileUtil;

/**
 * Creates the apk file. By default signs it with debug keystore.<br/>
 * Change that by setting configuration parameter
 * <code>&lt;sign&gt;&lt;debug&gt;false&lt;/debug&gt;&lt;/sign&gt;</code>.
 * 
 * @goal updatebuildinfo
 * @phase package
 * @requiresDependencyResolution apk
 */
public class UpdateBuildInfoMojo extends AbstractAndroidMojo {

	/*
	 * <p> Additional source directories that contain resources to be packaged
	 * into the apk. </p> <p> These are not source directories, that contain
	 * java classes to be compiled. It corresponds to the -df option of the
	 * apkbuilder program. It allows you to specify directories, that contain
	 * additional resources to be packaged into the apk. </p> So an example
	 * inside the plugin configuration could be:
	 * 
	 * <pre> &lt;configuration&gt; ... &lt;sourceDirectories&gt;
	 * &lt;sourceDirectory
	 * &gt;${project.basedir}/additionals&lt;/sourceDirectory&gt;
	 * &lt;/sourceDirectories&gt; ... &lt;/configuration&gt; </pre>
	 * 
	 * @parameter expression="${android.sourceDirectories}" default-value=""
	 */
	// private File[] sourceDirectories;

	/**
	 * Build location
	 * 
	 * @parameter expression="/do_not_checkin/build"
	 */
	private String buildDirectory;

	private File buildDir;

	private File buildInfoFile;
	private List<BuildInfo> buildInfoList;
	private int nextBuildNo;
	private Date currentDate;
	private String apkFileName;
	private String deliverable;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File outputFile = null, outputAlignedFile = null, destFile = null, destAlignedFile = null, packageInfoFile = null;
		String techId;
		try {

			buildInfoList = new ArrayList<BuildInfo>(); // initialization
			// srcDir = new File(baseDir.getPath() + File.separator +
			// sourceDirectory);
			buildDir = new File(baseDir.getPath() + buildDirectory);
			if (!buildDir.exists()) {
				buildDir.mkdir();
			}
			buildInfoFile = new File(buildDir.getPath() + "/build.info");
			if (baseDir.getPath().endsWith("unit")
					|| baseDir.getPath().endsWith("functional")
					|| baseDir.getPath().endsWith("performance")) {
				packageInfoFile = new File(baseDir.getParentFile().getParentFile()
						+ File.separator + ".phresco" + File.separator
						+ "phresco-package-info.xml");
		
			} else {
				packageInfoFile = new File(baseDir.getPath() + File.separator
						+ ".phresco" + File.separator
						+ "phresco-package-info.xml");
			}
			
			MojoProcessor processor = new MojoProcessor(packageInfoFile);
			Configuration configuration = processor.getConfiguration("package");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			if (baseDir.getPath().endsWith("unit")
					|| baseDir.getPath().endsWith("functional")
					|| baseDir.getPath().endsWith("performance")) {
				buildDir = baseDir.getParentFile().getParentFile();
				buildInfoFile = new File(baseDir.getPath() + buildDirectory
						+ "/build.info");
			}

			techId = configs.get("techId");
			
			if (StringUtils.isNotEmpty(techId)) {

				outputFile = new File(project.getBuild().getDirectory(),
						project.getBuild().getFinalName() + '.' + APKLIB);
				
			} else {
				
				outputFile = new File(project.getBuild().getDirectory(),
						project.getBuild().getFinalName() + '.' + APK);
				outputAlignedFile = new File(project.getBuild().getDirectory(),
						project.getBuild().getFinalName() + "-aligned." + APK);

			}

			nextBuildNo = generateNextBuildNo();

			currentDate = Calendar.getInstance().getTime();
		} catch (IOException e) {
			throw new MojoFailureException("APK could not initialize "
					+ e.getLocalizedMessage());
		} catch (PhrescoException e) {
			throw new MojoFailureException("APK could not initialize "
					+ e.getLocalizedMessage());
		}

		if (outputFile.exists()) {
			try {
				if (StringUtils.isNotEmpty(techId)) {
					getLog().info("APKLib created.. Copying to Build directory.....");
				} else {
					getLog().info("APK created.. Copying to Build directory.....");
				}
				String buildName = project.getBuild().getFinalName() + '_'
						+ getTimeStampForBuildName(currentDate);

				if (baseDir.getPath().endsWith("unit")
						|| baseDir.getPath().endsWith("functional")
						|| baseDir.getPath().endsWith("performance")) {
					buildDir = new File(baseDir.getPath() + buildDirectory);
				}

				if (StringUtils.isNotEmpty(techId)) {
					destFile = new File(buildDir, buildName + '.' + APKLIB);
					
				} else {
					
					destFile = new File(buildDir, buildName + '.' + APK);
					// Creating the file in build folder for copying the aligned APK - Created by Hari - 20-May-2013
					destAlignedFile = new File(buildDir, buildName+ "-aligned." + APK);

				}

				FileUtils.copyFile(outputFile, destFile);
				getLog().info("copied to..." + destFile.getName());
				
				/* If outputAlignedFile exists in target folder,
				 * Then we are copying it to destinationFile in build folder
				 * Added By - Hari - May, 20 , 2013
				 */
		
				if (outputAlignedFile != null && outputAlignedFile.exists()) {
					
					FileUtils.copyFile(outputAlignedFile, destAlignedFile);
					
				}
				apkFileName = destFile.getName();
				
				getLog().info("Creating deliverables.....");
				
				ZipArchiver zipArchiver = new ZipArchiver();
				File tmpFile = new File(buildDir, buildName);
				
				if (!tmpFile.exists()) {
					
					tmpFile.mkdirs();
					
				}
				FileUtils.copyFileToDirectory(destFile, tmpFile);
				
				/*To Copy the aligned apk into zip file in build folder,
				*It is for downloading the aligned apk from build Tab
				* Added by - Hari -May, 20 ,2013
				*/
				if (destAlignedFile!=null && destAlignedFile.exists()) {

					FileUtils.copyFileToDirectory(destAlignedFile, tmpFile);

				}
				if (!packageInfoFile.exists()) {
					PluginUtils.createBuildResources(packageInfoFile, baseDir,
							tmpFile);
				}
				File inputFile = new File(apkFileName);
				zipArchiver.addDirectory(tmpFile);
				File deliverableZip = new File(buildDir, buildName + ".zip");
				zipArchiver.setDestFile(deliverableZip);
				zipArchiver.createArchive();

				deliverable = deliverableZip.getPath();
				getLog().info(
						"Deliverables available at " + deliverableZip.getName());
				if (tmpFile.exists()) {
					FileUtil.delete(tmpFile);

				}

				writeBuildInfo(true);
			} catch (IOException e) {
				throw new MojoExecutionException("Error in writing output...");
			}

		}
	}

	private void writeBuildInfo(boolean isBuildSuccess)
			throws MojoExecutionException {
		try {
			PluginUtils pu = new PluginUtils();
			BuildInfo buildInfo = new BuildInfo();
			List<String> envList = pu.csvToList(environmentName);
			int buildNo = 0;
			if (buildNumber != null)
					 buildNo = Integer.parseInt(buildNumber);
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
			
			if (buildName!=null){
				
				buildInfo.setBuildName(buildName);
				
			}else{
				
			    buildInfo.setBuildName(apkFileName);
			}
			buildInfo.setDeliverables(deliverable);
			buildInfo.setEnvironments(envList);
			buildInfoList.add(buildInfo);
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(buildInfoFile);
			gson.toJson(buildInfoList, writer);

			writer.close();
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private String getTimeStampForDisplay(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"dd/MMM/yyyy HH:mm:ss");
		String timeStamp = formatter.format(currentDate.getTime());
		return timeStamp;
	}

	private String getTimeStampForBuildName(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"dd-MMM-yyyy-HH-mm-ss");
		String timeStamp = formatter.format(currentDate.getTime());
		return timeStamp;
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

}
