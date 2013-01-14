package com.photon.phresco.plugin.commons;

import java.io.File;

import org.apache.maven.project.MavenProject;

public class MavenProjectInfo {
    
    private File baseDir;
    private MavenProject project;
    private String projectCode;
    
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
    
}
