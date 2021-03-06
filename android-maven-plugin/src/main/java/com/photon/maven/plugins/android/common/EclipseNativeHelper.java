/**
 * Android Maven Plugin - android-maven-plugin
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
package com.photon.maven.plugins.android.common;

import com.photon.maven.plugins.android.AbstractAndroidMojo;
import com.photon.maven.plugins.android.AndroidNdk;
import com.photon.maven.plugins.android.phase09package.ApklibMojo;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.ArtifactProperties;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.artifact.DefaultArtifactType;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.util.filter.AndDependencyFilter;
import org.eclipse.aether.util.filter.ExclusionsDependencyFilter;
import org.eclipse.aether.util.filter.ScopeDependencyFilter;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.photon.maven.plugins.android.common.AndroidExtension.APKLIB;
import static org.apache.maven.RepositoryUtils.toDependency;

public class EclipseNativeHelper {

	public static final int NDK_REQUIRED_VERSION = 7;

	private MavenProject project;
	private RepositorySystemSession repoSession;
	private RepositorySystem repoSystem;
	private ArtifactFactory artifactFactory;
	private Log log;
	private List<RemoteRepository> projectRepos;

	public EclipseNativeHelper(MavenProject project,
			Object projectRepos,
			RepositorySystemSession repoSession, RepositorySystem repoSystem,
			ArtifactFactory artifactFactory, Log log) {
		this.project = project;
		this.projectRepos = (List<RemoteRepository>) projectRepos;
		this.repoSession = repoSession;
		this.repoSystem = repoSystem;
		this.artifactFactory = artifactFactory;
		this.log = log;
	}

	public static boolean hasStaticNativeLibraryArtifact(
			Set<Artifact> resolveNativeLibraryArtifacts, File unpackDirectory,
			String ndkArchitecture) {
		for (Artifact resolveNativeLibraryArtifact : resolveNativeLibraryArtifacts) {
			if ("a".equals(resolveNativeLibraryArtifact.getType())) {
				return true;
			}
			if (APKLIB.equals(resolveNativeLibraryArtifact.getType())) {
				File[] aFiles = listNativeFiles(resolveNativeLibraryArtifact,
						unpackDirectory, ndkArchitecture, true);
				if (aFiles != null && aFiles.length > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasSharedNativeLibraryArtifact(
			Set<Artifact> resolveNativeLibraryArtifacts, File unpackDirectory,
			String ndkArchitecture) {
		for (Artifact resolveNativeLibraryArtifact : resolveNativeLibraryArtifacts) {
			if ("so".equals(resolveNativeLibraryArtifact.getType())) {
				return true;
			}
			if (APKLIB.equals(resolveNativeLibraryArtifact.getType())) {
				File[] soFiles = listNativeFiles(resolveNativeLibraryArtifact,
						unpackDirectory, ndkArchitecture, false);
				if (soFiles != null && soFiles.length > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static File[] listNativeFiles(Artifact a, File unpackDirectory,
			final String ndkArchitecture, final boolean staticLibrary) {
		File libsFolder = new File(
				AbstractAndroidMojo.getLibraryUnpackDirectory(unpackDirectory,
						a), File.separator + ApklibMojo.NATIVE_LIBRARIES_FOLDER
						+ File.separator + ndkArchitecture);
		if (libsFolder.exists()) {
			File[] libFiles = libsFolder.listFiles(new FilenameFilter() {
				public boolean accept(final File dir, final String name) {
					return name.startsWith("lib")
							&& name.endsWith((staticLibrary ? ".a" : ".so"));
				}
			});
			return libFiles;
		}
		return null;
	}

	public Set<Artifact> getNativeDependenciesArtifacts(File unpackDirectory,
			boolean sharedLibraries) throws MojoExecutionException {
		final Set<Artifact> filteredArtifacts = new LinkedHashSet<Artifact>();
		final Set<Artifact> allArtifacts = new LinkedHashSet<Artifact>();

		// Add all dependent artifacts declared in the pom file
		// Note: The result of project.getDependencyArtifacts() can be an
		// UnmodifiableSet so we
		// have created our own above and add to that.
		allArtifacts.addAll(project.getDependencyArtifacts());

		// Add all attached artifacts as well - this could come from the NDK
		// mojo for example
		allArtifacts.addAll(project.getAttachedArtifacts());

		for (Artifact artifact : allArtifacts) {
			// A null value in the scope indicates that the artifact has been
			// attached
			// as part of a previous build step (NDK mojo)
			if (isNativeLibrary(sharedLibraries, artifact.getType())
					&& artifact.getScope() == null) {
				// Including attached artifact
				log.debug("Including attached artifact: "
						+ artifact.getArtifactId() + "("
						+ artifact.getGroupId()
						+ "). Artifact scope is not set.");
				filteredArtifacts.add(artifact);
			} else {
				if (isNativeLibrary(sharedLibraries, artifact.getType())
						&& (Artifact.SCOPE_COMPILE.equals(artifact.getScope()) || Artifact.SCOPE_RUNTIME
								.equals(artifact.getScope()))) {
					log.debug("Including attached artifact: "
							+ artifact.getArtifactId() + "("
							+ artifact.getGroupId()
							+ "). Artifact scope is Compile or Runtime.");
					filteredArtifacts.add(artifact);
				} else {
					if (APKLIB.equals(artifact.getType())) {
						// Check if the artifact contains a libs folder - if so,
						// include it in the list
						File libsFolder = new File(
								AbstractAndroidMojo.getLibraryUnpackDirectory(
										unpackDirectory, artifact) + "/libs");
						if (libsFolder.exists()) {
							log.debug("Including attached artifact: "
									+ artifact.getArtifactId() + "("
									+ artifact.getGroupId()
									+ "). Artifact is APKLIB.");
							filteredArtifacts.add(artifact);
						}
					}
				}
			}
		}

		Set<Artifact> transientArtifacts = processTransientDependencies(
				project.getDependencies(), sharedLibraries);

		filteredArtifacts.addAll(transientArtifacts);

		return filteredArtifacts;
	}

	private boolean isNativeLibrary(boolean sharedLibraries, String artifactType) {
		return (sharedLibraries ? "so".equals(artifactType) : "a"
				.equals(artifactType));
	}

	private Set<Artifact> processTransientDependencies(
			List<org.apache.maven.model.Dependency> dependencies,
			boolean sharedLibraries) throws MojoExecutionException {

		Set<Artifact> transientArtifacts = new LinkedHashSet<Artifact>();
		for (org.apache.maven.model.Dependency dependency : dependencies) {
			if (!"provided".equals(dependency.getScope())
					&& !dependency.isOptional()) {
				transientArtifacts.addAll(processTransientDependencies(
						toDependency(dependency,
								repoSession.getArtifactTypeRegistry()),
						sharedLibraries));
			}
		}

		return transientArtifacts;

	}

	public static Dependency toDependency(
			org.apache.maven.model.Dependency dependency,
			ArtifactTypeRegistry stereotypes) {
		ArtifactType stereotype = stereotypes.get(dependency.getType());
		if (stereotype == null) {
			stereotype = new DefaultArtifactType(dependency.getType());
		}

		boolean system = dependency.getSystemPath() != null
				&& dependency.getSystemPath().length() > 0;

		Map<String, String> props = null;
		if (system) {
			props = Collections.singletonMap(ArtifactProperties.LOCAL_PATH,
					dependency.getSystemPath());
		}

		org.eclipse.aether.artifact.Artifact artifact = new DefaultArtifact(
				dependency.getGroupId(), dependency.getArtifactId(),
				dependency.getClassifier(), null, dependency.getVersion(),
				props, stereotype);

		List<Exclusion> exclusions = new ArrayList<Exclusion>(dependency
				.getExclusions().size());
		for (org.apache.maven.model.Exclusion exclusion : dependency
				.getExclusions()) {
			exclusions.add(toExclusion(exclusion));
		}

		Dependency result = new Dependency(artifact, dependency.getScope(),
				dependency.isOptional(), exclusions);

		return result;
	}

	private static Exclusion toExclusion(
			org.apache.maven.model.Exclusion exclusion) {
		return new Exclusion(exclusion.getGroupId(), exclusion.getArtifactId(),
				"*", "*");
	}

	private Set<Artifact> processTransientDependencies(Dependency dependency,
			boolean sharedLibraries) throws MojoExecutionException {
		try {
			final Set<Artifact> artifacts = new LinkedHashSet<Artifact>();

			final CollectRequest collectRequest = new CollectRequest();

			collectRequest.setRoot(dependency);
			collectRequest.setRepositories(projectRepos);
			final DependencyNode node = repoSystem.collectDependencies(
					repoSession, collectRequest).getRoot();

			Collection<String> exclusionPatterns = new ArrayList<String>();
			if (dependency.getExclusions() != null
					&& !dependency.getExclusions().isEmpty()) {
				for (Exclusion exclusion : dependency.getExclusions()) {
					exclusionPatterns.add(exclusion.getGroupId() + ":"
							+ exclusion.getArtifactId());
				}
			}

			final DependencyRequest dependencyRequest = new DependencyRequest(
					node, new AndDependencyFilter(
							new ExclusionsDependencyFilter(exclusionPatterns),
							new AndDependencyFilter(new ScopeDependencyFilter(
									Arrays.asList("compile", "runtime"),
									Arrays.asList("test")),
							// Also exclude any optional dependencies
									new DependencyFilter() {
										@Override
										public boolean accept(
												DependencyNode dependencyNode,
												List<DependencyNode> dependencyNodes) {
											return !dependencyNode
													.getDependency()
													.isOptional();
										}
									})));

			repoSystem.resolveDependencies(repoSession, dependencyRequest);

			PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			node.accept(nlg);

			final List<Dependency> dependencies = nlg.getDependencies(false);

			for (Dependency dep : dependencies) {
				final org.eclipse.aether.artifact.Artifact depAetherArtifact = dep
						.getArtifact();
				if (isNativeLibrary(sharedLibraries,
						depAetherArtifact.getExtension())) {
					final Artifact mavenArtifact = artifactFactory
							.createDependencyArtifact(depAetherArtifact
									.getGroupId(), depAetherArtifact
									.getArtifactId(), VersionRange
									.createFromVersion(depAetherArtifact
											.getVersion()), depAetherArtifact
									.getExtension(), depAetherArtifact
									.getClassifier(), dep.getScope());
					mavenArtifact.setFile(depAetherArtifact.getFile());
					artifacts.add(mavenArtifact);
				}
			}

			return artifacts;
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Error while processing transient dependencies", e);
		}
	}

	public static void validateNDKVersion(File ndkHomeDir)
			throws MojoExecutionException {
		final File ndkVersionFile = new File(ndkHomeDir, "RELEASE.TXT");

		if (!ndkVersionFile.exists()) {
			throw new MojoExecutionException(
					"Could not locate RELEASE.TXT in the Android NDK base directory '"
							+ ndkHomeDir.getAbsolutePath()
							+ "'.  Please verify your setup! "
							+ AndroidNdk.PROPER_NDK_HOME_DIRECTORY_MESSAGE);
		}

		try {
			String versionStr = FileUtils.readFileToString(ndkVersionFile);
			validateNDKVersion(NDK_REQUIRED_VERSION, versionStr);
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Error while extracting NDK version from '"
							+ ndkVersionFile.getAbsolutePath()
							+ "'. Please verify your setup! "
							+ AndroidNdk.PROPER_NDK_HOME_DIRECTORY_MESSAGE);
		}
	}

	public static void validateNDKVersion(int desiredVersion, String versionStr)
			throws MojoExecutionException {

		int version = 0;

		if (versionStr != null) {
			versionStr = versionStr.trim();
			Pattern pattern = Pattern.compile("[r]([0-9]{1,3})([a-z]{0,1}).*");
			Matcher m = pattern.matcher(versionStr);
			if (m.matches()) {
				final String group = m.group(1);
				version = Integer.parseInt(group);
			}
		}

		if (version < desiredVersion) {
			throw new MojoExecutionException(
					"You are running an old NDK (version " + versionStr
							+ "), please update " + "to at least r'"
							+ desiredVersion + "' or later");
		}
	}

	public static String[] getAppAbi(File applicationMakefile) {
		Scanner scanner = null;
		try {
			if (applicationMakefile != null && applicationMakefile.exists()) {
				scanner = new Scanner(applicationMakefile);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine().trim();
					if (line.startsWith("APP_ABI")) {
						return line.substring(line.indexOf(":=") + 2).trim()
								.split(" ");
					}
				}
			}
		} catch (FileNotFoundException e) {
			// do nothing
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return null;
	}

	public static String[] getNdkArchitectures(final String ndkClassifier,
			final String ndkArchitecture, final String applicationMakefile,
			final File basedir) throws MojoExecutionException {
		// if there is a classifier, return it
		if (ndkClassifier != null) {
			return new String[] { ndkClassifier };
		}

		// if there is a specified ndk architecture, return it
		if (ndkArchitecture != null) {
			return new String[] { ndkArchitecture };
		}

		// if there is no application makefile specified, let's use the default
		// one
		String applicationMakefileToUse = applicationMakefile;
		if (applicationMakefileToUse == null) {
			applicationMakefileToUse = "jni/Application.mk";
		}

		// now let's see if the application file exists
		File appMK = new File(basedir, applicationMakefileToUse);
		if (appMK.exists()) {
			String[] foundNdkArchitectures = getAppAbi(appMK);
			if (foundNdkArchitectures != null) {
				return foundNdkArchitectures;
			}
		}

		// return a default ndk architecture
		return new String[] { "armeabi" };
	}
	
	public static org.apache.maven.artifact.Artifact toArtifact( String groupId, String artifactId )
    {
		org.eclipse.aether.artifact.Artifact artifact = new org.eclipse.aether.artifact.DefaultArtifact( groupId, artifactId, null, null );
        if ( artifact == null )
        {
            return null;
        }

        ArtifactHandler handler = newHandler( artifact );

        /*
         * NOTE: From Artifact.hasClassifier(), an empty string and a null both denote "no classifier". However, some
         * plugins only check for null, so be sure to nullify an empty classifier.
         */
        org.apache.maven.artifact.Artifact result =
            new org.apache.maven.artifact.DefaultArtifact( artifact.getGroupId(), artifact.getArtifactId(),
                                                           artifact.getVersion(), null,
                                                           artifact.getProperty( ArtifactProperties.TYPE,
                                                                                 artifact.getExtension() ),
                                                           nullify( artifact.getClassifier() ), handler );

        result.setFile( artifact.getFile() );
        result.setResolved( artifact.getFile() != null );

        List<String> trail = new ArrayList<String>( 1 );
        trail.add( result.getId() );
        result.setDependencyTrail( trail );

        return result;
    }
	
	public static ArtifactHandler newHandler( org.eclipse.aether.artifact.Artifact artifact )
    {
        String type = artifact.getProperty( ArtifactProperties.TYPE, artifact.getExtension() );
        DefaultArtifactHandler handler = new DefaultArtifactHandler( type );
        handler.setExtension( artifact.getExtension() );
        handler.setLanguage( artifact.getProperty( ArtifactProperties.LANGUAGE, null ) );
        handler.setAddedToClasspath( Boolean.parseBoolean( artifact.getProperty( ArtifactProperties.CONSTITUTES_BUILD_PATH,
                                                                                 "" ) ) );
        handler.setIncludesDependencies( Boolean.parseBoolean( artifact.getProperty( ArtifactProperties.INCLUDES_DEPENDENCIES,
                                                                                     "" ) ) );
        return handler;
    }
	
	private static String nullify( String string )
    {
        return ( string == null || string.length() <= 0 ) ? null : string;
    }
}
