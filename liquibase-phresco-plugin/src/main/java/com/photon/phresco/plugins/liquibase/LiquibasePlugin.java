package com.photon.phresco.plugins.liquibase;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.logging.Log;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.impl.ConfigManagerImpl;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.LiquibaseConstants;
import com.photon.phresco.plugins.api.ExecutionStatus;
import com.photon.phresco.plugins.impl.AbstractPhrescoPlugin;
import com.photon.phresco.plugins.impl.DefaultExecutionStatus;
import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import liquibase.integration.commandline.Main;


public class LiquibasePlugin extends AbstractPhrescoPlugin implements LiquibaseConstants{

	Log log;
	private File baseDir;

	public LiquibasePlugin(Log log) {
		super();
		this.log=log;
	}

	private String getDriverName(String dbtype) {
		if(ORACLE.equalsIgnoreCase(dbtype)){
			return ORACLE_DRIVER;
		}else if(MYSQL.equalsIgnoreCase(dbtype)){
			return MYSQL_DRIVER;
		}else{
			return null;
		}
	}

	private String dbURLConstruction(String url,String port,String dbname,String dbtype){

		if(ORACLE.equalsIgnoreCase(dbtype)){
			return "jdbc:oracle:thin:@"+url+":"+port+":"+dbname;

		}else if(MYSQL.equalsIgnoreCase(dbtype)){
			return "jdbc:mysql://"+url+":"+port+"/"+dbname;
		}else{
			return null;
		}

	}

	public ExecutionStatus liquibaseDiff(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {

		try {
			baseDir = mavenProjectInfo.getBaseDir();
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase diff command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			liquibaseargs.add(LIQUIBASE_DRIVER+getDriverName(configs.get(DBTYPE)));			
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(SOURCE_HOST),configs.get(SOURCE_PORT),configs.get(SOURCE_DBNAME),configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(SOURCE_USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(SOURCE_PASSWORD));			
			liquibaseargs.add(DIFF_CMD);			
			liquibaseargs.add(REFERENCE_URL+dbURLConstruction(configs.get(REFERENCE_HOST),configs.get(REFERENCE_PORT),configs.get(REFERENCE_DBNAME),configs.get(DBTYPE)));
			liquibaseargs.add(REFERENCE_USERNAME+configs.get(REFERENCEUSERNAME));
			liquibaseargs.add(REFERENCE_PASSWORD+configs.get(REFERENCEPASSWORD));	
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);
		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase diff functionality");
		}
		return new DefaultExecutionStatus();
	}

