/*
 * ###
 * Android Maven Plugin - android-maven-plugin
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
/*
 * Copyright (C) 2009 Jayway AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.photon.maven.plugins.android;

import static com.photon.maven.plugins.android.common.AndroidExtension.APK;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

import com.photon.maven.plugins.android.configuration.Zipalign;

/**
 * Implementation for the zipaplign goal. Implements parsing parameters from pom
 * or command line arguments and sets useful defaults as well.
 * 
 */
public abstract class AbstractZipalignMojo extends AbstractAndroidMojo {

	/**
	 * The configuration for the zipalign goal. As soon as a zipalign goal is
	 * invoked the command will be executed unless the skip parameter is set. By
	 * default the input file is the apk produced by the build in target. The
	 * outputApk will use the postfix -aligned.apk. The following shows a
	 * default full configuration of the zipalign goal as an example for changes
	 * as plugin configuration.
	 * 
	 * <pre>
	 * &lt;zipalign&gt;
	 *     &lt;skip&gt;false&lt;/skip&gt;
	 *     &lt;verbose&gt;true&lt;/verbose&gt;
	 *     &lt;inputApk&gt;${project.build.directory}/${project.artifactId}.apk&lt;/inputApk&gt;
	 *     &lt;outputApk&gt;${project.build.directory}/${project.artifactId}-aligned.apk&lt;/outputApk&gt;
	 * &lt;/zipalign&gt;
	 * </pre>
	 * 
	 * Values can also be configured as properties on the command line as
	 * android.zipalign.* or in pom or settings file as properties like
	 * zipalign.*.
	 * 
	 * @parameter
	 */
	private Zipalign zipalign;

	/**
	 * Skip the zipalign goal execution.
	 * 
	 * @parameter expression="${android.zipalign.skip}"
	 * @see com.photon.maven.plugins.android.configuration.Zipalign#skip
	 */
	private Boolean zipalignSkip;

	/**
	 * Activate verbose output for the zipalign goal execution.
	 * 
	 * @parameter expression="${android.zipalign.verbose}"
	 * @see com.photon.maven.plugins.android.configuration.Zipalign#verbose
	 */
	private Boolean zipalignVerbose;

	/**
	 * The apk file to be zipaligned. Per default the file is taken from build
	 * directory (target normally) using the build final name as file name and
	 * apk as extension.
	 * 
	 * @parameter expression="${android.zipalign.inputApk}"
	 * @see com.photon.maven.plugins.android.configuration.Zipalign#inputApk
	 */
	private String zipalignInputApk;

	/**
	 * The apk file produced by the zipalign goal. Per default the file is
	 * placed into the build directory (target normally) using the build final
	 * name appended with "-aligned" as file name and apk as extension.
	 * 
	 * @parameter expression="${android.zipalign.outputApk}"
	 * @see com.photon.maven.plugins.android.configuration.Zipalign#outputApk
	 */
	private String zipalignOutputApk;

	private Boolean parsedSkip;
	private Boolean parsedVerbose;
	private String parsedInputApk;
	private String parsedOutputApk;
	private File buildDir;
	private Date currentDate = Calendar.getInstance().getTime();
	/**
	 * Build location
	 * 
	 * @parameter expression="/do_not_checkin/build"
	 */
	private String buildDirectory;

	/**
	 * the apk file to be zipaligned.
	 */
	private File apkFile;
	/**
	 * the output apk file for the zipalign process.
	 */
	private File alignedApkFile;

	/**
	 * actually do the zipalign
	 * 
	 * @throws MojoExecutionException
	 */
	protected void zipalign() throws MojoExecutionException {

		// If we're not on a supported packaging with just skip (Issue 87)
		// http://code.google.com/p/maven-android-plugin/issues/detail?id=87
		if (!SUPPORTED_PACKAGING_TYPES.contains(project.getPackaging())) {
			getLog().info("Skipping zipalign on " + project.getPackaging());
			return;
		}

		parseParameters();
		if (parsedSkip) {
			getLog().info("Skipping zipalign");
		} else {
			CommandExecutor executor = CommandExecutor.Factory
					.createDefaultCommmandExecutor();
			executor.setLogger(this.getLog());

			String command = getAndroidSdk().getZipalignPath();

			List<String> parameters = new ArrayList<String>();
			if (parsedVerbose) {
				parameters.add("-v");
			}
			parameters.add("-f"); // force overwriting existing output file
			parameters.add("4"); // byte alignment has to be 4!
			parameters.add(parsedInputApk);
			parameters.add(parsedOutputApk);

			try {
				getLog().info("Running command: " + command);
				getLog().info("with parameters: " + parameters);
				executor.executeCommand(command, parameters);

				// Attach the resulting artifact (Issue 88)
				// http://code.google.com/p/maven-android-plugin/issues/detail?id=88
				File aligned = new File(parsedOutputApk);
				
				if (aligned.exists()) {
					projectHelper.attachArtifact(project, APK, "aligned",
							aligned);
					getLog().info(
							"Attach " + aligned.getAbsolutePath()
									+ " to the project");
					
				} else {
					getLog().error(
							"Cannot attach " + aligned.getAbsolutePath()
									+ " to the project"
									+ " - The file does not exist");
				}
			} catch (ExecutionException e) {
				throw new MojoExecutionException("", e);
			}
		}
	}

