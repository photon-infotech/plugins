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
package com.photon.phresco.plugin.commons;

import java.io.File;
import java.util.Map;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;

public class MavenProjectInfo {
    
    private File baseDir;
    private MavenProject project;
    private String projectCode;
    private String moduleName;
    private MavenSession mavenSession;
    private BuildPluginManager pluginManager;
    private ArtifactRepository localRepository;
    private Map<String, Object> keyValues;
    
	public Map<String, Object> getKeyValues() {
		return keyValues;
	}

	public void setKeyValues(Map<String, Object> keyValues) {
		this.keyValues = keyValues;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public File getBaseDir() {
        return baseDir;
    }
    
    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }
    
    public MavenProject getProject() {
        return project;
    }
    
    public void setProject(MavenProject project) {
        this.project = project;
    }
    
    public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public void setMavenSession(MavenSession mavenSession) {
		this.mavenSession = mavenSession;
	}

	public MavenSession getMavenSession() {
		return mavenSession;
	}

	public void setPluginManager(BuildPluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	public BuildPluginManager getPluginManager() {
		return pluginManager;
	}

	public void setLocalRepository(ArtifactRepository localRepository) {
		this.localRepository = localRepository;
	}

	public ArtifactRepository getLocalRepository() {
		return localRepository;
	}
    
}
