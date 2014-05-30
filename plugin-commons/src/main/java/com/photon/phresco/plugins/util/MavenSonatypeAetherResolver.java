package com.photon.phresco.plugins.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.sonatype.aether.RepositorySystemSession;

import com.photon.phresco.exception.PhrescoException;

public class MavenSonatypeAetherResolver {
	
	public static URL[] resolve(org.sonatype.aether.repository.RemoteRepository remoteRepo, List<org.sonatype.aether.artifact.Artifact> artifacts,
			org.sonatype.aether.RepositorySystem system, org.sonatype.aether.RepositorySystemSession session) throws PhrescoException {
		List<URL> urls = new ArrayList<URL>();
        org.sonatype.aether.collection.CollectRequest collectRequest = new org.sonatype.aether.collection.CollectRequest();
        org.sonatype.aether.artifact.Artifact artifact = artifacts.get(0);
        org.sonatype.aether.graph.Dependency dependency = new org.sonatype.aether.graph.Dependency(artifact, "compile");
		collectRequest.setRoot( dependency  );
        collectRequest.addRepository( remoteRepo );
        
		org.sonatype.aether.resolution.ArtifactRequest req = new org.sonatype.aether.resolution.ArtifactRequest();
        req.addRepository(remoteRepo);
        req.setArtifact(artifact);
        try {
			org.sonatype.aether.resolution.ArtifactResult resolveArtifact = system.resolveArtifact(session, req);
        	urls.add( resolveArtifact.getArtifact().getFile().toURI().toURL());
        } catch(Exception e) {
        	throw new PhrescoException(e);
        }
        URL[] urlArr = new URL[0];
        return urls.toArray(urlArr);
	}

	public static RepositorySystemSession getRepoSession(MavenSession mavenSession) {
		RepositorySystemSession session = mavenSession.getRepositorySession();
        return session;
	}
	
}
