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


package com.photon.phresco.plugins.android;

import static com.photon.maven.plugins.android.common.AndroidExtension.APK;
import static com.photon.maven.plugins.android.common.AndroidExtension.APKLIB;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.AbstractScanner;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;

import com.photon.maven.plugins.android.AbstractAndroidMojo;
import com.photon.maven.plugins.android.AndroidSdk;
import com.photon.maven.plugins.android.AndroidSigner;
import com.photon.maven.plugins.android.CommandExecutor;
import com.photon.maven.plugins.android.ExecutionException;
import com.photon.maven.plugins.android.common.NativeHelper;
import com.photon.maven.plugins.android.configuration.Sdk;
import com.photon.maven.plugins.android.configuration.Sign;
import com.photon.maven.plugins.android.phase09package.ApkBuilder;
import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.exception.PhrescoException;


public class Package {

	/**
	 * <p>
	 * How to sign the apk.
	 * </p>
	 * <p>
	 * Looks like this:
	 * </p>
	 *
	 * <pre>
	 * &lt;sign&gt;
	 *     &lt;debug&gt;auto&lt;/debug&gt;
	 * &lt;/sign&gt;
	 * </pre>
	 * <p>
	 * Valid values for <code>&lt;debug&gt;</code> are:
	 * <ul>
	 * <li><code>true</code> = sign with the debug keystore.
	 * <li><code>false</code> = don't sign with the debug keystore.
	 * <li><code>both</code> = create a signed as well as an unsigned apk.
	 * <li><code>auto</code> (default) = sign with debug keystore, unless
	 * another keystore is defined. (Signing with other keystores is not yet
	 * implemented. See <a
	 * href="http://code.google.com/p/maven-android-plugin/issues/detail?id=2"
	 * >Issue 2</a>.)
	 * </ul>
	 * </p>
	 * <p>
	 * Can also be configured from command-line with parameter
	 * <code>-Dandroid.sign.debug</code>.
	 * </p>
	 *
	 * @parameter
	 */
	private Sign sign;

	/**
	 * <p>
	 * Parameter designed to pick up <code>-Dandroid.sign.debug</code> in case
	 * there is no pom with a <code>&lt;sign&gt;</code> configuration tag.
	 * </p>
	 * <p>
	 * Corresponds to
	 * {@link com.photon.maven.plugins.android.configuration.Sign#debug}.
	 * </p>
	 *
	 * @parameter expression="${android.sign.debug}" default-value="auto"
	 * @readonly
	 */
	private String signDebug = "auto";

	/**
	 * <p>
	 * A possibly new package name for the application. This value will be
	 * passed on to the aapt parameter --rename-manifest-package. Look to aapt
	 * for more help on this.
	 * </p>
	 *
	 * @parameter expression="${android.renameManifestPackage}"
	 */
	private String renameManifestPackage;

	/**
	 * <p>
	 * Rewrite the manifest so that all of its instrumentation components target
	 * the given package. This value will be passed on to the aapt parameter
	 * --rename-instrumentation-target-package. Look to aapt for more help on
	 * this.
	 * </p>
	 *
	 * @parameter expression="${android.renameInstrumentationTargetPackage}"
	 */
	private String renameInstrumentationTargetPackage;

	/**
	 * <p>
	 * Allows to detect and extract the duplicate files from embedded jars. In
	 * that case, the plugin analyzes the content of all embedded dependencies
	 * and checks they are no duplicates inside those dependencies. Indeed,
	 * Android does not support duplicates, and all dependencies are inlined in
	 * the APK. If duplicates files are found, the resource is kept in the first
	 * dependency and removes from others.
	 *
	 * @parameter expression="${android.extractDuplicates}"
	 *            default-value="false"
	 */
	private boolean extractDuplicates;

	/**
	 * <p>
	 * Temporary folder for collecting native libraries.
	 * </p>
	 *
	 * @parameter default-value="${project.build.directory}/libs"
	 * @readonly
	 */
	private File nativeLibrariesOutputDirectory;

	/**
	 * <p>
	 * Default hardware architecture for native library dependencies (with
	 * {@code &lt;type>so&lt;/type>}).
	 * </p>
	 * <p>
	 * This value is used for dependencies without classifier, if
	 * {@code nativeLibrariesDependenciesHardwareArchitectureOverride} is not
	 * set.
	 * </p>
	 * <p>
	 * Valid values currently include {@code armeabi} and {@code armeabi-v7a}.
	 * </p>
	 *
	 * @parameter expression=
	 *            "${android.nativeLibrariesDependenciesHardwareArchitectureDefault}"
	 *            default-value="armeabi"
	 */
	private String nativeLibrariesDependenciesHardwareArchitectureDefault;

	/**
	 * <p>
	 * Classifier to add to the artifact generated. If given, the artifact will
	 * be an attachment instead.
	 * </p>
	 *
	 * @parameter
	 */
	private String classifier;

