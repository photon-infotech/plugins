/**
 * JsTest Maven Plugin
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
package net.awired.jstest.resource.overlay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.awired.jstest.common.io.DirectoryCopier;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.war.Overlay;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.util.FileUtils;

public class OverlayExtractor {

    private final Log log;
    private ArchiverManager archiverManager;
    private DirectoryCopier directoryCopier = new DirectoryCopier();

    public OverlayExtractor(Log log, ArchiverManager archiverManager) {
        this.log = log;
        this.archiverManager = archiverManager;
    }

    public void extract(File rootDirectory, MavenProject mavenProject) {
        try {
            List<Overlay> overlays = new ArrayList<Overlay>();
            final Overlay currentProjectOverlay = Overlay.createInstance();
            final TestOverlayManager overlayManager = new TestOverlayManager(overlays, mavenProject, "**/**",
                    "META-INF/**", currentProjectOverlay);
            final List<Overlay> resolvedOverlays = overlayManager.getOverlays();
            for (Overlay overlay2 : resolvedOverlays) {
                if (!overlay2.isCurrentProject()) {
                	if ("js".equals(overlay2.getArtifact().getType())) {
                    	File jsOverlayOutput = generateJsOverlayOutput(rootDirectory, overlay2);
                    	jsOverlayOutput.mkdirs();
                    	copyJsOverlay(overlay2.getArtifact().getFile(), jsOverlayOutput);
                    } else {
                    	unpackOverlay(rootDirectory, overlay2);
                    }
                }  
                
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot process overlay projects", e);
        }
    }

    private void unpackOverlay(File rootDirectory, Overlay overlay) throws MojoExecutionException {
        File overlayOutput = generateOverlayOutput(rootDirectory, overlay);
        overlayOutput.mkdirs();
        // TODO: not sure it's good, we should reuse the markers of the dependency plugin
        if (FileUtils.sizeOfDirectory(overlayOutput) == 0
                || overlay.getArtifact().getFile().lastModified() > overlayOutput.lastModified()) {
            File file = overlay.getArtifact().getFile();
            if (!file.isFile()) {
                copyDirectoryArtifact(file, overlayOutput);
            } else {
                unpackArchiveArtifact(file, overlayOutput);
            }
        } else {
            log.debug("Overlay [" + overlay + "] was already unpacked");
        }
    }

	private void copyJsOverlay(File file, File overlayOutput) {
		try {
			FileUtils.copyFileToDirectory(file, overlayOutput);
		} catch (IOException e) {
			 log.warn("cannot copy js overlay, its not found : " + file);
		}
	}
	
	private File generateJsOverlayOutput(File rootDirectory, Overlay overlay) {
		StringBuilder sb = new StringBuilder();
		sb.append(overlay.getGroupId().replaceAll("\\.", "/"));
		sb.append(File.separator);
		sb.append( overlay.getArtifactId());
		sb.append(File.separator);
		sb.append(overlay.getArtifact().getVersion());
        /*String subdir = convertPackageToPath(overlay.getGroupId()) + File.separator + overlay.getArtifactId() + File.separator + overlay.getArtifact().getVersion();
        if (overlay.getClassifier() != null) {
            subdir += "-" + overlay.getClassifier();
        }*/
        return new File(rootDirectory, sb.toString());
    }
	
    private File generateOverlayOutput(File rootDirectory, Overlay overlay) {
        String subdir = overlay.getGroupId() + File.separator + overlay.getArtifactId();
        if (overlay.getClassifier() != null) {
            subdir += "-" + overlay.getClassifier();
        }
        return new File(rootDirectory, subdir);
    }

    private void copyDirectoryArtifact(File dir, File output) {
        // if overlay is in same reactor instead of point to war
        // we have a link to target/classes

        File targetOfDependencyDir = new File(dir.getParent(), "jstest/src");
        if (targetOfDependencyDir.isDirectory()) {
            try {
                directoryCopier.copyDirectory(targetOfDependencyDir, output);
            } catch (IOException e) {
                log.error("error on copying same reactor target directory : " + targetOfDependencyDir, e);
            }
        } else {
            log.warn("Cannot manage overlay of a project in same reactor that is not build with jstest-maven-plugin");
        }
    }

    private void unpackArchiveArtifact(File file, File output) throws MojoExecutionException {
        if (!file.isFile()) {
            log.warn("cannot extract overlay, its not a file : " + file);
            return;
        }

        String archiveExt = FileUtils.getExtension(file.getAbsolutePath()).toLowerCase();
        try {
            UnArchiver unArchiver = archiverManager.getUnArchiver(archiveExt);
            unArchiver.setSourceFile(file);
            unArchiver.setDestDirectory(output);
            unArchiver.setOverwrite(true);
            unArchiver.extract();
        } catch (ArchiverException e) {
            throw new MojoExecutionException("Error unpacking file [" + file.getAbsolutePath() + "]" + "to ["
                    + output.getAbsolutePath() + "]", e);
        } catch (NoSuchArchiverException e) {
            log.warn("Skip unpacking dependency file [" + file.getAbsolutePath() + " with unknown extension ["
                    + archiveExt + "]");
        }
    }

}
