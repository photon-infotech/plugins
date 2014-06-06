package com.photon.phresco.plugins.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.filter.DependencyFilterUtils;

import com.photon.phresco.exception.PhrescoException;

public class MavenSonatypeAetherResolver {
	
	public static URL[] resolve(org.sonatype.aether.repository.RemoteRepository remoteRepo, List<org.sonatype.aether.artifact.Artifact> artifacts,
			org.sonatype.aether.RepositorySystem system, org.sonatype.aether.RepositorySystemSession session) throws PhrescoException {
		List<URL> urls = new ArrayList<URL>();
		DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
		for (Artifact artifact : artifacts) {
			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE ) );
			collectRequest.addRepository( remoteRepo );
			DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, classpathFlter );
			List<ArtifactResult> artifactResults;
			try {
				artifactResults = system.resolveDependencies( session, dependencyRequest ).getArtifactResults();
				for (ArtifactResult artifactResult : artifactResults ) {
					urls.add(artifactResult.getArtifact().getFile().toURI().toURL());
				}
			} catch (DependencyResolutionException e) {
				throw new PhrescoException(e);
			} catch (MalformedURLException e) {
				throw new PhrescoException(e);
			}
		}
        URL[] urlArr = new URL[0];
        return urls.toArray(urlArr);
	}

	public static RepositorySystemSession getRepoSession(MavenSession mavenSession) {
		RepositorySystemSession session = mavenSession.getRepositorySession();
        return session;
	}
	
}
