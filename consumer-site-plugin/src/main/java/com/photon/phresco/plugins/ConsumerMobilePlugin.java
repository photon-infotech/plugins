/**
 * Phresco Plugins
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import org.apache.commons.exec.ExecuteException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.jdom2.JDOMException;

import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.convertor.ThemeConvertor;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.manager.ConversionManager;
import com.photon.phresco.manager.ValidationManager;
import com.photon.phresco.parser.LocaleExtractor;
import com.photon.phresco.plugin.commons.DatabaseUtil;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.drupal.DrupalPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.status.ValidationStatus;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.photon.phresco.validator.DirectoryValidator;
import com.photon.phresco.validator.FileDimensionValidator;
import com.photon.phresco.validator.FileValidator;
import com.photon.phresco.vo.CsvFileVO;

public class ConsumerMobilePlugin extends DrupalPlugin {
	
	private File baseDir;
	private String buildNumber;
	private String environmentName;
	private boolean importSql;
	private File buildDir;
	private File binariesDir;
	private File buildFile;
	private File tempDir;
	private Log log;
	private String sqlPath;
	private ConversionManager conversionManager;
	
	public ConsumerMobilePlugin(Log log) {
		super(log);
	}

	public void themeValidator(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("Theme validation is being done...");
		try {
			LocaleExtractor localeExtractor = new LocaleExtractor(mavenProjectInfo, File.separator
					+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.manifest.name"),
					mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.target.dir"));
			List<String> localDirectories = localeExtractor.getLocaleDirectories();

			ValidationManager validationManager = new ValidationManager(mavenProjectInfo, File.separator
					+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.manifest.name"),
					mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.target.dir"));

			DirectoryValidator directoryValidator = new DirectoryValidator(localDirectories);
			validationManager.addValidator(directoryValidator);
			List<ValidationStatus> validationStatusDirectoryList = validationManager.validate();
			// log current messages in case next validation fails
			System.out.println("Dumping messages after folder validation");
			for (ValidationStatus v : validationStatusDirectoryList) {
				System.out.println(v.getMessage());
			}
			for (ValidationStatus v : validationStatusDirectoryList) {
				// must throw exception if folder does not exist otherwise next
				// validation will not work
				if (v.isStatus() == false) {
					throw new PhrescoException(v.getMessage());
				}
			}

			FileValidator fileValidator = new FileValidator(localDirectories);
			validationManager.addValidator(fileValidator);
			List<ValidationStatus> validationStatusFileList = validationManager.validate();
			// log current messages in case next validation fails
			System.out.println("Dumping messages after file validation");
			for (ValidationStatus v : validationStatusFileList) {
				System.out.println(v.getMessage());
			}
			for (ValidationStatus v : validationStatusFileList) {
				// must throw exception if folder does not exist otherwise next
				// validation will not work
				if (v.isStatus() == false) {
					throw new PhrescoException(v.getMessage());
				}
			}

			FileDimensionValidator fileDimensionValidator = new FileDimensionValidator(localDirectories);
			validationManager.addValidator(fileDimensionValidator);
			List<ValidationStatus> validationStatusFileDimList = validationManager.validate();
			// log current messages in case next validation fails
			System.out.println("Dumping messages after image dimension validation");
			for (ValidationStatus v : validationStatusFileDimList) {
				System.out.println(v.getMessage());
			}
			for (ValidationStatus v : validationStatusFileDimList) {
				// must throw exception if folder does not exist otherwise next
				// validation will not work
				if (v.isStatus() == false) {
					throw new PhrescoException(v.getMessage());
				}
			}
			// log all messages after successful build - all validations passed.
			// All status messages aggregated
			List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
			validationStatusList.addAll(validationStatusDirectoryList);
			validationStatusList.addAll(validationStatusFileList);
			validationStatusList.addAll(validationStatusFileDimList);
			String concatenatedStatusMessage = "";
			System.out.println("---- CONSOLIDATED MESSAGES-------");
			for (ValidationStatus v : validationStatusList) {
				concatenatedStatusMessage += v.getMessage();
			}
			System.out.println(concatenatedStatusMessage);
		} catch (ZipException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}

	}

	public void themeConvertor(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("Theme conversion is being done...");
		try {
			new ThemeConvertor().convert(mavenProjectInfo);
		} catch (Exception e) {
			throw new PhrescoException(e.getMessage());
		}
	}

	public void contentValidator(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("Content validation is being done...");		
		try {
			LocaleExtractor localeExtractor = new LocaleExtractor(mavenProjectInfo, File.separator
					+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.manifest.name"),
					mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.target.dir"));
			List<String> localDirectories = localeExtractor.getLocaleDirectories();

			ValidationManager validationManager = new ValidationManager(mavenProjectInfo, File.separator
					+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.manifest.name"),
					mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.target.dir"));

			DirectoryValidator directoryValidator = new DirectoryValidator(localDirectories);
			validationManager.addValidator(directoryValidator);
			List<ValidationStatus> validationStatusDirectoryList = validationManager.validate();
			// log current messages in case next validation fails
			System.out.println("Dumping messages after folder validation");
			for (ValidationStatus v : validationStatusDirectoryList) {
				System.out.println(v.getMessage());
			}
			for (ValidationStatus v : validationStatusDirectoryList) {
				// must throw exception if folder does not exist otherwise next
				// validation will not work
				System.out.println("v.isStatus()------->" + v.isStatus());
				if (v.isStatus() == false) {
					throw new PhrescoException(v.getMessage());
				}
			}

			FileValidator fileValidator = new FileValidator(localDirectories);
			validationManager.addValidator(fileValidator);
			List<ValidationStatus> validationStatusFileList = validationManager.validate();
			// log current messages in case next validation fails
			System.out.println("Dumping messages after file valdiation");
			for (ValidationStatus v : validationStatusFileList) {

				System.out.println(v.getMessage());
			}
			for (ValidationStatus v : validationStatusFileList) {
				// must throw exception if folder does not exist otherwise next
				// validation will not work
				if (v.isStatus() == false) {
					throw new PhrescoException(v.getMessage());
				}
			}

			FileDimensionValidator fileDimensionValidator = new FileDimensionValidator(localDirectories);
			validationManager.addValidator(fileDimensionValidator);
			List<ValidationStatus> validationStatusFileDimList = validationManager.validate();
			// log current messages in case next validation fails
			System.out.println("Dumping messages after image file dimension valdiation");
			for (ValidationStatus v : validationStatusFileDimList) {
				System.out.println(v.getMessage());
			}
			for (ValidationStatus v : validationStatusFileDimList) {
				// must throw exception if folder does not exist otherwise next
				// validation will not work
				if (v.isStatus() == false) {
					throw new PhrescoException(v.getMessage());
				}
			}
			// log all messages after successful build - all validations passed.
			// All status messages aggregated
			System.out.println("---- CONSOLIDATED MESSAGES-------");
			List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
			validationStatusList.addAll(validationStatusDirectoryList);
			validationStatusList.addAll(validationStatusFileList);
			validationStatusList.addAll(validationStatusFileDimList);
			String concatenatedStatusMessage = "";
			for (ValidationStatus v : validationStatusList) {
				concatenatedStatusMessage += v.getMessage();
			}
			System.out.println(concatenatedStatusMessage);
		} catch (ZipException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	public void contentConvertor(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		System.out.println("Content conversion is being done");
		try {
			
			System.out.println("Content COnvertor started");
			 conversionManager = new ConversionManager(mavenProjectInfo, File.separator
					+ "manifest.xml", mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.target.dir"));
			List<CsvFileVO> csvoFileList = conversionManager.convert(mavenProjectInfo);
			String phrescoTargetDir=null;
			conversionManager.replaceParameter(mavenProjectInfo, phrescoTargetDir);
			
			System.out.println(mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.php.file.name")
					+ " created at location" + "." + mavenProjectInfo.getBaseDir() + File.separator + "source"
					+ File.separator + "sites" + File.separator + "all" + File.separator + "modules" + File.separator
					+ "jnj_site_build" + File.separator + "build" + File.separator + "scripts");
		} catch (IOException e) {
			e.printStackTrace();
			throw new PhrescoException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PhrescoException(e);
		}
	}

	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		themeValidator(mavenProjectInfo);
		themeConvertor(mavenProjectInfo);
		contentValidator(mavenProjectInfo);
		contentConvertor(mavenProjectInfo);
		super.pack(configuration, mavenProjectInfo);
	}
	
	@Override
	public void deploy(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		baseDir = mavenProjectInfo.getBaseDir();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get(ENVIRONMENT_NAME);
        buildNumber = configs.get(BUILD_NUMBER);
        importSql = Boolean.parseBoolean(configs.get(EXECUTE_SQL));
        sqlPath = configs.get(FETCH_SQL);
        
		try {
			ConversionManager conversionManager = new ConversionManager();
			Map getEnv = conversionManager.getEnvironmentDetails(mavenProjectInfo, null);
			init();
			//createDb(getEnv);
			importSQL(getEnv);
			packDrupal();
			extractBuild();
			deploy();
			executeDrushCommands(mavenProjectInfo, getEnv);
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException(e);
		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init() throws MojoExecutionException  {
		try {
			System.out.println("Build No => " + buildNumber + " Environment => " + environmentName);
			if (StringUtils.isEmpty(buildNumber) || StringUtils.isEmpty(environmentName)) {
				callUsage();
			}
			
			PluginUtils pu = new PluginUtils();
			BuildInfo buildInfo = pu.getBuildInfo(Integer.parseInt(buildNumber));
			System.out.println("Build Name " + buildInfo);
			
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			buildFile = new File(buildDir.getPath() + File.separator + buildInfo.getBuildName());
			System.out.println("buildFile path " + buildFile.getPath());
			binariesDir = new File(baseDir.getPath() + BINARIES_DIR);
			
			String context = "";
			List<com.photon.phresco.configuration.Configuration> configurations = getConfiguration(Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration configuration : configurations) {
				context = configuration.getProperties().getProperty(Constants.SERVER_CONTEXT);
				break;
			}
			tempDir = new File(buildDir.getPath() + TEMP_DIR + File.separator + context);
			tempDir.mkdirs();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void callUsage() throws MojoExecutionException {
		System.out.println("Invalid usage.");
		System.out.println("Usage of Deploy Goal");
		System.out.println(
				"mvn drupal:deploy -DbuildName=\"Name of the build\""
				+ " -DenvironmentName=\"Multivalued evnironment names\""
				+ " -DimportSql=\"Does the deployment needs to import sql(TRUE/FALSE?)\"");
		throw new MojoExecutionException(
				"Invalid Usage. Please see the Usage of Deploy Goal");
	}
	
	private void createDb(Map getEnv) throws MojoExecutionException, PhrescoException {		
		try {
			System.out.println(getEnv);
			Connection Conn = DriverManager.getConnection 
					("jdbc:mysql://" + getEnv.get("host") + "/?user=" + getEnv.get("username") + "&password=" + getEnv.get("password")); 
			Statement s = Conn.createStatement(); 
			s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + getEnv.get("dbname"));
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	private void importSQL(Map getEnv) throws MojoExecutionException, PhrescoException {
		DatabaseUtil util = new DatabaseUtil();
		try {
			if (importSql) {
				createDb(getEnv);
				util.fetchSqlConfiguration(sqlPath, importSql, baseDir, environmentName);
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}	
	
	private void packDrupal() throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			//fetching drupal binary from repo
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_INITIALIZE);

			bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("[ERROR]")) {
					errorParam = true;
				}
			}
			if (errorParam) {
				throw new MojoExecutionException("Drupal Binary Download Failed ");
			}
			
			//packing drupal binary to build
			File drupalBinary = null;
			File[] listFiles = binariesDir.listFiles();
			for (File file : listFiles) {
				if (file.isDirectory()) {
					drupalBinary = new File(binariesDir + "/drupal");
					file.renameTo(drupalBinary);
				}
			}
			if (!drupalBinary.exists()) {
				throw new MojoExecutionException("Drupal binary not found");
			}
			if (drupalBinary != null) {
				FileUtils.copyDirectoryStructure(drupalBinary, tempDir);
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}
	
    private void extractBuild() throws MojoExecutionException {
		try {
			ArchiveUtil.extractArchive(buildFile.getPath(), tempDir.getPath(), ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void deploy() throws MojoExecutionException {
		String deployLocation = "";
		try {
			List<com.photon.phresco.configuration.Configuration> configurations = getConfiguration(Constants.SETTINGS_TEMPLATE_SERVER);
			for (com.photon.phresco.configuration.Configuration configuration : configurations) {
				deployLocation = configuration.getProperties().getProperty(Constants.SERVER_DEPLOY_DIR);
				break;
			}
			File deployDir = new File(deployLocation);
			if (!deployDir.exists()) {
				throw new MojoExecutionException(" Deploy Directory" + deployLocation + " Does Not Exists ");
			}
			System.out.println("Project is deploying into " + deployLocation);
			FileUtils.copyDirectoryStructure(tempDir.getParentFile(), deployDir);
			System.out.println("Project is deployed successfully");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	public void executeDrushCommands(MavenProjectInfo mavenProjectInfo,Map getEnv) throws ExecuteException, IOException, JDOMException {
		String OS = System.getProperty("os.name").toLowerCase();
		String command;
		String filePath;
		
		try 
		{ 
			System.out.println("Your OS => " + OS);
			 
		    filePath = (String) getEnv.get("deploy_dir")
				+ File.separator
				+ (String) getEnv.get("context")
				+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.build.scripts.file.path");
			
			System.out.println("Path to Installation File => " + filePath);
		
			if (OS.indexOf("win") >= 0) {				
	            command = "cmd /c drush php-script " + filePath;
	            
			} else {				
	            command = "drush php-script " + filePath;
				//command = "/bin/bash -c \"drush php-scrip\"" + filePath;
			}
			
            System.out.println("Started creating content...");
			Process process = Runtime.getRuntime().exec(command);
        		
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = reader.readLine(); 
			while(line != null) 
			{
				System.out.println(line); 
				line = reader.readLine(); 
				
				if (line == null) {
					process.destroy();
				}
			} 
			System.out.println("Content creation is done.");
		} 
		catch(IOException e1) {
		    System.out.println("Exception thrown while executing the Drush command."+  e1.getStackTrace());
		    e1.getStackTrace();
		    e1.getMessage();
		}
	}

	private List<com.photon.phresco.configuration.Configuration> getConfiguration(String type) throws PhrescoException, ConfigurationException {
		ConfigManager configManager = PhrescoFrameworkFactory.getConfigManager(new File(baseDir.getPath() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + CONFIG_FILE));
		return configManager.getConfigurations(environmentName, type);		
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir.getParentFile());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
