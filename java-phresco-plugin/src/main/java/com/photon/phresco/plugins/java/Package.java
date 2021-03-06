/**
 * java-phresco-plugin
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
package com.photon.phresco.plugins.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginConfigurationException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginNotFoundException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.sonatype.aether.RepositorySystemSession;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ModuleInfo;
import com.photon.phresco.configuration.Environment;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.impl.ConfigManagerImpl;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Assembly.FileSets.FileSet;
import com.photon.phresco.plugins.model.Assembly.FileSets.FileSet.Excludes;
import com.photon.phresco.plugins.model.Assembly.FileSets.FileSet.Includes;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.util.MavenSonatypeAetherResolver;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.plugins.util.WarConfigProcessor;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Build;
import com.phresco.pom.model.Model;
import com.phresco.pom.util.PomProcessor;

public class Package implements PluginConstants {
	
	private MavenProject project;
	private File baseDir;
	private String environmentName;
	private String buildName;
	private String buildNumber;
	private int buildNo;
	private String mainClassName;
	private String jarName;
	private File targetDir;
	private File buildDir;
	private File buildInfoFile;
	private File tempDir;
	private int nextBuildNo;
	private String zipName;
	private Date currentDate;
	private String context;
	private Log log;
	private PluginPackageUtil util;
	private PluginUtils pu;
	private String sourceDir;
	private StringBuilder builder;
	private String packageType;
	private String subModule = "";
	private File workingDirectory;
	private File pomFile;
	private String srcPomFileName;
	private String packagingType;
	private MavenSession mavenSession;
    private BuildPluginManager pluginManager;
    private ApplicationInfo appInfo;
    private String dotPhrescoDirName;
    private File dotPhrescoDir;
    private File srcDirectory;
    private File dotPhrescoRoot;
    private ApplicationInfo appInfoRoot;
    private String buildVersion;
    private PlexusContainer plexusContainer;
    
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		plexusContainer = mavenProjectInfo.getPlexusContainer();
		baseDir = mavenProjectInfo.getBaseDir();
        project = mavenProjectInfo.getProject();
        mavenSession = mavenProjectInfo.getMavenSession();
        pluginManager = mavenProjectInfo.getPluginManager();
        buildVersion = mavenProjectInfo.getBuildVersion();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildName = configs.get(BUILD_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        jarName = configs.get(JAR_NAME);
        mainClassName = configs.get(MAIN_CLASS_NAME);
        util = new PluginPackageUtil();
        pu = new PluginUtils();
        builder = new StringBuilder();
        packageType = configs.get("packageType");
        pomFile = project.getFile();
        String packMinifiedFilesValue = configs.get(PACK_MINIFIED_FILES);
        workingDirectory = new File(baseDir.getPath());
        dotPhrescoDirName = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR);
        dotPhrescoDir = baseDir;
        if (StringUtils.isNotEmpty(dotPhrescoDirName)) {
        	dotPhrescoDir = new File(baseDir.getParent() + File.separator + dotPhrescoDirName);
        }
        dotPhrescoRoot = dotPhrescoDir;
        File warConfigFile = new File(dotPhrescoDir.getPath() + File.separator + DOT_PHRESCO_FOLDER + File.separator + WAR_CONFIG_FILE);
        File warSourceConfigFile = new File(baseDir.getPath() + File.separator + CONF_DIRECTORY  + File.separator + WAR_CONFIG_FILE);
        if (StringUtils.isNotEmpty(mavenProjectInfo.getModuleName())) {
        	subModule = mavenProjectInfo.getModuleName();
        	warConfigFile = new File(dotPhrescoDir.getPath() + File.separator + subModule + File.separator + DOT_PHRESCO_FOLDER + File.separator + WAR_CONFIG_FILE);
        	workingDirectory = new File(baseDir.getPath() + File.separator + subModule);
        	warSourceConfigFile = new File(workingDirectory + File.separator +  CONF_DIRECTORY + File.separator + WAR_CONFIG_FILE);
        	dotPhrescoDir = new File(dotPhrescoDir.getPath() + File.separatorChar + subModule);
        	pomFile = pu.getPomFile(dotPhrescoDir, workingDirectory);
        } 
        File splitProjectDirectory = pu.getSplitProjectSrcDir(pomFile, dotPhrescoDir, subModule);
    	srcDirectory = workingDirectory;
    	if (splitProjectDirectory != null) {
    		srcDirectory = splitProjectDirectory;
    		warSourceConfigFile = new File(srcDirectory+ File.separator +  CONF_DIRECTORY + File.separator + WAR_CONFIG_FILE);
    	} 
        initPomAndPackage();
        PluginUtils.checkForConfigurations(dotPhrescoDir, environmentName);
        try { 
			init();
			if (environmentName != null) {
				updateFinalName();
				configure();
			}
			if(StringUtils.isNotEmpty(packMinifiedFilesValue)) {
				updateConfigFile(packMinifiedFilesValue, warConfigFile);
				updateConfigFile(packMinifiedFilesValue, warSourceConfigFile);
			}
			getMavenCommands(configuration);
			executeMvnPackage();
			if (!"pom".equals(packagingType)) {
				boolean buildStatus = build();
				writeBuildInfo(buildStatus);
			}
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		} catch (JAXBException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}	
	}

	private void updateConfigFile(String packMinifiedFilesValue,
			File warConfigFile) throws JAXBException, IOException,
			PhrescoException {
		boolean packMinifiedFiles = Boolean.parseBoolean(packMinifiedFilesValue);
		WarConfigProcessor configProcessor = new WarConfigProcessor(warConfigFile);
		emptyFileSetExclude(configProcessor, EXCLUDE_FILE);
		if(!packMinifiedFiles) {
			List<String> excludes = new ArrayList<String>();
			excludes.add("/js/**/*-min.js");
			excludes.add("/js/**/*.min.js");
			excludes.add("/css/**/*-min.css");
			excludes.add("/css/**/*.min.css");
			setFileSetExcludes(configProcessor, EXCLUDE_FILE, excludes);
		}
		includeLibraryJsFiles(configProcessor);
		configProcessor.save();
	}
	
	private void initPomAndPackage() throws PhrescoException {
		appInfo = pu.getAppInfo(dotPhrescoDir);
		appInfoRoot = pu.getAppInfo(dotPhrescoRoot);
		srcPomFileName = project.getFile().getName();
		try {
			File srcPomFile = new File(srcDirectory.getPath() + File.separator + appInfo.getPomFile());
			if(!srcPomFile.exists()) {
				srcPomFile = new File(srcDirectory.getPath() + File.separator + "pom.xml");
			}
			packagingType = new PomProcessor(srcPomFile).getModel().getPackaging();
			srcPomFileName = srcPomFile.getName();
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}
	
	private void setFileSetExcludes(WarConfigProcessor configProcessor, String FileSetId, List<String> exclues) throws PhrescoException {
		try {
			FileSet fileSet = configProcessor.getFileSet(FileSetId);
			if(fileSet.getExcludes() == null) {
				Excludes excludes = new Excludes();
				fileSet.setExcludes(excludes);
			}
			for (String exclue : exclues) {
				fileSet.getExcludes().getExclude().add(exclue);
			}
		} catch (JAXBException e) {
			throw new PhrescoException(e);
		} 
	}
	
	private void includeLibraryJsFiles(WarConfigProcessor configProcessor) throws PhrescoException {
		try {
			FileSet fileSet = configProcessor.getFileSet("includeLibJs");
			if (fileSet == null) {
				FileSet fs = new FileSet();
				fs.setId("includeLibJs");
				fs.setDirectory("src/main/webapp");
				fs.setOutputDirectory("/");
				
				List<String> inlcudeFiles = new ArrayList<String>();
				inlcudeFiles.add("/js/**/lib/**/*-min.js");
				inlcudeFiles.add("/js/**/lib/**/*.min.js");
				inlcudeFiles.add("/js/**/libs/**/*-min.js");
				inlcudeFiles.add("/js/**/libs/**/*.min.js");
				
				Includes includes = new Includes();
				fs.setIncludes(includes);
				
				for (String inlcudeFile : inlcudeFiles) {
					fs.getIncludes().getInclude().add(inlcudeFile);
				}
				configProcessor.createFileSet(fs);
			}
		} catch (JAXBException e) {
			throw new PhrescoException(e);
		} 
	}
	
	private void emptyFileSetExclude(WarConfigProcessor configProcessor, String FileSetId) throws PhrescoException {
		try {
			FileSet fileSet = configProcessor.getFileSet(FileSetId);
			fileSet.setExcludes(null);
		} catch (JAXBException e) {
			throw new PhrescoException(e);
		}
	}
	
	private void init() throws MojoExecutionException {
		try {
			buildDir = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY);
			buildInfoFile = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY + BUILD_INFO_FILE);
			File buildInfoDir = new File(workingDirectory.getPath() + PluginConstants.BUILD_DIRECTORY);
			targetDir = new File(project.getBuild().getDirectory());
			if(StringUtils.isNotEmpty(subModule)) {
				targetDir = new File(baseDir.getPath() + File.separator + subModule + DO_NOT_CHECKIN_FOLDER + File.separator + TARGET);
			} 
			dotPhrescoDir = getProjectRoot(dotPhrescoDir);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			
			if (!buildInfoDir.exists()) {
				buildInfoDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			nextBuildNo = util.generateNextBuildNo(buildInfoFile);
			currentDate = Calendar.getInstance().getTime();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private File getProjectRoot(File childDir) {
		File[] listFiles = childDir.listFiles(new PhrescoDirFilter());
		if (listFiles != null && listFiles.length > 0) {
			return childDir;
		}
		if (childDir.getParentFile() != null) {
			return getProjectRoot(childDir.getParentFile());
		}
		return null;
	}

	public class PhrescoDirFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.equals(DOT_PHRESCO_FOLDER);
		}
	}

	private void updateFinalName() throws MojoExecutionException {
		try {
			PomProcessor pomprocessor = new PomProcessor(pomFile);
			if(isJarProject(project)) {
				context = jarName;
				updatemainClassName();
			} else {
				String envName = environmentName;
				List<String> envList = pu.csvToList(environmentName);

				if (environmentName.indexOf(',') > -1) { // multi-value
					envName = readDefaultEnv(envList);
				}
				List<com.photon.phresco.configuration.Configuration> configurations = pu.getConfiguration(dotPhrescoDir, envName, Constants.SETTINGS_TEMPLATE_SERVER);
				if(CollectionUtils.isNotEmpty(configurations)) {
					for (com.photon.phresco.configuration.Configuration configuration : configurations) {
						context = configuration.getProperties().getProperty(Constants.SERVER_CONTEXT);
						break;
					}
				}
			}
			sourceDir = pomprocessor.getProperty(POM_PROP_KEY_SOURCE_DIR);
			if (StringUtils.isEmpty(context)) {
				return;
			}
			pomprocessor.setFinalName(context);
			pomprocessor.save();
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private boolean isJarProject(MavenProject project) throws PhrescoPomException, PhrescoException {
		boolean jarProject = false;
		if(StringUtils.isNotEmpty(packagingType)) {
			if(packagingType.equals(PACKAGING_TYPE_JAR)) {
				jarProject = true;
			}
		}
		return jarProject;
	}
	
	public String readDefaultEnv(List<String> envList) throws MojoExecutionException {
		boolean defaultEnv = false;
		String defaultEnvName = "";
		ConfigManager configManager = null;
		try {
//			String customerId = pu.readCustomerId(dotPhrescoDir);
			String readProjectCode = pu.readProjectCode(baseDir);
			File settingsXml = new File(Utility.getProjectHome() + readProjectCode + Constants.SETTINGS_XML);
			if (settingsXml.exists()) {
				configManager = new ConfigManagerImpl(settingsXml);
				List<Environment> settingsEnvironments = configManager.getEnvironments(envList);
				for (Environment environment : settingsEnvironments) {
					defaultEnv = environment.isDefaultEnv();
					if (defaultEnv) {
						defaultEnvName = environment.getName();
					}
				}
			}
			if (!defaultEnv) {
				configManager = new ConfigManagerImpl(new File(dotPhrescoDir.getPath() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + Constants.CONFIGURATION_INFO_FILE));
				List<Environment> configurationEnvironments = configManager.getEnvironments(envList);
				for (Environment configEnvironment : configurationEnvironments) {
					defaultEnv = configEnvironment.isDefaultEnv();
					if (defaultEnv) {
						defaultEnvName = configEnvironment.getName();
					}
				}
			}
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ConfigurationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return defaultEnvName;
	}
	
	private void updatemainClassName() throws MojoExecutionException {
		try {
			if (StringUtils.isEmpty(mainClassName)) {
				return;
			}
			List<Element> configList = new ArrayList<Element>();
			PomProcessor pomprocessor = new PomProcessor(pomFile);
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element archive = doc.createElement(JAVA_POM_ARCHIVE);
			Element manifest = doc.createElement(JAVA_POM_MANIFEST);
			Element addClasspath = doc.createElement(JAVA_POM_ADD_PATH);
			addClasspath.setTextContent("true");
			manifest.appendChild(addClasspath);
			Element mainClass = doc.createElement(JAVA_POM_MAINCLASS);
			mainClass.setTextContent(mainClassName);
			manifest.appendChild(addClasspath);
			manifest.appendChild(mainClass);
			archive.appendChild(manifest);
			configList.add(archive);

			pomprocessor.addConfiguration(JAR_PLUGIN_GROUPID, JAR_PLUGIN_ARTIFACT_ID, configList, false);
			pomprocessor.save();
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void getMavenCommands(Configuration configuration) {
		List<Parameter> parameters = configuration.getParameters().getParameter();
		for (Parameter parameter : parameters) {
			if(parameter.getPluginParameter() != null && parameter.getMavenCommands() != null) {
				List<MavenCommand> mavenCommands = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCommand : mavenCommands) {
					if(parameter.getValue().equals(mavenCommand.getKey())) {
						builder.append(mavenCommand.getValue());
						builder.append(STR_SPACE);
					}
				}
			}
		}
	}
	
	private void executeMvnPackage() throws MojoExecutionException, IOException, PhrescoException {
		log.info("Packaging the project...");
		StringBuilder sb = new StringBuilder();
		sb.append(MVN_CMD);
		sb.append(STR_SPACE);
		sb.append(MVN_PHASE_CLEAN);
		sb.append(STR_SPACE);
		sb.append(MVN_PHASE_INSTALL);
		sb.append(STR_SPACE);
		sb.append(Constants.HYPHEN_F);
		sb.append(STR_SPACE);
		File rootPomFile = pu.getPomFile(dotPhrescoRoot, baseDir);
		sb.append(rootPomFile.getName());
		sb.append(STR_SPACE);
		sb.append(builder.toString());
		sb.append(STR_SPACE);
		sb.append(FrameworkConstants.HYPHEN_N);
		if(StringUtils.isNotEmpty(buildVersion)) {
			sb.append(STR_SPACE);
			sb.append("-Dpackage.version=" + buildVersion);
		}
		List<String> buildModules = getBuildModules(appInfoRoot, subModule);
		if (StringUtils.isNotEmpty(subModule)) {
			buildModules.add(subModule);
		}
		String baseCommand = sb.toString();
		executeCommand(baseCommand, baseDir,"");
		if(CollectionUtils.isNotEmpty(buildModules)) {
			for (String module : buildModules) {
				File dir = new File(baseDir, module);
				File subPomFile = pu.getPomFile(new File(dotPhrescoRoot, module), dir);
				StringBuilder stringBuilder = new StringBuilder(); 
				stringBuilder.append(MVN_CMD);
				stringBuilder.append(STR_SPACE);
				stringBuilder.append(MVN_PHASE_CLEAN);
				stringBuilder.append(STR_SPACE);
				stringBuilder.append(MVN_PHASE_INSTALL);
				stringBuilder.append(STR_SPACE);
				stringBuilder.append(Constants.HYPHEN_F);
				stringBuilder.append(STR_SPACE);
				stringBuilder.append(subPomFile.getName());
				stringBuilder.append(STR_SPACE);
				stringBuilder.append(builder.toString());
				sb.append(STR_SPACE);
				sb.append("-Dpackage.version=" + buildVersion);
				String command = stringBuilder.toString();
				executeCommand(command, dir, module);
			}
		}
	}
	
	private void installArtifact(File currentDir, String module) throws PhrescoException {
		String srcDir = project.getProperties().getProperty(Constants.POM_PROP_KEY_SPLIT_SRC_DIR);
		if (StringUtils.isNotEmpty(srcDir)) {
    		String appDirName = appInfo.getAppDirName();
			currentDir = new File(Utility.getProjectHome() + File.separatorChar + appDirName + File.separatorChar + srcDir + File.separatorChar + module);
		}
		try { 
			File phrescoPom = pu.getPomFile(new File(dotPhrescoRoot.getPath() + File.separatorChar + module), new File(baseDir.getPath() + File.separatorChar + module));
			PomProcessor phrescoPomProcessor = new PomProcessor(phrescoPom);
			File pomFile = new File(currentDir, phrescoPomProcessor.getProperty("source.pom"));
			PomProcessor processor = new PomProcessor(pomFile);
			String packagingSrcPOm = processor.getModel().getPackaging();
			if(StringUtils.isEmpty(packagingSrcPOm)) {
				packagingSrcPOm = "jar";
			}
			StringBuilder builder = new StringBuilder("mvn install:install-file ");
			builder.append("-DgroupId=").append(processor.getGroupId()).append(" ");
			builder.append("-DartifactId=").append(processor.getArtifactId()).append(" ");
			String projversion = processor.getVersion();
			if(StringUtils.isNotEmpty(buildVersion)) {
				projversion = buildVersion;
			}
			builder.append("-Dversion=").append(projversion).append(" ");
			builder.append("-Dpackaging=").append(packagingSrcPOm).append(" ");
			String finalName = "";
			String buildDir = "";
			if(phrescoPom.exists()) {
				finalName = phrescoPomProcessor.getFinalName();
				if(StringUtils.isEmpty(finalName)) {
					finalName = project.getBuild().getFinalName();
				}
				Model model = phrescoPomProcessor.getModel();
				Build build = model.getBuild();
				if(build != null) {
					buildDir = build.getDirectory();
				}
				if(StringUtils.isEmpty(buildDir)) {
					buildDir = project.getBuild().getDirectory();
				}
			}
			String fileConfig = "";
			StringBuilder fileString = new StringBuilder();
						
			fileString.append(buildDir).append("/").append(finalName).append(".").append(packagingSrcPOm);
			fileConfig = fileString.toString();
			if("pom".equals(packagingSrcPOm)) {
				fileConfig = project.getProperties().getProperty("source.pom");
			}
			builder.append("-Dfile=").append("" + fileConfig);
			String line = "";
			BufferedReader bufferedReader = Utility.executeCommand(builder.toString(), currentDir.toString());
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line); //do not use getLog() here as this line already contains the log type.
			}
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		}
	}
	
	 private Xpp3Dom convertPlexusConfiguration(PlexusConfiguration config) {
		    Xpp3Dom xpp3DomElement = new Xpp3Dom(config.getName());
		    xpp3DomElement.setValue(config.getValue());
		    for (String name : config.getAttributeNames()) {
		      xpp3DomElement.setAttribute(name, config.getAttribute(name));
		    }
		    for (PlexusConfiguration child : config.getChildren()) {
		      xpp3DomElement.addChild(convertPlexusConfiguration(child));
		    }
		    return xpp3DomElement;
		  }
	 
	private void executeCommand(String command, File workDir, String module) throws IOException, PhrescoException {
		String line ="";
		BufferedReader bufferedReader = Utility.executeCommand(command, workDir.toString());
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line); //do not use getLog() here as this line already contains the log type.
		}
		if(project.getFile().getName().equals(appInfo.getPhrescoPomFile())) {
			installArtifact(workDir, module);
		}
	}
	
	private List<String> getBuildModules(ApplicationInfo appInfo, String moduleName) {
		List<String> depModuleList = new ArrayList<String>();
		List<ModuleInfo> modules = appInfo.getModules();
		if (CollectionUtils.isNotEmpty(modules)) {
			for (ModuleInfo moduleInfo : modules) {
				if (moduleName.equals(moduleInfo.getCode())) {
					List<String> dependentModules = moduleInfo.getDependentModules();
					if (CollectionUtils.isNotEmpty(dependentModules)) {
						for (String dependentModule : dependentModules) {
							getBuildModules(appInfo, dependentModule);
							if (!depModuleList.contains(dependentModule)) {
								depModuleList.add(dependentModule);
							}
						}
					}
				}
			}
		}
		return depModuleList;
		
	}
	 
	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {
			log.info("Building the project...");
			createPackage();
		} catch (Exception e) {
			isBuildSuccess = false;
			log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return isBuildSuccess;
	}

	private void configure() throws MojoExecutionException {
		log.info("Configuring the project....");
		try {
			adaptSourceConfig();
			pu.writeDatabaseDriverToConfigXml(srcDirectory, sourceDir, environmentName);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void adaptSourceConfig() throws MojoExecutionException {
		PomProcessor processor  = null;
		File sourceConfigXML = null;
		try {
			processor = new PomProcessor(pomFile);
			String configXml = processor.getProperty(POM_PROP_CONFIG_FILE);
			if(StringUtils.isNotEmpty(configXml)) {
				sourceConfigXML = new File(srcDirectory + configXml);
			} else {
				sourceConfigXML = new File(srcDirectory + sourceDir + FORWARD_SLASH +  CONFIG_FILE);
			}
			File parentFile = sourceConfigXML.getParentFile();
			if (parentFile.exists()) {
				pu.executeUtil(environmentName, dotPhrescoDir.getPath(), sourceConfigXML);
			}
		} catch (PhrescoPomException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private void createPackage() throws MojoExecutionException {
		if(StringUtils.isEmpty(packagingType)) {
			packagingType = PACKAGING_TYPE_JAR;
		}
		try {
			zipName = util.createPackage(buildName, buildNumber, nextBuildNo, currentDate);
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			File packageInfoFile = new File(dotPhrescoDir.getPath() + File.separator + DOT_PHRESCO_FOLDER + File.separator + PHRESCO_PACKAGE_FILE);
			String zipNameWithoutExt = zipName.substring(0, zipName.lastIndexOf('.'));
			if ("war".equals(packagingType)) {
				if("zip".equals(packageType)) {
					copyZipToPackage(zipNameWithoutExt, context);
					return;
				} else {
					copyWarToPackage(zipNameWithoutExt, context);
				}
			} else {
				copyJarToPackage(zipNameWithoutExt);
			}
			PluginUtils.createBuildResources(packageInfoFile, workingDirectory, tempDir);
			ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}
	
	

	private void copyJarToPackage(String zipNameWithoutExt) throws MojoExecutionException {
		try {
			String[] list = targetDir.list(new JarFileNameFilter());
			if (list.length > 0) {
				File jarFile = new File(targetDir.getPath() + File.separator + list[0]);
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				FileUtils.copyFileToDirectory(jarFile, tempDir);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void copyWarToPackage(String zipNameWithoutExt, String context) throws MojoExecutionException {
		try {
			String[] list = targetDir.list(new WarFileNameFilter());
			if (list.length > 0) {
				File warFile = new File(targetDir.getPath() + File.separator + list[0]);
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				File contextWarFile = new File(targetDir.getPath() + File.separator + context + ".war");
				warFile.renameTo(contextWarFile);
				FileUtils.copyFileToDirectory(contextWarFile, tempDir);
			} else {
				throw new MojoExecutionException(context + ".war not found in " + targetDir.getPath());
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void copyZipToPackage(String zipNameWithoutExt, String context) throws MojoExecutionException {
		try {
			String[] list = targetDir.list(new ZipFileNameFilter());
			if (list.length > 0) {
				File zipFile = new File(targetDir.getPath() + File.separator + list[0]);
				if(!buildDir.exists()) {
					buildDir.mkdirs();
				}
				FileUtils.copyFileToDirectory(zipFile, buildDir);
				File contextZipFile = new File(buildDir.getPath() + File.separator + zipNameWithoutExt + ".zip");
				File buildZipFile = new File(buildDir, zipFile.getName());
				buildZipFile.renameTo(contextZipFile);
			} else {
				throw new MojoExecutionException(context + ".war not found in " + targetDir.getPath());
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		if(StringUtils.isEmpty(packagingType)) {
			return;
		}
		util.writeBuildInfo(isBuildSuccess, buildName, buildNumber, nextBuildNo, environmentName, buildNo, currentDate, buildInfoFile);
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			if(tempDir != null && tempDir.exists()) {
				FileUtils.deleteDirectory(tempDir);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}

class WarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".war");
	}
}

class ZipFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".zip");
	}
}

class JarFileNameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".jar");
	}

}
