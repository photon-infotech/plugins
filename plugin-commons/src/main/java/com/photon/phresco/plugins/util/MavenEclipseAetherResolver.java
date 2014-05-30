package com.photon.phresco.plugins.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

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
        List<URL> urls = new ArrayList<URL>();
        CollectRequest collectRequest = new CollectRequest();
        Artifact artifact = artifacts.get(0);
		org.eclipse.aether.graph.Dependency dependency = new org.eclipse.aether.graph.Dependency(artifact, "compile");
		collectRequest.setRoot( dependency  );
        collectRequest.addRepository( repo );
        
        org.eclipse.aether.resolution.ArtifactRequest req = new org.eclipse.aether.resolution.ArtifactRequest();
        req.addRepository(repo);
        req.setArtifact(artifact);
        try {
        	org.eclipse.aether.resolution.ArtifactResult resolveArtifact = repoSystem.resolveArtifact(repoSession, req );
    		urls.add( resolveArtifact.getArtifact().getFile().toURI().toURL());
        } catch(Exception e) {
        	throw new PhrescoException(e);
        }
        URL[] urlArr = new URL[0];
        return urls.toArray(urlArr);
	}
	
}