	/**
	 * <p>
	 * Override hardware architecture for native library dependencies (with
	 * {@code &lt;type>so&lt;/type>}).
	 * </p>
	 * <p>
	 * This overrides any classifier on native library dependencies, and any
	 * {@code nativeLibrariesDependenciesHardwareArchitectureDefault}.
	 * </p>
	 * <p>
	 * Valid values currently include {@code armeabi} and {@code armeabi-v7a}.
	 * </p>
	 *
	 * @parameter expression=
	 *            "${android.nativeLibrariesDependenciesHardwareArchitectureOverride}"
	 */
	private String nativeLibrariesDependenciesHardwareArchitectureOverride;

	/**
	 * <p>
	 * Additional source directories that contain resources to be packaged into
	 * the apk.
	 * </p>
	 * <p>
	 * These are not source directories, that contain java classes to be
	 * compiled. It corresponds to the -df option of the apkbuilder program. It
	 * allows you to specify directories, that contain additional resources to
	 * be packaged into the apk.
	 * </p>
	 * So an example inside the plugin configuration could be:
	 *
	 * <pre>
	 * &lt;configuration&gt;
	 * 	  ...
	 *    &lt;sourceDirectories&gt;
	 *      &lt;sourceDirectory&gt;${project.basedir}/additionals&lt;/sourceDirectory&gt;
	 *   &lt;/sourceDirectories&gt;
	 *   ...
	 * &lt;/configuration&gt;
	 * </pre>
	 *
	 * @parameter expression="${android.sourceDirectories}" default-value=""
	 */
	private File[] sourceDirectories;

	/**
	 * @component
	 * @readonly
	 * @required
	 */
	protected ArtifactFactory artifactFactory;

	/**
	 * Build location
	 *
	 * @parameter expression="/do_not_checkin/build"
	 */
	private String buildDirectory = "/do_not_checkin/build";

	private MavenProject project;
	protected MavenProjectHelper projectHelper;
	private File srcDir;
	private File targetDir;
	private File buildDir;
	private File buildInfoFile;
	private List<BuildInfo> buildInfoList;
	private int nextBuildNo;
	private Date currentDate;
	private String apkFileName;
	private String deliverable;
	private File baseDir;
	private String environmentName;
	//Dynamic parameters
	private File sourceDirectory;
	private Log log;
	private boolean generateApk;
	private File extractedDependenciesRes;
	private File resourceDirectory;
	private File combinedRes;
	private File extractedDependenciesAssets;
	protected static final List<String> EXCLUDED_DEPENDENCY_SCOPES = Arrays
	.asList("provided", "import");
	private Sdk sdk;     //TODO Give value for Sdk
	private File sdkPath;  //TODO Give value for Sdkpath
	private String sdkPlatform = "4.0.3";  //TODO Give value for SdkPlatform
	public static final String ENV_ANDROID_HOME = "ANDROID_HOME";
	private File nativeLibrariesDirectory;
	private List<RemoteRepository> projectRepos;
	private RepositorySystemSession repoSession;
	private RepositorySystem repoSystem;
	private File unpackedApkLibsDirectory;
	private File resourceOverlayDirectory;
	private File[] resourceOverlayDirectories;
	private File combinedAssets;
	private File assetsDirectory;
	private File androidManifestFile;
	private String configurations;
	private String[] aaptExtraArgs = {};
	
	private static final Pattern PATTERN_JAR_EXT = Pattern.compile("^.+\\.jar$", 2);

	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get("environmentName");
        project = mavenProjectInfo.getProject();
        
        //TODO change into dynamic content
        generateApk = true;
        sourceDirectory = new File(project.getBasedir() + "/source/src/");
        extractedDependenciesRes = new File("do_not_checkin/target/generated-sources/extracted-dependencies/res");
		resourceDirectory = new File(baseDir.getPath() + "/source/res");
		combinedRes = new File("do_not_checkin/target/generated-sources/combined-resources/res");
		extractedDependenciesAssets = new File("do_not_checkin/target/generated-sources/extracted-dependencies/assets");
		nativeLibrariesDirectory = new File(baseDir.getPath() + "/libs");
		unpackedApkLibsDirectory = new File("do_not_checkin/target/unpack/apklibs");
		resourceOverlayDirectory = new File(baseDir.getPath() + "/res-overlay");
		combinedAssets = new File("do_not_checkin/target/generated-sources/combined-assets/assets");
		assetsDirectory = new File(baseDir.getPath() + "/source/assets");
		androidManifestFile = new File(baseDir.getPath() + "/source/AndroidManifest.xml");
		
		// Make an early exit if we're not supposed to generate the APK
		if (!generateApk) {
			return;
		}

		buildInfoList = new ArrayList<BuildInfo>(); // initialization
		srcDir = new File(baseDir.getPath() + File.separator + sourceDirectory);
		buildDir = new File(baseDir.getPath() + buildDirectory);
		if (!buildDir.exists()) {
			buildDir.mkdir();
			log.info("Build directory created..." + buildDir.getPath());
		}
		buildInfoFile = new File(buildDir.getPath() + "/build.info");

