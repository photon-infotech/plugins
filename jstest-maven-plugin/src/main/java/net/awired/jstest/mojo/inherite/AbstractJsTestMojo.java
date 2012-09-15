package net.awired.jstest.mojo.inherite;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.awired.jstest.common.StringStacktrace;
import net.awired.jstest.resource.ResourceDirectory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;

public abstract class AbstractJsTestMojo extends JsTestConfiguration {

    protected abstract void run() throws MojoExecutionException, MojoFailureException;

    private StringStacktrace stringStacktrace = new StringStacktrace();

    /**
     * @component role="org.codehaus.plexus.archiver.manager.ArchiverManager"
     * @required
     */
    protected ArchiverManager archiverManager;

    public final void execute() throws MojoExecutionException, MojoFailureException {
        try {
            run();
        } catch (MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("The jstest-maven-plugin encountered an exception: \n"
                    + stringStacktrace.stringify(e), e);
        }
    }

    public List<ResourceDirectory> buildOverlaysResourceDirectories() {
        List<ResourceDirectory> overlays = new ArrayList<ResourceDirectory>();
        File overlayDirectory = getOverlayDirectory();
        File[] groupIdDirs = overlayDirectory.listFiles();
        
        System.out.println("************************* buildOverlaysResourceDirectories ************************ " + getMavenProject().getArtifacts());
        System.out.println("project name === " + getMavenProject().getArtifactId());
        System.out.println("project de === " + getMavenProject().getDependencyArtifacts());
        File[] allDirs = null;
        if (groupIdDirs != null) {
            for (File groupIdDir : groupIdDirs) {
                File[] artifactIdDirs = groupIdDir.listFiles();
                allDirs = concat(allDirs, artifactIdDirs);
                /*for (File resource : artifactIdDirs) {
                	System.out.println("resource === " + resource);
                    overlays.add(new ResourceDirectory(resource, ResourceDirectory.DEFAULT_INCLUDES,
                            ResourceDirectory.DEFAULT_EXCLUDES));
                }*/
            }
            overlays.addAll(arrangeAsDependency(allDirs));
        }
        System.out.println("************************* buildOverlaysResourceDirectories ************************" + overlays);
        return overlays;
    }
    
    File[] concat(File[] first, File[] second) {
    	if (first == null) {
    		first = new File[0];
    	}
    	File[] all = new File[first.length + second.length];
    	System.arraycopy(first, 0, all, 0, first.length);
    	System.arraycopy(second, 0, all, first.length, second.length);
    	return all;
    }


    
    private List<ResourceDirectory> arrangeAsDependency(File[] artifactIdDirs) {
    	Set dependencyArtifacts = getMavenProject().getDependencyArtifacts();
    	final Iterator it = dependencyArtifacts.iterator();

        final List<ResourceDirectory> result = new ArrayList<ResourceDirectory>();
        while (it.hasNext()) {
            Artifact artifact = (Artifact) it.next();
            ResourceDirectory resourceDirectory = getDependencyAsResource(artifact, artifactIdDirs);
            System.out.println("######### resourceDirectory " + resourceDirectory);
            if (resourceDirectory != null) {
            	System.out.println("!!!!!!!! added " +  resourceDirectory.getDirectory());
            	result.add(resourceDirectory);
            }
        }
        
		return result;
    }
    
    private ResourceDirectory getDependencyAsResource(Artifact artifact, File[] artifactIdDirs) {
    	String fileName = artifact.getArtifactId() ; //+ "-" + artifact.getVersion() + ".js";
    	for (File resource : artifactIdDirs) {
    		System.out.println("rsrc = " + resource.getName() + " fileName = " + fileName);
    		if (resource.getName().equals(fileName)) {
    			System.out.println("resource ... " + resource);
    			return new ResourceDirectory(resource, ResourceDirectory.DEFAULT_INCLUDES,
                        ResourceDirectory.DEFAULT_EXCLUDES);
    		}
    	}
		return null;
    }
}
