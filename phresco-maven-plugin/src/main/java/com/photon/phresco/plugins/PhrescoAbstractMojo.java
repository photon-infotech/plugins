/**
 * Phresco Maven Plugin
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
package com.photon.phresco.plugins;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.photon.phresco.commons.model.ArtifactGroup;
import com.photon.phresco.commons.model.ArtifactInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.api.PhrescoPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Implementation.Dependency;
import com.photon.phresco.plugins.util.MavenPluginArtifactResolver;
import com.photon.phresco.plugins.util.MojoProcessor;

public abstract class PhrescoAbstractMojo extends AbstractMojo {
	
	/**
     * The entry point to Aether, i.e. the component doing all the work.
     * 
     * @component
     */
    private RepositorySystem repoSystem;
    
    /**
     * The current repository/network configuration of Maven.
     * 
     * @parameter default-value="${repositorySystemSession}"
     * @readonly
     */
    private RepositorySystemSession repoSession;
    
    /**
     * The project's remote repositories to use for the resolution of project dependencies.
     * 
     * @parameter default-value="${project.remoteProjectRepositories}"
     * @readonly
     */
    private List<RemoteRepository> projectRepos;
    
    public PhrescoPlugin getPlugin(Dependency dependency) throws PhrescoException {
        //Caching not needed since it will be triggered as a new process every time from the maven
    	return constructClass(dependency);
    }

    private PhrescoPlugin constructClass(Dependency dependency) throws PhrescoException {
    	Log log = getLog();
		try {
			Class<PhrescoPlugin> apiClass = (Class<PhrescoPlugin>) Class
					.forName(dependency.getClazz(), true, getURLClassLoader(dependency));
			Constructor<PhrescoPlugin> constructor = apiClass.getDeclaredConstructor(Log.class);
            return constructor.newInstance(log);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PhrescoException(e);
		}
    }
    
    private URLClassLoader getURLClassLoader(Dependency dependency) throws PhrescoException {
		List<Artifact> artifacts = new ArrayList<Artifact>();
		
    	List<ArtifactGroup> plugins  = new ArrayList<ArtifactGroup>();
    	ArtifactGroup artifactGroup = new ArtifactGroup();
    	artifactGroup.setGroupId(dependency.getGroupId());
    	artifactGroup.setArtifactId(dependency.getArtifactId());
    	artifactGroup.setPackaging(dependency.getType());
    	ArtifactInfo info = new ArtifactInfo();
    	info.setVersion(dependency.getVersion());
    	artifactGroup.setVersions(Collections.singletonList(info));
    	plugins.add(artifactGroup);
    	
		for (ArtifactGroup plugin : plugins) {
			List<ArtifactInfo> versions = plugin.getVersions();
			for (ArtifactInfo artifactInfo : versions) {
				Artifact artifact = new DefaultArtifact(plugin.getGroupId(), plugin.getArtifactId(), PluginConstants.PACKAGING_TYPE_JAR, artifactInfo.getVersion());
				artifacts.add(artifact);
			}
		}
		
		URL[] artifactURLs;
		try {
			artifactURLs = MavenPluginArtifactResolver.resolve(projectRepos.get(0), artifacts, repoSystem, repoSession);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
		if (clsLoader == null) {
		    clsLoader = this.getClass().getClassLoader();
		}
		URLClassLoader classLoader = new URLClassLoader(artifactURLs, clsLoader);
		return classLoader;
	}
    
    protected Configuration getConfiguration(String infoFile, String goal) throws PhrescoException {
    	if (new File(infoFile).exists()) {
    		MojoProcessor processor = new MojoProcessor(new File(infoFile));
            return processor.getConfiguration(goal);
    	} else {
    		return null;
    	}
    }
    
    protected MavenProjectInfo getMavenProjectInfo(MavenProject project) {
        MavenProjectInfo mavenProjectInfo = new MavenProjectInfo();
    	mavenProjectInfo.setBaseDir(project.getBasedir());
        mavenProjectInfo.setProject(project);
        mavenProjectInfo.setProjectCode(project.getBasedir().getName());
        return mavenProjectInfo;
    }
    
    protected MavenProjectInfo getMavenProjectInfo(MavenProject project, String subModule) {
        MavenProjectInfo mavenProjectInfo = new MavenProjectInfo();
    	mavenProjectInfo.setBaseDir(project.getBasedir());
        mavenProjectInfo.setProject(project);
        mavenProjectInfo.setProjectCode(project.getBasedir().getName());
        mavenProjectInfo.setModuleName(subModule);
        return mavenProjectInfo;
    }
    
    protected Dependency getDependency(String infoFile, String goal) throws PhrescoException {
    	MojoProcessor processor = new MojoProcessor(new File(infoFile));
    	if (processor.getImplementationDependency(goal) != null) {
    		return processor.getImplementationDependency(goal).getDependency();
		}
		return null;
    }
    
    protected boolean isGoalAvailable(String infoFile, String goal) throws PhrescoException {
    	MojoProcessor processor = new MojoProcessor(new File(infoFile));
    	return processor.isGoalAvailable(goal);
	}
}