	private void parseParameters() {
		getLog().debug("Parsing parameters");
		// <zipalign> exist in pom file
		if (zipalign != null) {
			// <zipalign><skip> exists in pom file
			if (zipalign.isSkip() != null) {
				parsedSkip = zipalign.isSkip();
			} else {
				parsedSkip = determineSkip();
			}

			// <zipalign><verbose> exists in pom file
			if (zipalign.isVerbose() != null) {
				parsedVerbose = zipalign.isVerbose();
			} else {
				parsedVerbose = determineVerbose();
			}

			// <zipalign><inputApk> exists in pom file
			if (zipalign.getInputApk() != null) {
				parsedInputApk = zipalign.getInputApk();
			} else {
				parsedInputApk = determineInputApk();
			}

			// <zipalign><outputApk> exists in pom file
			if (zipalign.getOutputApk() != null) {
				parsedOutputApk = zipalign.getOutputApk();

			} else {
				parsedOutputApk = determineOutputApk();
				
			}
			

		}
		// command line options
		else {
			parsedSkip = determineSkip();
			parsedVerbose = determineVerbose();
			parsedInputApk = determineInputApk();
			parsedOutputApk = determineOutputApk();
			
			
		}

		getLog().debug("skip:" + parsedSkip);
		getLog().debug("verbose:" + parsedVerbose);
		getLog().debug("inputApk:" + parsedInputApk);
		getLog().debug("outputApk:" + parsedOutputApk);
	}

	/**
	 * Get skip value for zipalign from command line option.
	 * 
	 * @return if available return command line value otherwise return default
	 *         false.
	 */
	private Boolean determineSkip() {
		Boolean enabled;
		if (zipalignSkip != null) {
			enabled = zipalignSkip;
		} else {
			getLog().debug("Using default for zipalign.skip=false");
			enabled = Boolean.FALSE;
		}
		return enabled;
	}

	/**
	 * Get verbose value for zipalign from command line option.
	 * 
	 * @return if available return command line value otherwise return default
	 *         false.
	 */
	private Boolean determineVerbose() {
		Boolean enabled;
		if (zipalignVerbose != null) {
			enabled = zipalignVerbose;
		} else {
			getLog().debug("Using default for zipalign.verbose=false");
			enabled = Boolean.FALSE;
		}
		return enabled;
	}

	/**
	 * Gets the apk file location from basedir/target/finalname.apk
	 * 
	 * @return absolute path.
	 */
	private String getApkLocation() {
		if (apkFile == null)
			apkFile = new File(project.getBuild().getDirectory(), project
					.getBuild().getFinalName() + "." + APK);
		return apkFile.getAbsolutePath();
	}

	/**
	 * Gets the apk file location from basedir/target/finalname-aligned.apk.
	 * "-aligned" is the inserted string for the output file.
	 * 
	 * @return absolute path.
	 * @throws IOException
	 */
	private String getAlignedApkLocation() {
		
		if (alignedApkFile == null)
			alignedApkFile = new File(project.getBuild().getDirectory(),
					project.getBuild().getFinalName() + "-aligned." + APK);
		
		   
			
			buildDir = new File(baseDir.getPath() + buildDirectory);
			System.out.println("BuildDir created ====="+ buildDir);
			
			if (!buildDir.exists()) {
				buildDir.mkdir();
				System.out.println("**BuildDir created*****");
			}
			return alignedApkFile.getAbsolutePath();

	}

	/**
	 * Get inputApk value for zipalign from command line option.
	 * 
	 * @return if available return command line value otherwise return default.
	 */
	private String determineInputApk() {
		String inputApk;
		if (zipalignInputApk != null) {
			inputApk = zipalignInputApk;
		} else {
			String inputPath = getApkLocation();
			getLog().debug("Using default for zipalign.inputApk: " + inputPath);
			inputApk = inputPath;
		}
		return inputApk;
	}

	/**
	 * Get outputApk value for zipalign from command line option.
	 * 
	 * @return if available return command line value otherwise return default.
	 */
	private String determineOutputApk() {
		String outputApk;
		if (zipalignOutputApk != null) {
			outputApk = zipalignOutputApk;

		} else {
			String outputPath = getAlignedApkLocation();
			getLog().debug(
					"Using default for zipalign.outputApk: " + outputPath);
			outputApk = outputPath;
			
		}
		return outputApk;
	}
	
	private String getTimeStampForBuildName(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
		String timeStamp = formatter.format(currentDate.getTime());
		return timeStamp;
	}

}
