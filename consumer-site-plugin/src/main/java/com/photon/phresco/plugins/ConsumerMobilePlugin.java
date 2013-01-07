package com.photon.phresco.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.manager.ValidationManager;
import com.photon.phresco.parser.LocaleExtractor;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.drupal.DrupalPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.status.ValidationStatus;
import com.photon.phresco.validator.DirectoryValidator;
import com.photon.phresco.validator.FileDimensionValidator;
import com.photon.phresco.validator.FileValidator;


public class ConsumerMobilePlugin extends DrupalPlugin {

	public ConsumerMobilePlugin(Log log) {
		super(log);
	}

	public void themeValidator(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		//log.info("Theme validation is being done");
		try {

			LocaleExtractor localeExtractor = new LocaleExtractor(
					mavenProjectInfo, File.separator + "theme-manifest.xml",
					mavenProjectInfo.getProject().getProperties()
							.getProperty("phresco.theme.target.dir"));
			List<String> localDirectories = localeExtractor
					.getLocaleDirectories();

			ValidationManager validationManager = new ValidationManager(
					mavenProjectInfo, File.separator + "theme-manifest.xml",
					mavenProjectInfo.getProject().getProperties()
							.getProperty("phresco.theme.target.dir"));

			DirectoryValidator directoryValidator = new DirectoryValidator(
					localDirectories);
			validationManager.addValidator(directoryValidator);
			List<ValidationStatus> validationStatusDirectoryList = validationManager
					.validate();
			for (ValidationStatus v : validationStatusDirectoryList) {
				System.out.println(v.getMessage());
				//log.info(v.getMessage());
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
			List<ValidationStatus> validationStatusFileList = validationManager
					.validate();

			for (ValidationStatus v : validationStatusFileList) {
				System.out.println(v.getMessage());
				//log.info(v.getMessage());
			}
			for (ValidationStatus v : validationStatusFileList) {
				// must throw exception if folder does not exist otherwise next
				// validation will not work
				if (v.isStatus() == false) {
					throw new PhrescoException(v.getMessage());
				}
			}

			FileDimensionValidator fileDimensionValidator = new FileDimensionValidator(
					localDirectories);
			validationManager.addValidator(fileDimensionValidator);
			List<ValidationStatus> validationStatusFileDimList = validationManager
					.validate();

			for (ValidationStatus v : validationStatusFileDimList) {
				System.out.println(v.getMessage());
				//log.info(v.getMessage());
			}
			for (ValidationStatus v : validationStatusFileDimList) {
				// must throw exception if folder does not exist otherwise next
				// validation will not work
				if (v.isStatus() == false) {
					throw new PhrescoException(v.getMessage());
				}
			}
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
			//log.info(concatenatedStatusMessage);
		} catch (ZipException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}

	}

	public void themeConvertor(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		//log.info("Theme conversion is being done");

	}

	public void contentValidator(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		//log.info("Content validation is being done");
		try {
			LocaleExtractor localeExtractor = new LocaleExtractor(
					mavenProjectInfo, File.separator + "manifest.xml",
					mavenProjectInfo.getProject().getProperties()
							.getProperty("phresco.content.target.dir"));
			List<String> localDirectories = localeExtractor
					.getLocaleDirectories();

			ValidationManager validationManager = new ValidationManager(
					mavenProjectInfo, File.separator + "manifest.xml",
					mavenProjectInfo.getProject().getProperties()
							.getProperty("phresco.content.target.dir"));

			DirectoryValidator directoryValidator = new DirectoryValidator(
					localDirectories);
			validationManager.addValidator(directoryValidator);
			List<ValidationStatus> validationStatusDirectoryList = validationManager
					.validate();
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
			List<ValidationStatus> validationStatusFileList = validationManager
					.validate();

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

			FileDimensionValidator fileDimensionValidator = new FileDimensionValidator(
					localDirectories);
			validationManager.addValidator(fileDimensionValidator);
			List<ValidationStatus> validationStatusFileDimList = validationManager
					.validate();

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

	public void contentConvertor(MavenProjectInfo mavenProjectInfo)
			throws PhrescoException {
		//log.info("Content conversion is being done");

	}

	public void pack(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		themeValidator(mavenProjectInfo);
		themeConvertor(mavenProjectInfo);
		contentValidator(mavenProjectInfo);
		contentConvertor(mavenProjectInfo);
		super.pack(configuration, mavenProjectInfo);
	}

}