		//			nextBuildNo = generateNextBuildNo();

		currentDate = Calendar.getInstance().getTime();

		try {
			configure();
			generateIntermediateAp_();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}
		

		// Initialize apk build configuration
		File outputFile = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + '.' + APK);

		final boolean signWithDebugKeyStore = getAndroidSigner().isSignWithDebugKeyStore();
		try {
			if (getAndroidSigner().shouldCreateBothSignedAndUnsignedApk()) {
				log.info("Creating debug key signed apk file " + outputFile);

				createApkFile(outputFile, true);
				final File unsignedOutputFile = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + "-unsigned." + APK);
				log.info("Creating additional unsigned apk file " + unsignedOutputFile);
				createApkFile(unsignedOutputFile, false);
				projectHelper.attachArtifact(project, unsignedOutputFile, classifier == null ? "unsigned" : classifier + "_unsigned");
			} else {
				createApkFile(outputFile, signWithDebugKeyStore);
			} 
		}
		catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		}

		if (classifier == null) {
			// Set the generated .apk file as the main artifact (because the pom
			// states <packaging>apk</packaging>)
			project.getArtifact().setFile(outputFile);
		} else {
			// If there is a classifier specified, attach the artifact using that
			projectHelper.attachArtifact(project, outputFile, classifier);
		}
		/*if (outputFile.exists()) {

			try {
				log.info("APK created.. Copying to Build directory.....");
				String buildName = project.getBuild().getFinalName() + '_' + getTimeStampForBuildName(currentDate);
				File destFile = new File(buildDir, buildName + '.' + APK);
				FileUtils.copyFile(outputFile, destFile);
				log.info("copied to..." + destFile.getName());
				apkFileName = destFile.getName();

				log.info("Creating deliverables.....");
				ZipArchiver zipArchiver = new ZipArchiver();
				File inputFile = new File(apkFileName);
				zipArchiver.addFile(destFile, destFile.getName());
				File deliverableZip = new File(buildDir, buildName + ".zip");
				zipArchiver.setDestFile(deliverableZip);
				zipArchiver.createArchive();

				deliverable = deliverableZip.getName();
				log.info("Deliverables available at " + deliverableZip.getName());
				writeBuildInfo(true);
			} catch (IOException e) {
				throw new MojoExecutionException("Error in writing output...");
			}

		}*/
	}

	private void configure() throws MojoExecutionException {
			try {
			if (StringUtils.isEmpty(environmentName)) {
				return;
			}
			log.info("Configuring the project....");
			log.info(sourceDirectory.getParent() + "\\assets\\phresco-env-config.xml");
			File srcConfigFile = new File(sourceDirectory.getParent(), "\\assets\\phresco-env-config.xml");
			
			String basedir = baseDir.getName();
			PluginUtils pu = new PluginUtils();
			pu.executeUtil(environmentName, basedir, srcConfigFile);
			pu.setDefaultEnvironment(environmentName, srcConfigFile);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	

	/*private int generateNextBuildNo() throws IOException {
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

		nextBuildNo = buildArray[buildArray.length - 1] + 1; // increment 1 to the max in the build list
		return nextBuildNo;
	}*/

	void createApkFile(File outputFile, boolean signWithDebugKeyStore) throws MojoExecutionException {

		File dexFile = new File(project.getBuild().getDirectory(), "classes.dex");
		File zipArchive = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".ap_");
		ArrayList<File> sourceFolders = new ArrayList<File>();
		if (sourceDirectories != null) {
			for (File f : sourceDirectories) {
				sourceFolders.add(f);
			}
		}
		ArrayList<File> jarFiles = new ArrayList<File>();
		ArrayList<File> nativeFolders = new ArrayList<File>();

		boolean useInternalAPKBuilder = true;
		try {
			initializeAPKBuilder();
			// Ok...
			// So we can try to use the internal ApkBuilder
		} catch (Throwable e) {
			// Not supported platform try to old way.
			useInternalAPKBuilder = false;
		}

		// Process the native libraries, looking both in the current build
		// directory as well as
		// at the dependencies declared in the pom. Currently, all .so files are
		// automatically included
		processNativeLibraries(nativeFolders);

		if (useInternalAPKBuilder) {
			doAPKWithAPKBuilder(outputFile, dexFile, zipArchive, sourceFolders, jarFiles, nativeFolders, false, signWithDebugKeyStore, false);
		} else {
			doAPKWithCommand(outputFile, dexFile, zipArchive, sourceFolders, jarFiles, nativeFolders, signWithDebugKeyStore);
		}
	}

	private final Map<String, List<File>> m_jars = new HashMap<String, List<File>>();

	private void computeDuplicateFiles(File jar) throws  IOException {
		ZipFile file = new ZipFile(jar);
		Enumeration<? extends ZipEntry> list = file.entries();
		while (list.hasMoreElements()) {
			ZipEntry ze = list.nextElement();
			if (!(ze.getName().contains("META-INF/") || ze.isDirectory())) { // Exclude
																				// META-INF
																				// and
																				// Directories
				List<File> l = m_jars.get(ze.getName());
				if (l == null) {
					l = new ArrayList<File>();
					m_jars.put(ze.getName(), l);
				}
				l.add(jar);
			}
		}
	}

	/**
	 * Creates the APK file using the internal APKBuilder.
	 *
	 * @param outputFile
	 *            the output file
	 * @param dexFile
	 *            the dex file
	 * @param zipArchive
	 *            the classes folder
	 * @param sourceFolders
	 *            the resources
	 * @param jarFiles
	 *            the embedded java files
	 * @param nativeFolders
	 *            the native folders
	 * @param verbose
	 *            enables the verbose mode
	 * @param signWithDebugKeyStore
	 *            enables the signature of the APK using the debug key
	 * @param debug
	 *            enables the debug mode
	 * @throws MojoExecutionException
	 *             if the APK cannot be created.
	 */
	private void doAPKWithAPKBuilder(File outputFile, File dexFile, File zipArchive, ArrayList<File> sourceFolders, ArrayList<File> jarFiles, ArrayList<File> nativeFolders, boolean verbose,
			boolean signWithDebugKeyStore, boolean debug) throws MojoExecutionException {

		/* Following line doesn't make any difference if we keep it or comment it
		 * Commented By - Viral - Feb 11, 2012
		 */
//		sourceFolders.add(new File(project.getBuild().getDirectory(), "android-classes"));

		for (Artifact artifact : getRelevantCompileArtifacts()) {
			if (extractDuplicates) {
				try {
					computeDuplicateFiles(artifact.getFile());
				} catch (Exception e) {
					log.warn("Cannot compute duplicates files from " + artifact.getFile().getAbsolutePath(), e);
				}
			}
			jarFiles.add(artifact.getFile());
		}

		// Check duplicates.
		if (extractDuplicates) {
			List<String> duplicates = new ArrayList<String>();
			List<File> jarToModify = new ArrayList<File>();
			for (String s : m_jars.keySet()) {
				List<File> l = m_jars.get(s);
				if (l.size() > 1) {
					log.warn("Duplicate file " + s + " : " + l);
					duplicates.add(s);
					for (int i = 1; i < l.size(); i++) {
						if (!jarToModify.contains(l.get(i))) {
							jarToModify.add(l.get(i));
						}
					}
				}
			}

			// Rebuild jars.
			for (File file : jarToModify) {
				File newJar;
				newJar = removeDuplicatesFromJar(file, duplicates);
				int index = jarFiles.indexOf(file);
				if (newJar != null) {
					jarFiles.set(index, newJar);
				}

			}
		}

		ApkBuilder builder = new ApkBuilder(outputFile, zipArchive, dexFile, signWithDebugKeyStore, (verbose) ? System.out : null);

		if (debug) {
			builder.setDebugMode(debug);
		}
		/* Following code block is responsible to make the .apk size almost doubled.
		 * Commented By - Viral - Feb 11, 2012
		 */
//		for (File sourceFolder : sourceFolders) {
//			builder.addSourceFolder(sourceFolder);
//		}

		for (File jarFile : jarFiles) {
			if (jarFile.isDirectory()) {
				String[] filenames = jarFile.list(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return PATTERN_JAR_EXT.matcher(name).matches();
					}
				});

				for (String filename : filenames) {
					builder.addResourcesFromJar(new File(jarFile, filename));
				}
			} else {
				builder.addResourcesFromJar(jarFile);
			}
		}

		for (File nativeFolder : nativeFolders) {
			builder.addNativeLibraries(nativeFolder, null);
		}

		builder.sealApk();
	}

	private File removeDuplicatesFromJar(File in, List<String> duplicates) {
		File target = new File(project.getBasedir(), "target");
		File tmp = new File(target, "unpacked-embedded-jars");
		tmp.mkdirs();
		File out = new File(tmp, in.getName());

		if (out.exists()) {
			return out;
		}
		try {
			out.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Create a new Jar file
		FileOutputStream fos = null;
		ZipOutputStream jos = null;
		try {
			fos = new FileOutputStream(out);
			jos = new ZipOutputStream(fos);
		} catch (FileNotFoundException e1) {
			log.error("Cannot remove duplicates : the output file " + out.getAbsolutePath() + " does not found");
			return null;
		}

		ZipFile inZip = null;
		try {
			inZip = new ZipFile(in);
			Enumeration<? extends ZipEntry> entries = inZip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				// If the entry is not a duplicate, copy.
				if (!duplicates.contains(entry.getName())) {
					// copy the entry header to jos
					jos.putNextEntry(entry);
					InputStream currIn = inZip.getInputStream(entry);
					copyStreamWithoutClosing(currIn, jos);
					currIn.close();
					jos.closeEntry();
				}
			}
		} catch (IOException e) {
			log.error("Cannot removing duplicates : " + e.getMessage());
			return null;
		}

		try {
			if (inZip != null) {
				inZip.close();
			}
			jos.close();
			fos.close();
			jos = null;
			fos = null;
		} catch (IOException e) {
			// ignore it.
		}
		log.info(in.getName() + " rewritten without duplicates : " + out.getAbsolutePath());
		return out;
	}

	/**
	 * Copies an input stream into an output stream but does not close the
	 * streams.
	 *
	 * @param in
	 *            the input stream
	 * @param out
	 *            the output stream
	 * @throws IOException
	 *             if the stream cannot be copied
	 */
	private static void copyStreamWithoutClosing(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.write(b, 0, n);
		}
	}

	/**
	 * Creates the APK file using the command line.
	 *
	 * @param outputFile
	 *            the output file
	 * @param dexFile
	 *            the dex file
	 * @param zipArchive
	 *            the classes folder
	 * @param sourceFolders
	 *            the resources
	 * @param jarFiles
	 *            the embedded java files
	 * @param nativeFolders
	 *            the native folders
	 * @param signWithDebugKeyStore
	 *            enables the signature of the APK using the debug key
	 * @throws MojoExecutionException
	 *             if the APK cannot be created.
	 */
	private void doAPKWithCommand(File outputFile, File dexFile, File zipArchive, ArrayList<File> sourceFolders, ArrayList<File> jarFiles, ArrayList<File> nativeFolders, boolean signWithDebugKeyStore)
			throws MojoExecutionException {

		CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
		executor.setLogger(this.log);

		List<String> commands = new ArrayList<String>();
		commands.add(outputFile.getAbsolutePath());

		if (!signWithDebugKeyStore) {
			commands.add("-u");
		}

		commands.add("-z");
		commands.add(new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".ap_").getAbsolutePath());
		commands.add("-f");
		commands.add(new File(project.getBuild().getDirectory(), "classes.dex").getAbsolutePath());
		commands.add("-rf");
		commands.add(new File(project.getBuild().getDirectory(), "classes").getAbsolutePath());

		if (nativeFolders != null && !nativeFolders.isEmpty()) {
			for (File lib : nativeFolders) {
				commands.add("-nf");
				commands.add(lib.getAbsolutePath());
			}
		}

		for (Artifact artifact : getRelevantCompileArtifacts()) {
			commands.add("-rj");
			commands.add(artifact.getFile().getAbsolutePath());
		}

		log.info(getAndroidSdk().getPathForTool("apkbuilder") + " " + commands.toString());
		try {
			executor.executeCommand(getAndroidSdk().getPathForTool("apkbuilder"), commands, project.getBasedir(), false);
		} catch (ExecutionException e) {
			throw new MojoExecutionException("", e);
		}
	}

	private void initializeAPKBuilder() throws MojoExecutionException {
		File file = getAndroidSdk().getSDKLibJar();
		ApkBuilder.initialize(log, file);
	}

	private void processNativeLibraries(final List<File> natives) throws MojoExecutionException {
		// Examine the native libraries directory for content. This will only be
		// true if:
		// a) the directory exists
		// b) it contains at least 1 file
		final boolean hasValidNativeLibrariesDirectory = nativeLibrariesDirectory != null && nativeLibrariesDirectory.exists()
				&& (nativeLibrariesDirectory.listFiles() != null && nativeLibrariesDirectory.listFiles().length > 0);

		// Retrieve any native dependencies or attached artifacts. This may
		// include artifacts from the ndk-build MOJO
		NativeHelper nativeHelper = new NativeHelper(project, projectRepos, repoSession, repoSystem, artifactFactory, log);
		final Set<Artifact> artifacts = nativeHelper.getNativeDependenciesArtifacts(unpackedApkLibsDirectory, true);

		final boolean hasValidBuildNativeLibrariesDirectory = nativeLibrariesOutputDirectory.exists()
				&& (nativeLibrariesOutputDirectory.listFiles() != null && nativeLibrariesOutputDirectory.listFiles().length > 0);

		if (artifacts.isEmpty() && hasValidNativeLibrariesDirectory && !hasValidBuildNativeLibrariesDirectory) {

			log.debug("No native library dependencies detected, will point directly to " + nativeLibrariesDirectory);

			// Point directly to the directory in this case - no need to copy
			// files around
			natives.add(nativeLibrariesDirectory);
		} else if (!artifacts.isEmpty() || hasValidNativeLibrariesDirectory) {
			// In this case, we may have both .so files in it's normal location
			// as well as .so dependencies

			// Create the ${project.build.outputDirectory}/libs
			final File destinationDirectory = new File(nativeLibrariesOutputDirectory.getAbsolutePath());
			destinationDirectory.mkdirs();

			// Point directly to the directory
			natives.add(destinationDirectory);

			// If we have a valid native libs, copy those files - these already
			// come in the structure required
			if (hasValidNativeLibrariesDirectory) {
				copyLocalNativeLibraries(nativeLibrariesDirectory, destinationDirectory);
			}

			if (!artifacts.isEmpty()) {
				for (Artifact resolvedArtifact : artifacts) {
					if ("so".equals(resolvedArtifact.getType())) {
						final File artifactFile = resolvedArtifact.getFile();
						try {
							final String artifactId = resolvedArtifact.getArtifactId();
							final String filename = artifactId.startsWith("lib") ? artifactId + ".so" : "lib" + artifactId + ".so";

							final File finalDestinationDirectory = getFinalDestinationDirectoryFor(resolvedArtifact, destinationDirectory);
							final File file = new File(finalDestinationDirectory, filename);
							log.debug("Copying native dependency " + artifactId + " (" + resolvedArtifact.getGroupId() + ") to " + file);
							org.apache.commons.io.FileUtils.copyFile(artifactFile, file);
						} catch (Exception e) {
							throw new MojoExecutionException("Could not copy native dependency.", e);
						}
					} else if (APKLIB.equals(resolvedArtifact.getType())) {
						natives.add(new File(getLibraryUnpackDirectory(resolvedArtifact) + "/libs"));
					}
				}
			}
		}
	}

	private File getFinalDestinationDirectoryFor(Artifact resolvedArtifact, File destinationDirectory) {
		final String hardwareArchitecture = getHardwareArchitectureFor(resolvedArtifact);

		File finalDestinationDirectory = new File(destinationDirectory, hardwareArchitecture + "/");

		finalDestinationDirectory.mkdirs();
		return finalDestinationDirectory;
	}

	private String getHardwareArchitectureFor(Artifact resolvedArtifact) {

		if (StringUtils.isNotBlank(nativeLibrariesDependenciesHardwareArchitectureOverride)) {
			return nativeLibrariesDependenciesHardwareArchitectureOverride;
		}

		final String classifier = resolvedArtifact.getClassifier();
		if (StringUtils.isNotBlank(classifier)) {
			return classifier;
		}

		return nativeLibrariesDependenciesHardwareArchitectureDefault;
	}

	private void copyLocalNativeLibraries(final File localNativeLibrariesDirectory, final File destinationDirectory) throws MojoExecutionException {
		log.debug("Copying existing native libraries from " + localNativeLibrariesDirectory);
		try {
			org.apache.commons.io.FileUtils.copyDirectory(localNativeLibrariesDirectory, destinationDirectory, new FileFilter() {
				public boolean accept(final File pathname) {
					return pathname.getName().endsWith(".so");
				}
			});
		} catch (IOException e) {
			log.error("Could not copy native libraries: " + e.getMessage(), e);
			throw new MojoExecutionException("Could not copy native dependency.", e);
		}
	}

	/**
	 * Generates an intermediate apk file (actually .ap_) containing the
	 * resources and assets.
	 *
	 * @throws MojoExecutionException
	 */
	private void generateIntermediateAp_() throws MojoExecutionException {
		CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
		executor.setLogger(this.log);
		File[] overlayDirectories;

		if (resourceOverlayDirectories == null || resourceOverlayDirectories.length == 0) {
			overlayDirectories = new File[] { resourceOverlayDirectory };
		} else {
			overlayDirectories = resourceOverlayDirectories;
		}

		if (extractedDependenciesRes.exists()) {
			try {
				log.info("Copying dependency resource files to combined resource directory.");
				if (!combinedRes.exists() && !combinedRes.mkdirs()) {
					throw new MojoExecutionException("Could not create directory for combined resources at " + combinedRes.getAbsolutePath());
				}
				org.apache.commons.io.FileUtils.copyDirectory(extractedDependenciesRes, combinedRes);
			} catch (IOException e) {
				throw new MojoExecutionException("", e);
			}
		}
		if (resourceDirectory.exists() && combinedRes.exists()) {
			try {
				log.info("Copying local resource files to combined resource directory.");
				org.apache.commons.io.FileUtils.copyDirectory(resourceDirectory, combinedRes, new FileFilter() {

					/**
					 * Excludes files matching one of the common file to
					 * exclude. The default excludes pattern are the ones from
					 * {org
					 * .codehaus.plexus.util.AbstractScanner#DEFAULTEXCLUDES}
					 *
					 * @see java.io.FileFilter#accept(java.io.File)
					 */
					public boolean accept(File file) {
						for (String pattern : AbstractScanner.DEFAULTEXCLUDES) {
							if (AbstractScanner.match(pattern, file.getAbsolutePath())) {
								log.debug("Excluding " + file.getName() + " from resource copy : matching " + pattern);
								return false;
							}
						}
						return true;
					}
				});
			} catch (IOException e) {
				throw new MojoExecutionException("", e);
			}
		}

		// Must combine assets.
		// The aapt tools does not support several -A arguments.
		// We copy the assets from extracted dependencies first, and then the
		// local assets.
		// This allows redefining the assets in the current project
		if (extractedDependenciesAssets.exists()) {
			try {
				log.info("Copying dependency assets files to combined assets directory.");
				org.apache.commons.io.FileUtils.copyDirectory(extractedDependenciesAssets, combinedAssets, new FileFilter() {
					/**
					 * Excludes files matching one of the common file to
					 * exclude. The default excludes pattern are the ones from
					 * {org
					 * .codehaus.plexus.util.AbstractScanner#DEFAULTEXCLUDES}
					 *
					 * @see java.io.FileFilter#accept(java.io.File)
					 */
					public boolean accept(File file) {
						for (String pattern : AbstractScanner.DEFAULTEXCLUDES) {
							if (AbstractScanner.match(pattern, file.getAbsolutePath())) {
								log.debug("Excluding " + file.getName() + " from asset copy : matching " + pattern);
								return false;
							}
						}

						return true;

					}
				});
			} catch (IOException e) {
				throw new MojoExecutionException("", e);
			}
		}

		// Next pull APK Lib assets, reverse the order to give precedence to
		// libs higher up the chain
		List<Artifact> artifactList = new ArrayList<Artifact>(getAllRelevantDependencyArtifacts());
		for (Artifact artifact : artifactList) {
			if (artifact.getType().equals(APKLIB)) {
				File apklibAsssetsDirectory = new File(getLibraryUnpackDirectory(artifact) + "/assets");
				if (apklibAsssetsDirectory.exists()) {
					try {
						log.info("Copying dependency assets files to combined assets directory.");
						org.apache.commons.io.FileUtils.copyDirectory(apklibAsssetsDirectory, combinedAssets, new FileFilter() {
							/**
							 * Excludes files matching one of the common file to
							 * exclude. The default excludes pattern are the
							 * ones from
							 * {org.codehaus.plexus.util.AbstractScanner
							 * #DEFAULTEXCLUDES}
							 *
							 * @see java.io.FileFilter#accept(java.io.File)
							 */
							public boolean accept(File file) {
								for (String pattern : AbstractScanner.DEFAULTEXCLUDES) {
									if (AbstractScanner.match(pattern, file.getAbsolutePath())) {
										log.debug("Excluding " + file.getName() + " from asset copy : matching " + pattern);
										return false;
									}
								}

								return true;

							}
						});
					} catch (IOException e) {
						throw new MojoExecutionException("", e);
					}

				}
			}
		}

		if (assetsDirectory.exists()) {
			try {
				log.info("Copying local assets files to combined assets directory.");
				org.apache.commons.io.FileUtils.copyDirectory(assetsDirectory, combinedAssets, new FileFilter() {
					/**
					 * Excludes files matching one of the common file to
					 * exclude. The default excludes pattern are the ones from
					 * {org
					 * .codehaus.plexus.util.AbstractScanner#DEFAULTEXCLUDES}
					 *
					 * @see java.io.FileFilter#accept(java.io.File)
					 */
					public boolean accept(File file) {
						for (String pattern : AbstractScanner.DEFAULTEXCLUDES) {
							if (AbstractScanner.match(pattern, file.getAbsolutePath())) {
								log.debug("Excluding " + file.getName() + " from asset copy : matching " + pattern);
								return false;
							}
						}

						return true;

					}
				});
			} catch (IOException e) {
				throw new MojoExecutionException("", e);
			}
		}

		File androidJar = getAndroidSdk().getAndroidJar();
		File outputFile = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".ap_");

		List<String> commands = new ArrayList<String>();
		commands.add("package");
		commands.add("-f");
		commands.add("-M");
		commands.add(androidManifestFile.getAbsolutePath());
		for (File resOverlayDir : overlayDirectories) {
			if (resOverlayDir != null && resOverlayDir.exists()) {
				commands.add("-S");
				commands.add(resOverlayDir.getAbsolutePath());
			}
		}
		if (combinedRes.exists()) {
			commands.add("-S");
			commands.add(combinedRes.getAbsolutePath());
		} else {
			if (resourceDirectory.exists()) {
				commands.add("-S");
				commands.add(resourceDirectory.getAbsolutePath());
			}
		}
		for (Artifact artifact : getAllRelevantDependencyArtifacts()) {
			if (artifact.getType().equals(APKLIB)) {
				final String apkLibResDir = getLibraryUnpackDirectory(artifact) + "/res";
				if (new File(apkLibResDir).exists()) {
					commands.add("-S");
					commands.add(apkLibResDir);
				}
			}
		}
		commands.add("--auto-add-overlay");

		// Use the combined assets.
		// Indeed, aapt does not support several -A arguments.
		if (combinedAssets.exists()) {
			commands.add("-A");
			commands.add(combinedAssets.getAbsolutePath());
		}

		if (StringUtils.isNotBlank(renameManifestPackage)) {
			commands.add("--rename-manifest-package");
			commands.add(renameManifestPackage);
		}

		if (StringUtils.isNotBlank(renameInstrumentationTargetPackage)) {
			commands.add("--rename-instrumentation-target-package");
			commands.add(renameInstrumentationTargetPackage);
		}

		commands.add("-I");
		commands.add(androidJar.getAbsolutePath());
		commands.add("-F");
		commands.add(outputFile.getAbsolutePath());
		if (StringUtils.isNotBlank(configurations)) {
			commands.add("-c");
			commands.add(configurations);
		}

		for (String aaptExtraArg : aaptExtraArgs) {
			commands.add(aaptExtraArg);
		}

		log.info(getAndroidSdk().getPathForTool("aapt") + " " + commands.toString());
		try {
			executor.executeCommand(getAndroidSdk().getPathForTool("aapt"), commands, project.getBasedir(), false);
		} catch (ExecutionException e) {
			throw new MojoExecutionException("", e);
		}
	}

	protected AndroidSigner getAndroidSigner() {
		if (sign == null) {
			return new AndroidSigner(signDebug);
		}
		return new AndroidSigner(sign.getDebug());
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		
	}
	
	private Set<Artifact> getAllRelevantDependencyArtifacts() {
		final Set<Artifact> allArtifacts = project.getArtifacts();
		final Set<Artifact> results = filterOutIrrelevantArtifacts(allArtifacts);
		return results;
	}
	
	private Set<Artifact> getRelevantCompileArtifacts() {
		final List<Artifact> allArtifacts = project.getCompileArtifacts();
		final Set<Artifact> results = filterOutIrrelevantArtifacts(allArtifacts);
		return results;
	}
	
	private Set<Artifact> filterOutIrrelevantArtifacts(
			Iterable<Artifact> allArtifacts) {
		final Set<Artifact> results = new LinkedHashSet<Artifact>();
		for (Artifact artifact : allArtifacts) {
			if (artifact == null) {
				continue;
			}

			if (EXCLUDED_DEPENDENCY_SCOPES.contains(artifact.getScope())) {
				continue;
			}

			if ("apk".equalsIgnoreCase(artifact.getType())) {
				continue;
			}

			results.add(artifact);
		}
		return results;
	}
	
	private AndroidSdk getAndroidSdk() throws MojoExecutionException {
		File chosenSdkPath;
		String chosenSdkPlatform;

		if (sdk != null) {
			// An <sdk> tag exists in the pom.

			if (sdk.getPath() != null) {
				// An <sdk><path> tag is set in the pom.

				chosenSdkPath = sdk.getPath();
			} else {
				// There is no <sdk><path> tag in the pom.

				if (sdkPath != null) {
					// -Dandroid.sdk.path is set on command line, or via
					// <properties><android.sdk.path>...
					chosenSdkPath = sdkPath;
				} else {
					// No -Dandroid.sdk.path is set on command line, or via
					// <properties><android.sdk.path>...
					chosenSdkPath = new File(getAndroidHomeOrThrow());
				}
			}

			// Use <sdk><platform> from pom if it's there, otherwise try
			// -Dandroid.sdk.platform from command line or
			// <properties><sdk.platform>...
			if (!isBlank(sdk.getPlatform())) {
				chosenSdkPlatform = sdk.getPlatform();
			} else {
				chosenSdkPlatform = sdkPlatform;
			}
		} else {
			// There is no <sdk> tag in the pom.

			if (sdkPath != null) {
				// -Dandroid.sdk.path is set on command line, or via
				// <properties><android.sdk.path>...
				chosenSdkPath = sdkPath;
			} else {
				// No -Dandroid.sdk.path is set on command line, or via
				// <properties><android.sdk.path>...
				chosenSdkPath = new File(getAndroidHomeOrThrow());
			}

			// Use any -Dandroid.sdk.platform from command line or
			// <properties><sdk.platform>...
			chosenSdkPlatform = sdkPlatform;
		}

		return new AndroidSdk(chosenSdkPath, chosenSdkPlatform);
	}
	
	private String getAndroidHomeOrThrow() throws MojoExecutionException {
		final String androidHome = System.getenv(ENV_ANDROID_HOME);
		if (isBlank(androidHome)) {
			throw new MojoExecutionException(
					"No Android SDK path could be found. You may configure it in the "
							+ "plugin configuration section in the pom file using <sdk><path>...</path></sdk> or "
							+ "<properties><android.sdk.path>...</android.sdk.path></properties> or on command-line "
							+ "using -Dandroid.sdk.path=... or by setting environment variable "
							+ ENV_ANDROID_HOME);
		}
		return androidHome;
	}
	
	private String getLibraryUnpackDirectory(Artifact apkLibraryArtifact) {
		return AbstractAndroidMojo.getLibraryUnpackDirectory(
				unpackedApkLibsDirectory, apkLibraryArtifact);
	}

	public static String getLibraryUnpackDirectory(
			File unpackedApkLibsDirectory, Artifact apkLibraryArtifact) {
		return unpackedApkLibsDirectory.getAbsolutePath() + File.separator
				+ apkLibraryArtifact.getId().replace(":", "_");
	}
}