	public ExecutionStatus liquibaseStatus(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			baseDir = mavenProjectInfo.getBaseDir();
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase status command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String changelogPath=configs.get(CHANGELOG_PATH_FOR_STATUS);
			liquibaseargs.add(LIQUIBASE_CHANGELOG+changelogPath);
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(HOST),configs.get(PORT),configs.get(DBNAME),configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(PASSWORD));
			liquibaseargs.add(STATUS_CMD);				
			if(Boolean.valueOf(configs.get(VERBOSE))){
				liquibaseargs.add(LIQUIBASE_VERBOSE);
			}
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);
		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase status functionality");
		}
		return new DefaultExecutionStatus();
	}

	public com.photon.phresco.configuration.Configuration getConfiguration(String environmentName) throws PhrescoException {
		try {
			ConfigManager configManager = null;
			configManager = new ConfigManagerImpl(new File(baseDir.getPath() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + Constants.CONFIGURATION_INFO_FILE));
			List<com.photon.phresco.configuration.Configuration> configurations = configManager.getConfigurations(environmentName,Constants.SETTINGS_TEMPLATE_DB);
			if (CollectionUtils.isNotEmpty(configurations)) {
				return configurations.get(0);
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return null;
	}

	public ExecutionStatus liquibaseSelectedRollback(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			baseDir = mavenProjectInfo.getBaseDir();
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase Rollback Count command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);				
			String changelogPath=configs.get(CHANGELOG_PATH_FOR_ROLLBACKCOUNT);
			liquibaseargs.add(LIQUIBASE_CHANGELOG+changelogPath);
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(HOST),configs.get(PORT),configs.get(DBNAME),configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(PASSWORD));
			liquibaseargs.add(ROLLBACK_COUNT);	
			liquibaseargs.add(configs.get(ROLLBACK_COUNT_VALUE));				
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);

		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase selected rollback functionality");
		}
		return new DefaultExecutionStatus();
	}
	public ExecutionStatus liquibaseRollbackToDate(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			baseDir = mavenProjectInfo.getBaseDir();
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase RollbackToDate command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);

			String changelogpath=configs.get(CHANGELOG_PATH_FOR_ROLLBACKDATE);
			liquibaseargs.add(LIQUIBASE_CHANGELOG+changelogpath);
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(HOST),configs.get(PORT),configs.get(DBNAME),configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(PASSWORD));
			liquibaseargs.add(ROLLBACK_TO_DATE);	
			liquibaseargs.add(configs.get(ROLLBACK_DATE));				
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);
		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase rollback to date functionality");
		} 
		return new DefaultExecutionStatus();
	}
	public ExecutionStatus liquibaseRollbackToTag(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			baseDir = mavenProjectInfo.getBaseDir();
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase RollbackTag command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			String changelogpath=configs.get(CHANGELOG_PATH_FOR_ROLLBACKTAG);
			liquibaseargs.add(LIQUIBASE_CHANGELOG+changelogpath);
			liquibaseargs.add(LIQUIBASE_DRIVER+getDriverName(configs.get(DBTYPE)));			
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(HOST),configs.get(PORT),configs.get(DBNAME),configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(PASSWORD));
			liquibaseargs.add(ROLLBACK);
			liquibaseargs.add(configs.get(TAG));
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);
		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase rollback to tag functionality");
		} 
		return new DefaultExecutionStatus();
	}
	
	public ExecutionStatus liquibaseTag(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			baseDir = mavenProjectInfo.getBaseDir();
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase Tag command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			liquibaseargs.add(LIQUIBASE_DRIVER+getDriverName(configs.get(DBTYPE)));			
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(HOST),configs.get(PORT),configs.get(DBNAME),configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(PASSWORD));
			liquibaseargs.add(TAG);
			liquibaseargs.add(configs.get(DBTAG));
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);
		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase tag functionality");
		}
		return new DefaultExecutionStatus();
	}

	public ExecutionStatus liquibaseDbDoc(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase dbdoc command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(PASSWORD));
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(HOST), configs.get(PORT), configs.get(DBNAME), configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_CHANGELOG+configs.get(CHANGELOG_FILE_FOR_DBDOC));
			liquibaseargs.add(DBDOC_CMD);
			liquibaseargs.add(PATH_TO_DBDOC);
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);
		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase dbdoc functionality");
		} 
		return new DefaultExecutionStatus();
	}

	public ExecutionStatus liquibaseUpdate(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase update command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(PASSWORD));
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(HOST), configs.get(PORT), configs.get(DBNAME), configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_CHANGELOG+PATH_TO_UPDATE_XML);
			liquibaseargs.add(UPDATE_CMD);
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);
		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase update functionality");
		} 
		return new DefaultExecutionStatus();
	}

	public ExecutionStatus liquibaseInstall(Configuration configuration, MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		try {
			List<String> liquibaseargs=new ArrayList<String>();
			log.info("******* Invoking liquibase install command *******");
			Map<String, String> configs = MojoUtil.getAllValues(configuration);
			liquibaseargs=new ArrayList<String>();
			liquibaseargs.add(LIQUIBASE_USERNAME+configs.get(USERNAME));
			liquibaseargs.add(LIQUIBASE_PASSWORD+configs.get(PASSWORD));
			liquibaseargs.add(LIQUIBASE_URL+dbURLConstruction(configs.get(HOST), configs.get(PORT), configs.get(DBNAME), configs.get(DBTYPE)));
			liquibaseargs.add(LIQUIBASE_CHANGELOG+configs.get(INSTALL));
			liquibaseargs.add(UPDATE_CMD);
			String liquibasearguments[]=liquibaseargs.toArray(new String[liquibaseargs.size()]);
			Main.main(liquibasearguments);
		} catch (Exception e) {
			throw new PhrescoException("Exception occured in liquibase install functionality");
		} 
		return new DefaultExecutionStatus();
	}

	@Override
	public ExecutionStatus runIntegrationTest(Configuration configuration,
			MavenProjectInfo mavenProjectInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		return null;
	}
}