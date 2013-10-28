package com.photon.phresco.plugins;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.repository.RemoteRepository;

import com.photon.phresco.exception.PhrescoException;

public class Configurer extends AbstractMavenLifecycleParticipant {

	@Override
	public void afterProjectsRead(MavenSession session)
			throws MavenExecutionException {
		List<MavenProject> projects = session.getProjects();
		for (MavenProject mavenProject : projects) {
			if(mavenProject.getFile().getName().equals("phresco-pom.xml")) {
				MavenProject sourceProject;
				try {
					sourceProject = getSourceProject(mavenProject);
					Properties properties = sourceProject.getProperties();
					Set<Object> keySet = properties.keySet();
					//Made properties available in phresco-pom.xml
					if(CollectionUtils.isNotEmpty(keySet)) {
						for (Object object : keySet) {
							mavenProject.getProperties().put(object, properties.get(object));
						}
					}
					//Made plugins available in phresco-pom.xml
					List<Plugin> buildPlugins = sourceProject.getBuildPlugins();
					if(CollectionUtils.isNotEmpty(buildPlugins)) {
						for (Plugin plugin : buildPlugins) {
							mavenProject.getBuildPlugins().add(plugin);
						}
					}
					//Made profiles available in phresco-pom.xml
					List<Profile> activeProfiles = sourceProject.getActiveProfiles();
					if(CollectionUtils.isNotEmpty(activeProfiles)) {
						for (Profile profile : activeProfiles) {
							mavenProject.getActiveProfiles().add(profile);
						}
					}
					//Made plugin repos available in phresco-pom.xml
					List<RemoteRepository> remotePluginRepositories = sourceProject.getRemotePluginRepositories();
					if(CollectionUtils.isNotEmpty(remotePluginRepositories)) {
						for (RemoteRepository remoteRepository : remotePluginRepositories) {
							mavenProject.getRemotePluginRepositories().add(remoteRepository);
						}
					}
					//Made project repos available in phresco-pom.xml
					List<RemoteRepository> remoteProjectRepositories = sourceProject.getRemoteProjectRepositories();
					if(CollectionUtils.isNotEmpty(remoteProjectRepositories)) {
						for (RemoteRepository remoteRepository : remoteProjectRepositories) {
							mavenProject.getRemoteProjectRepositories().add(remoteRepository);
						}
					}
					List<String> modules = sourceProject.getModules();
					if(CollectionUtils.isNotEmpty(modules)) {
						String string = modules.toString();
						String modulesString = string.substring(1, string.length()-1);
						mavenProject.getProperties().put("modules", modulesString);
					}
					mavenProject.setFile(mavenProject.getFile());
					removePlugins(mavenProject, sourceProject.getPackaging());
					mavenProject.addProjectReference(sourceProject);
					mavenProject.setDependencies(sourceProject.getDependencies());
					mavenProject.setPackaging(sourceProject.getPackaging());
				} catch (PhrescoException e) {
					throw new MavenExecutionException(e.getMessage(), e);
				}
			}
		}
		super.afterProjectsRead(session);
	}
	
	 private MavenProject getSourceProject(MavenProject phrescoProject) throws PhrescoException {
    	Model model = null;
    	FileReader reader = null;
    	MavenXpp3Reader mavenreader = new MavenXpp3Reader();
    	try {
    		File pomFile = new File(phrescoProject.getBasedir(), phrescoProject.getProperties().getProperty("source.pom"));
    	    reader = new FileReader(pomFile);
    	    model = mavenreader.read(reader);
    	    model.setPomFile(pomFile);
    	}catch(Exception ex){
    		throw new PhrescoException(ex);
    	}
    	MavenProject project = new MavenProject(model);
    	return project;
	 }
	 
	 private void removePlugins(MavenProject project, String packagingType) {
		 Plugin jarPlugin = new Plugin();
		 jarPlugin.setGroupId("org.apache.maven.plugins");
		 jarPlugin.setArtifactId("maven-jar-plugin");
		 Plugin warPlugin = new Plugin();
		 warPlugin.setGroupId("org.apache.maven.plugins");
		 warPlugin.setArtifactId("maven-war-plugin");
		 if(packagingType.equals("war")) {
			 project.getBuildPlugins().remove(jarPlugin);
		 } else if(packagingType.equals("jar")) {
			 project.getBuildPlugins().remove(warPlugin);
		 } else if(packagingType.equals("pom")) {
			 List<Plugin> plugins = new ArrayList<Plugin>();
			 plugins.add(jarPlugin);
			 plugins.add(warPlugin);
			 project.getBuildPlugins().removeAll(plugins);
		 } 
	 }
}
