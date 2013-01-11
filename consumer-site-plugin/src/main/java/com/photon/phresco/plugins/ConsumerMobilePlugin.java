package com.photon.phresco.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.manager.ConversionManager;
import com.photon.phresco.manager.ValidationManager;
import com.photon.phresco.parser.LocaleExtractor;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.drupal.DrupalPlugin;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.status.ValidationStatus;
import com.photon.phresco.validator.DirectoryValidator;
import com.photon.phresco.validator.FileDimensionValidator;
import com.photon.phresco.validator.FileValidator;
import com.photon.phresco.vo.CsvFileVO;

public class ConsumerMobilePlugin extends DrupalPlugin {

	public ConsumerMobilePlugin(Log log) {
		super(log);
	}

	public void themeValidator(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		log.info("Theme validation is being done");
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
			log.info("Dumping messages after folder validation");
			for (ValidationStatus v : validationStatusDirectoryList) {
				log.info(v.getMessage());
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
			log.info("Dumping messages after file validation");
			for (ValidationStatus v : validationStatusFileList) {
				log.info(v.getMessage());
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
			log.info("Dumping messages after image dimension validation");
			for (ValidationStatus v : validationStatusFileDimList) {
				log.info(v.getMessage());
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
			log.info("---- CONSOLIDATED MESSAGES-------");
			for (ValidationStatus v : validationStatusList) {
				concatenatedStatusMessage += v.getMessage();
			}
			log.info(concatenatedStatusMessage);
		} catch (ZipException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}

	}

	public void themeConvertor(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// log.info("Theme conversion is being done");

	}

	public void contentValidator(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		log.info("Content validation is being done");
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
			log.info("Dumping messages after folder validation");
			for (ValidationStatus v : validationStatusDirectoryList) {
				log.info(v.getMessage());
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
			log.info("Dumping messages after file valdiation");
			for (ValidationStatus v : validationStatusFileList) {

				log.info(v.getMessage());
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
			log.info("Dumping messages after image file dimension valdiation");
			for (ValidationStatus v : validationStatusFileDimList) {
				log.info(v.getMessage());
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
			log.info("---- CONSOLIDATED MESSAGES-------");
			List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
			validationStatusList.addAll(validationStatusDirectoryList);
			validationStatusList.addAll(validationStatusFileList);
			validationStatusList.addAll(validationStatusFileDimList);
			String concatenatedStatusMessage = "";
			for (ValidationStatus v : validationStatusList) {
				concatenatedStatusMessage += v.getMessage();
			}
			log.info(concatenatedStatusMessage);
		} catch (ZipException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	public void contentConvertor(MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		log.info("Content conversion is being done");
		try {
			ConversionManager conversionManager = new ConversionManager(mavenProjectInfo, File.separator
					+ "manifest.xml", mavenProjectInfo.getProject().getProperties()
					.getProperty("phresco.content.target.dir"));
			List<CsvFileVO> csvoFileList = conversionManager.convert(mavenProjectInfo);
			log.info(mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.php.file.name")
					+ " created at location" + "."
					+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.php.deploy.dir"));
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
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec("drush si -y standard --account-name=admin --account-pass=admin --account-mail=admin@mobilityfwk.com --db-url=mysql://root:root@localhost/drushdru --site-mail=noreply@mobilityfwk.com --site-name=Mobility-Phase2-Enhancements");
			rt.exec("drush en -y corolla");
			rt.exec("drush vset theme_default corolla");
			rt.exec("drush en -y php translation restapi framework_1_5 mail mobile_productlocator mobile_eretailer mobile_bazaar_voice social_share googleanalytics google_appliance webform jnj_site_build");
			rt.exec("drush vset site_frontpage mobile-home");
			rt.exec("drush sqlc < {source}\\sql\\mysql\\5.0\\site.sql");
			rt.exec("drush scr {source}\\sites\\all\\modules\\jnj_site_build\\build\\scripts\\consumer-mobile-v1.build");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
