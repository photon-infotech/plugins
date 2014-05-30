/**
 * Phresco Plugin Commons
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
package com.photon.phresco.plugins.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.util.Utility;

public class MavenPluginArtifactResolver {
	
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
        	e.printStackTrace();
        }
        URL[] urlArr = new URL[0];
        return urls.toArray(urlArr);
	}

	
	public static URL[] resolveUsingTemp(String repoURL, List<Artifact> artifacts) throws PhrescoException {
		URL[] urls = new URL[10];
		for (Artifact artifact : artifacts) {
			InputStream in = null;
			FileOutputStream fos = null;
			try {
				URL url = new URL(repoURL + artifact.getGroupId().replace(".", "/") + "/" + artifact.getArtifactId() + "/" + artifact.getVersion() + "/" + artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar");
				URLConnection connection = url.openConnection();
				in = connection.getInputStream();
				File filePath = new File(Utility.getPhrescoTemp() + artifact.getArtifactId() + ".jar");
				filePath.createNewFile();
				int i = 0;
				urls[i++] = filePath.toURI().toURL();
				fos = new FileOutputStream(filePath);
				byte[] buf = new byte[1024];
				while (true) {
					int len;
					len = in.read(buf);
					if (len == -1) {
						break;
					}
					fos.write(buf, 0, len);
				}
			} catch (Exception e) {
				throw new PhrescoException(e);
			} finally {
				Utility.closeStream(in);
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						throw new PhrescoException(e);
					}
				}
			}
		}
		return urls;
	}


	public static URL[] resolveAether(org.sonatype.aether.repository.RemoteRepository remoteRepo, List<org.sonatype.aether.artifact.Artifact> artifacts,
			org.sonatype.aether.RepositorySystem system, org.sonatype.aether.RepositorySystemSession session) {		
		List<URL> urls = new ArrayList<URL>();
        org.sonatype.aether.collection.CollectRequest collectRequest = new org.sonatype.aether.collection.CollectRequest();
        org.sonatype.aether.artifact.Artifact artifact = artifacts.get(0);
        org.sonatype.aether.graph.Dependency dependency = new org.sonatype.aether.graph.Dependency(artifact, "compile");
		collectRequest.setRoot( dependency  );
//        collectRequest.addRepository( remoteRepo );
        
		org.sonatype.aether.resolution.ArtifactRequest req = new org.sonatype.aether.resolution.ArtifactRequest();
//        req.addRepository(remoteRepo);
        req.setArtifact(artifact);
        try {
			org.sonatype.aether.resolution.ArtifactResult resolveArtifact = system.resolveArtifact(session, req);
			System.out.println("URL FOUND IS    " + resolveArtifact.getArtifact().getFile().toURI().toURL());
        	urls.add( resolveArtifact.getArtifact().getFile().toURI().toURL());
        } catch(Exception e) {
        	e.printStackTrace();
        }
        URL[] urlArr = new URL[0];
        return urls.toArray(urlArr);
	}
	
}
