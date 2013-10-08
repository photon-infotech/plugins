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
package net.awired.jstest.mojo.inherite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.awired.jstest.resource.ResourceDirectory;
import net.awired.jstest.resource.ResourceResolver;
import net.awired.jstest.runner.RunnerType;
import net.awired.jstest.runner.TestType;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

public abstract class JsTestConfiguration extends AbstractMojo {

    /**
     * @parameter default-value="${project.build.sourceDirectory}" expression="${sourceDir}"
     */
    private File sourceDir;

    /**
     * @parameter default-value="${project.basedir}" expression="${baseDir}"
     */
    
    private File baseDir;
    
    /**
     * @parameter default-value= ".xml,.json,.tmp" expression="${includes}"
     */
    private List<String> includes;
    
    /**
     * @parameter
     */
    private List<String> sourceIncludes = ResourceDirectory.DEFAULT_INCLUDES;

    /**
     * @parameter
     */
    private List<String> sourceExcludes = ResourceDirectory.DEFAULT_EXCLUDES;

    /**
     * @parameter default-value="${project.build.testSourceDirectory}" expression="${testDir}"
     */
    private File testDir;

    /**
     * @parameter
     */
    private List<String> testIncludes = ResourceDirectory.DEFAULT_INCLUDES;

    /**
     * @parameter
     */
    private List<String> testExcludes = ResourceDirectory.DEFAULT_EXCLUDES;

    /**
     * @parameter expression="${overlaydirs}"
     */
    private List<File> preloadOverlayDirs;

    /**
     * @parameter
     */
    private List<String> overlayIncludes = ResourceDirectory.DEFAULT_INCLUDES;

    /**
     * @parameter
     */
    private List<String> overlayExcludes = ResourceDirectory.DEFAULT_EXCLUDES;

    /**
     * @parameter default-value="8234" expression="${devPort}"
     */
    private int devPort;

    /**
     * @parameter default-value="8234" expression="${testPort}"
     */
    private int testPort;

    /**
     * will increment port number if not free
     * 
     * @parameter default-value="true" expression="${testPortFindFree}"
     */
    private boolean testPortFindFree;

    /**
     * @parameter expression="${coverage}" default-value="false"
     */
    private boolean coverage;

    /**
     * @parameter expression="${emulator}" default-value="true"
     */
    private boolean emulator;

    /**
     * @parameter expression="${amdType}"
     */
    private RunnerType runnerType = RunnerType.DEFAULT;

    /**
     * @parameter expression="${testType}"
     */
    private TestType testType;

    /**
     * @parameter expression="${runnerTemplate}"
     */
    private String runnerTemplate;

    /**
     * @parameter expression="${amdPreload}"
     */
    private List<String> amdPreloads = new ArrayList<String>();

    /**
     * @parameter expression="${runnerAmdFile}"
     */
    private String runnerAmdFile;

    /**
     * @parameter expression="${skipTests}"
     */
    private boolean skipTests;

    /**
     * @parameter default-value="false" expression="${maven.test.skip}"
     */
    private boolean mavenTestSkip;

    /**
     * @parameter default-value="false" expression="${maven.test.failure.ignore}"
     */
    private boolean mavenTestFailureIgnore;

    ///////////////////////////////////////////////////


    /**
     * @parameter default-value="${project.build.directory}${file.separator}${project.build.finalName}"
     */
    private String warTargetDir;

    /**
     * @parameter expression="${packBeforeTest}" default-value="false"
     */
    private boolean packBeforeTest;

    /**
     * @parameter default-value="${project.build.finalName}"
     */
    protected String context;

    /**
     * @parameter default-value="${project.build.directory}${file.separator}jstest"
     */
    private File jsTestTargetDir;

    /**
     * @parameter default-value="${project.build.directory}${file.separator}jstest${file.separator}coverage.dat"
     */
    private File coverageReportFile;

    /**
     * @parameter default-value="${project.build.directory}${file.separator}jstest${file.separator}report"
     */
    private File reportDir;

    /**
     * @parameter default-value="${project.build.directory}${file.separator}jstest${file.separator}src"
     */
    private File targetSrcDir;

    /**
     * @parameter default-value="${project.build.directory}${file.separator}jstest${file.separator}instrumented"
     */
    private File instrumentedDirectory;

