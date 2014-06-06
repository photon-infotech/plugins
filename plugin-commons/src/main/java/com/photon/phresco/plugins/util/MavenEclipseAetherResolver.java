package com.photon.phresco.plugins.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.util.Utility;

public class MavenEclipseAetherResolver {
	
	private static final Logger S_LOGGER= Logger.getLogger(MavenPluginArtifactResolver.class);
	private static Boolean isDebugEnabled = S_LOGGER.isDebugEnabled();
	
	public static final URL[] resolve(Object remoteRepo , List<Artifact> artifacts, 
			org.eclipse.aether.RepositorySystem repoSystem, org.eclipse.aether.RepositorySystemSession repoSession) throws Exception {
		if (isDebugEnabled) {
	        S_LOGGER.debug("Entered into MavenArtifactResolver.resolve()");
	    }
		LocalRepository localRepo = new LocalRepository(Utility.getLocalRepoPath());
		if (isDebugEnabled) {
	        S_LOGGER.debug("Local Repository Is" + Utility.getLocalRepoPath());
	        S_LOGGER.debug("Local Repository Is" + localRepo);
	    }
		RemoteRepository repo =  (RemoteRepository) remoteRepo;
		DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
        List<URL> urls = new ArrayList<URL>();
        for (Artifact artifact : artifacts) {
	        CollectRequest collectRequest = new CollectRequest();
	        collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE ) );
	        collectRequest.addRepository( repo );
	        DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, classpathFlter );
	        List<ArtifactResult> artifactResults =
	        repoSystem.resolveDependencies( repoSession, dependencyRequest ).getArtifactResults();
	        for (ArtifactResult artifactResult : artifactResults ) {
	        	urls.add(artifactResult.getArtifact().getFile().toURI().toURL());
	        }
        }
        URL[] urlArr = new URL[0];
        return urls.toArray(urlArr);
	}
	
}
