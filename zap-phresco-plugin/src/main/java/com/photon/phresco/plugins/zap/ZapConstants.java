package com.photon.phresco.plugins.zap;

public interface ZapConstants {
	
	/**
     * Zap configurations
     * 
     */
   
	String URL = "url";
	String ENVIRONMENT_NAME = "environmentName";
	String PROTOCOL = "protocol";
	String HOST = "host";
	String PORT = "port";
	String ZAPDIR = "zapDir";
	String RECURSE = "recurse";
	String INSCOPE_ONLY= "inScopeOnly";
	String TRUE = "true";
	String NO = "no";
	String ASCAN = "ascan";
	String SCAN = "scan";
	String SPIDER = "spider";
	String ACTION = "action";
	String ZAP_REPORT = "zapreport";
	String REPORT_FILE = "report.xml";
	String TARGET = "target";
	String SPACE = " ";
	String MAVEN = "mvn";
	String XML = "XML";
	String JSON = "JSON";
	String PHRESCO_POM = "phresco-pom.xml";
	String VALIDATE = "validate";
	String HYPEN_F = "-f";
	String ZAP_PROFILE = "-Pzap";
	String CONFIG_FILE = "phresco-env-config.xml";
	String SERVER = "Server";
	String COLON = ":";
	String DOUBLE_SLASH = "//";
	String SLASH = "/";
	String OUTPUT_FORMAT = "action/scan/?zapapiformat=XML";
	String EQUAL = "=";
	String AMPERSAND = "&";
	String APPLICATION_JSON = "application/json";
	String APPLICATION_XML = "application/xml";
	String SHUTDOWN = "shutdown";
	String DOT_PHRESCO_FOLDER     = ".phresco";
	String DO_NOT_CHECKIN_FOLDER  = "do_not_checkin";
	String OTHERS = "OTHER";
	String CORE = "core";
	String OTHER = "other";
	String REPORT = "xmlreport/";
	String TYPE = "type";
	String HEAD = "HEAD";
	String SERVER_ERROR = "server not started";
	String APPLICATION_JSON_FORMAT= "zapapiformat=JSON";
	String APPLICATION_XML_FORMAT = "zapapiformat=XML";
	String QUESTION_MARK = "?";
    String ZAP_JAR="zap.jar";
    String INVALID_ZAP_DIR = "Invalid Zap Directory";
    String CONFIG_FILE_NOT_FOUND_ERROR = "Config File does not exists";
    String REPORT_FAIL = "Report Generate Failed";
    String ZAP_PORT = "zapPort";
    String MAVEN_PARAMETER = "-D";
    String ANT_RUN = "antrun:run";
    String START_ZAP_TARGET = "-Dtarget.name=startZapDaemon";
    String HEAD_REVISION = "HEAD";
    String ZAP_WINDOWS_BATCH_COMMAND = "zap.bat";
    String ZAP_MAC_BATCH_COMMAND = "sh zap.sh";
    String NEW_SESSION = "New Session";
    String SPIDER_COMPLETED = "Spider scanning complete: true";
    String ACTIVE_SCAN_COMPLETED = "scanner completed";
    String ZAP_NOT_STARTED_IN_REMOTE = "Zap Not started in Remote";
    String REMOTE = "remote";
}
