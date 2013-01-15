package com.photon.phresco.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.convertor.ThemeConvertor;
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
		try {
			new ThemeConvertor().convert(mavenProjectInfo);
		} catch (Exception e) {
			throw new PhrescoException(e.getMessage());
		}
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

	public void runDrushCommands() throws IOException {
		
		Executor exec = new DefaultExecutor();
		CommandLine cl = new CommandLine(
				"drush si -y standard --account-name=admin --account-pass=admin --account-mail=admin@mobilityfwk.com --db-url=mysql://root:root@localhost/drushdru --site-mail=noreply@mobilityfwk.com --site-name=Mobility-Phase2-Enhancements");
		int exitvalue = exec.execute(cl);

		exec = new DefaultExecutor();
		cl = new CommandLine("drush en -y corolla");
		exitvalue = exec.execute(cl);

		exec = new DefaultExecutor();
		cl = new CommandLine("drush vset theme_default corolla");
		exitvalue = exec.execute(cl);

		exec = new DefaultExecutor();
		cl = new CommandLine(
				"drush en -y php translation restapi framework_1_5 mail mobile_productlocator mobile_eretailer mobile_bazaar_voice social_share googleanalytics google_appliance webform jnj_site_build");
		exitvalue = exec.execute(cl);

		exec = new DefaultExecutor();
		cl = new CommandLine("drush vset site_frontpage mobile-home");
		exitvalue = exec.execute(cl);

		exec = new DefaultExecutor();
		cl = new CommandLine("drush sqlc < {source}" + File.separator + "sql" + File.separator + "mysql"
				+ File.separator + "5.0" + File.separator + "site.sql");
		exitvalue = exec.execute(cl);

		exec = new DefaultExecutor();
		cl = new CommandLine("drush scr {source}" + File.separator + "sites" + File.separator + "all" + File.separator
				+ "modules" + File.separator + "jnj_site_build" + File.separator + "build" + File.separator + "scripts"
				+ File.separator + "consumer-mobile-v1.build");
		exitvalue = exec.execute(cl);
	}

	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			themeValidator(mavenProjectInfo);
			themeConvertor(mavenProjectInfo);
			contentValidator(mavenProjectInfo);
			contentConvertor(mavenProjectInfo);
			super.pack(configuration, mavenProjectInfo);
			runDrushCommands();
		} catch (IOException e) {
			log.info(e.getMessage());
			throw new PhrescoException(e);
		}
	}
}