    /**
     * @parameter expression="${addOverlaysToSourceMap}" default-value="true"
     */
    private boolean addOverlaysToSourceMap;
    
    public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> fileIncludes) {
		includes = fileIncludes;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	/**
     * @parameter default-value="${project.build.directory}${file.separator}jstest${file.separator}overlays"
     */
    private File overlayDirectory;

    /**
     * @parameter default-value="${project}"
     */
    private MavenProject mavenProject;
    
    //////////////////////////////////////////////////////////

    public boolean isIgnoreFailure() {
        return mavenTestFailureIgnore;
    }

    public boolean isSkipTests() {
        return skipTests || mavenTestSkip;
    }

    public boolean isSkipTestsCompile() {
        return mavenTestSkip;
    }

    public RunnerType buildAmdRunnerType() {
        if (runnerAmdFile != null) {
            this.runnerType.setAmdFile(runnerAmdFile);
        }
        if (runnerTemplate != null) {
            this.runnerType.setTemplate(runnerTemplate);
        }
        return this.runnerType;
    }

    public TestType buildTestType(ResourceResolver resourceResolver) throws MojoExecutionException {
        if (testType == null) {
            for (TestType testType : TestType.values()) {
                File resource = resourceResolver.getResource(ResourceResolver.SRC_RESOURCE_PREFIX
                        + testType.getTesterResources()[0]);
                File libResource = resourceResolver.getResource(ResourceResolver.LIB_RESOURCE_PREFIX
                        + testType.getTesterResources()[0]);
                if (resource != null || libResource != null) {
                    this.testType = testType;
                }
            }
        }
        if (testType == null) {
            throw new MojoExecutionException("Cannot found test type sources, for types " + TestType.values());
        }
        return testType;
    }

    private ResourceDirectory buildInstrumentedSrcResourceDirectory() {
        return new ResourceDirectory(instrumentedDirectory, sourceIncludes, sourceExcludes);
    }

    private ResourceDirectory buildTargetSrcResourceDirectory() {
        return new ResourceDirectory(targetSrcDir, sourceIncludes, sourceExcludes);
    }

    public ResourceDirectory buildSrcResourceDirectory() {
        ResourceDirectory resourceDirectory = new ResourceDirectory(sourceDir, sourceIncludes, sourceExcludes);
        resourceDirectory.setUpdatable(true);
        return resourceDirectory;
    }

    public ResourceDirectory buildCurrentSrcDir(boolean serverMode) {
        if (serverMode) {
            return buildSrcResourceDirectory();
        } else if (isCoverage()) {
            return buildInstrumentedSrcResourceDirectory();
        } else {
            return buildTargetSrcResourceDirectory();
        }
    }

    public File getPreparedReportDir() {
        reportDir.mkdirs();
        return reportDir;
    }

    public ResourceDirectory buildTestResourceDirectory() {
        ResourceDirectory resourceDirectory = new ResourceDirectory(testDir, testIncludes, testExcludes);
        resourceDirectory.setUpdatable(true);
        return resourceDirectory;
    }

    public File getOverlayDirectory() {
        return overlayDirectory;
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public boolean isCoverage() {
        return coverage;
    }

    public File getInstrumentedDirectory() {
        return instrumentedDirectory;
    }

    public File getTargetSourceDirectory() {
        return targetSrcDir;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public int getDevPort() {
        return devPort;
    }

    public File getCoverageReportFile() {
        return coverageReportFile;
    }

    public boolean isEmulator() {
        return emulator;
    }

    public int getTestPort() {
        return testPort;
    }

    public boolean isTestPortFindFree() {
        return testPortFindFree;
    }

    public File getTestDir() {
        return testDir;
    }

    public List<String> getAmdPreloads() {
        return amdPreloads;
    }

    public String getWarTargetDir() {
        return warTargetDir;
    }

    public void setWarTargetDir(String warTargetDir) {
        this.warTargetDir = warTargetDir;
    }

    public boolean isPackBeforeTest() {
        return packBeforeTest;
    }

    public void setPackBeforeTest(boolean packBeforeTest) {
        this.packBeforeTest = packBeforeTest;
    }

	public boolean isAddOverlaysToSourceMap() {
		return addOverlaysToSourceMap;
	}

	public void setAddOverlaysToSourceMap(boolean addOverlaysToSourceMap) {
		this.addOverlaysToSourceMap = addOverlaysToSourceMap;
	}
}
