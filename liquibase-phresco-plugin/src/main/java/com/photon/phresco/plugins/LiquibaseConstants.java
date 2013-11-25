package com.photon.phresco.plugins;

import com.photon.phresco.plugin.commons.PluginConstants;

public interface LiquibaseConstants extends PluginConstants {
	
	
	String USERNAME="username";
	String PASSWORD="password";
	String VERBOSE="verbose";
	String DBTYPE="type";
	String LIQUIBASE_DRIVER="--driver=";
	String LIQUIBASE_USERNAME="--username=";
	String LIQUIBASE_PASSWORD="--password=";
	String LIQUIBASE_VERBOSE="--verbose";
	String LIQUIBASE_URL="--url=";
	String LIQUIBASE_CHANGELOG="--changeLogFile=";
	String HOST="host";
	String PORT="port";
	String DBNAME="dbname";
	String OUTPUTDIR = "outputDir";
	String CHANGELOG_FILE_FOR_DBDOC="changelogFileForDbDoc";
	String INSTALL="install";
	String REFERENCE_USERNAME="--referenceUsername=";
	String REFERENCE_PASSWORD="--referencePassword=";
	String REFERENCE_URL="--referenceUrl=";
	String CHANGELOG_PATH_FOR_STATUS="changeLogPathForStatus";
	String CHANGELOG_PATH_FOR_ROLLBACKCOUNT="changeLogPathForRollbackCount";
	String CHANGELOG_PATH_FOR_ROLLBACKDATE="changeLogPathForRollbackDate";
	String CHANGELOG_PATH_FOR_ROLLBACKTAG="changeLogPathForRollbackTag";
	String ROLLBACK_COUNT="rollbackCount";
	String ROLLBACK_COUNT_VALUE="rollbackCountValue";
	String ROLLBACK_TO_DATE="rollbackToDate";
	String ROLLBACK_DATE="rollbackDate";
	String ROLLBACK="rollback";
	String TAG="tag";
	String DBTAG="dbtag";
    String LIQUIBASE_UPDATE="liquibase:update";
    String LIQUIBASE_ROLLBACK="liquibase:rollback";
    String LIQUIBASE_STATUS="liquibase:status";
    String LIQUIBASE_BAT_COMMAND="";
    String SOURCE_USERNAME = "sourceUsername";
    String REFERENCEUSERNAME="referenceUsername";
	String SOURCE_PASSWORD = "sourcePassword";
	String REFERENCEPASSWORD="referencePassword";
	String SOURCE_HOST = "sourceHost";
	String REFERENCE_HOST = "referenceHost";
	String SOURCE_PORT = "sourcePort";
	String REFERENCE_PORT = "referencePort";
	String SOURCE_DBNAME = "sourceDbName";
	String REFERENCE_DBNAME = "referenceDbName";
    
    String STATUS_CMD="status";
    String DBDOC_CMD="dbDoc";
    String UPDATE_CMD="update";
    String DIFF_CMD="diff";
    
    String DATABASE ="Database";
    
    String ENVIRONMENTNAME="environmentName";
    
    String ORACLE="oracle";
    String ORACLE_DRIVER="oracle.jdbc.OracleDriver";
    String MYSQL="mysql";
    String MYSQL_DRIVER="com.mysql.jdbc.Driver";
    
    String PATH_TO_DBDOC="docs\\liquibase\\";
    String PATH_TO_UPDATE_XML="src/main/resources/liquibase/update.xml";
    String PATH_TO_INSTALL_XML="src/main/resources/liquibase/install.xml";
   /* liquibase 
	--driver="oracle.jdbc.OracleDriver" 
	--url="jdbc:oracle:thin:@172.16.17.228:1521:orcl" 
	--username=lb_test
	--password=lb_test 
	diff
	--referenceUrl="jdbc:oracle:thin:@172.16.17.228:1521:orcl"
	--referenceUsername=lb_solo
	--referencePassword=lb_solo*/

	
	
	
	
}