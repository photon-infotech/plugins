package com.photon.phresco.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.photon.phresco.api.ApplicationProcessor;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ArtifactGroup;
import com.photon.phresco.configuration.Configuration;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class JnJApplicationProcessor implements ApplicationProcessor{
	
	private static final String VARIABLE_FIELD = "variable";
	private static final String END_MODULE_END_TAG = "' ends";
	private static final String START_MODULE_END_TAG = "' starts";
	private static final String MODULE_START_TAG = "-- '";
	private static final String DOUBLE_HYPHEN = "--";
	private static final String VALUE_FIELD = "value";
	private static final String NAME_FIELD = "name";
	private static final String SQL_VARIABLE_SEP = "`,`";
	private static final String SQL_VALUE_SEP = "','";
	private static final String SINGLE_QUOTE = "'";
	private static final String LINE_BREAK = "\n";
	private static final String EQUAL = "=";
	private static final String CONFIGURATION = "configuration-";
	private static final String DOT_SQL = ".sql";
	private static final String MYSQL = "mysql";
	private static final String NAME = NAME_FIELD;
	private static final String CONFIG_TAG = "configuration";
	private static final String CONFIG_XPATH_END_TAG = "']";
	private static final String CONFIG_XPATH = "//configurations/configuration[@name='";
	private static final String SEMI_COLON = ";";
	private static final String VARIABLE_END_TAG = "`)";
	private static final String VARIABLE_START_TAG = " (`";
	private static final String VALUES_END_TAG = "');";
	private static final String VALUES_START_TAG = " VALUES ('";
	private static final String INSERT_INTO = "INSERT INTO ";
	private static final String DELETE_FROM = "DELETE FROM ";
	private static final String WHERE = " WHERE ";
	private static final String DEFAULT_VALUE = "defaultValue";
	private static final String VARIABLE_NAME = "variableName";
	private static final String TABLE_NAME = "tableName";
	private static final String XML = "feature-manifest.xml";
	private static final String FEATURES = "features";

	@Override
    public List<Configuration> preConfiguration(ApplicationInfo appInfo, String featureName, String envName) throws PhrescoException {
        File featureManifest = new File(Utility.getProjectHome() + appInfo.getAppDirName() + getThirdPartyFolder(appInfo) + File.separator + featureName + File.separator + XML);
		List<Configuration> configs = getConfigObjFromXml(featureManifest.getPath());

		return configs;
    }
    
	@Override
	public void postConfiguration(ApplicationInfo appInfo, List<Configuration> configurations)
			throws PhrescoException {
		String envName = configurations.get(0).getEnvName();
		String featureName = configurations.get(0).getProperties().getProperty("featureName");
		String propertyValue = getPropertyValue(appInfo, Constants.POM_PROP_KEY_SQL_FILE_DIR);
		File featureManifest = new File(Utility.getProjectHome() + appInfo.getAppDirName() + getThirdPartyFolder(appInfo) + File.separator + featureName + File.separator + XML);
		File featureSqlDir = new File(Utility.getProjectHome() + appInfo.getAppDirName() + propertyValue);
		storeConfigObj(configurations, featureManifest, featureSqlDir, envName);
	}

	@Override
	public List<Configuration> preFeatureConfiguration(ApplicationInfo appInfo,
			String featureName) throws PhrescoException {
		File featureManifest = new File(Utility.getProjectHome() + appInfo.getAppDirName() + getThirdPartyFolder(appInfo) + File.separator + featureName + File.separator + XML);
		List<Configuration> configs = getConfigObjFromXml(featureManifest.getPath());

		return configs;
	}
	
	@Override
	public void postFeatureConfiguration(ApplicationInfo appInfo, 
			List<Configuration> configs, String featureName)
			throws PhrescoException {
		try {
			String propertyValue = getPropertyValue(appInfo, Constants.POM_PROP_KEY_SQL_FILE_DIR);
			File featureManifest = new File(Utility.getProjectHome() + appInfo.getAppDirName() + getThirdPartyFolder(appInfo) + File.separator + featureName + File.separator + XML);
			File featureSqlDir = new File(Utility.getProjectHome() + appInfo.getAppDirName() + propertyValue);
			storeConfigObj(configs, featureManifest, featureSqlDir, "Production");
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	public List<Configuration> getConfigObjFromXml(String featureManifestXml) throws PhrescoException {
		List<Configuration> configs = new ArrayList<Configuration>();
		try {
			File featureManifestXmlFile = new File(featureManifestXml);
			
	        Configuration config = null;
	        if (featureManifestXmlFile.isFile()) {
	            config = new Configuration(featureManifestXmlFile.getName(), FEATURES);
	        } else {
	            return Collections.emptyList();
	        }
	        
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(featureManifestXmlFile);
			doc.getDocumentElement().normalize();
	 
			NodeList nList = doc.getElementsByTagName(CONFIG_TAG);
			Properties properties = new Properties();
			for (int temp = 0; temp < nList.getLength(); temp++) {
			   Node nNode = nList.item(temp);
			   // get attributes
			   if (nNode.hasAttributes()) {
				   NamedNodeMap attributes = nNode.getAttributes();
				   Node name = attributes.getNamedItem(NAME);
				   if (name != null) {
					   Element eElement = (Element) nNode;
					   String defaultValue = getTagValue(DEFAULT_VALUE, eElement);
					   properties.put(name.getNodeValue(), defaultValue);
				   }
			   }
			}
	        
	        config.setProperties(properties);
	        configs.add(config);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return configs;
	}

	private static String getTagValue(String sTag, Element eElement) {
		String tagValue = "";
		NodeList elementsByTagName = eElement.getElementsByTagName(sTag);
		if (elementsByTagName != null) {
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
			if (nlList.getLength() > 0) {
			    Node nValue = (Node) nlList.item(0);
			    if (nValue != null) {
			    	tagValue = nValue.getNodeValue();
			    }
			}
		}
		
		return tagValue;
	  }
	
	private String getThirdPartyFolder(ApplicationInfo appInfo) throws PhrescoException { 
		File pomPath = new File(Utility.getProjectHome() + appInfo.getAppDirName() + File.separator + Constants.POM_NAME);
		try {
			PomProcessor processor = new PomProcessor(pomPath);
			String property = processor.getProperty(Constants.POM_PROP_KEY_MODULE_SOURCE_DIR);
			if(StringUtils.isNotEmpty(property)) {
				return property;
			}
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
		
		return "";
	}
	
	private void storeConfigObj(List<Configuration> configs, File featureManifestXmlFile, File featureSqlDir, String environmentName) throws PhrescoException { 
		try {
			if (!featureManifestXmlFile.isFile()) {
				throw new PhrescoException("manifest file is not available ");
			}
			
			// Document
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(featureManifestXmlFile);
			doc.getDocumentElement().normalize();
			
			// xpath
			XPathFactory factory= XPathFactory.newInstance();
            XPath xPathInstance = factory.newXPath();
            
			for (Configuration configuration : configs) {
			    Properties properties = configuration.getProperties();
			    Enumeration em = properties.keys();
			    while (em.hasMoreElements()) {
					String insertQuery = "";
					String insertFieldQuery = "";

					String deleteQuery = "";
					String deleteFieldQuery = "";
					
					String tableName = "";
					String variableName = "";
					String dafaultValue = "";
					
					String constructedQuery = "";
			        String key = (String) em.nextElement();
			        Object object = properties.get(key);
			        
			        // get config object for this key
			        String xPathQuery= CONFIG_XPATH + key + CONFIG_XPATH_END_TAG;	
			        XPathExpression xPathExpression = xPathInstance.compile(xPathQuery);
			        //evalute the xpath query in the entire xml document and define the return type
		            Object results = xPathExpression.evaluate(doc, XPathConstants.NODESET);
		            NodeList nList = (NodeList) results;
		            
		            // config objects
		            for (int i = 0; i < nList.getLength(); i++) {
		                Node nNode = nList.item(i);
		                // get config object values
		 			   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 				  // getting child nodes to construct query
		 				  NodeList childNodes = nNode.getChildNodes();
						   for (int temp1 = 0; temp1 < childNodes.getLength(); temp1++) {
							   Node childNode = childNodes.item(temp1);
							   if (childNode.getNodeType() == Node.ELEMENT_NODE) {
								   if (TABLE_NAME.equals(childNode.getNodeName())) {
									   tableName = childNode.getTextContent();
									   if (!VARIABLE_FIELD.equals(tableName)) {
										   
										   return;
									   }
									   deleteQuery = deleteQuery + DELETE_FROM + childNode.getTextContent() + WHERE + NAME_FIELD + EQUAL;
									   insertQuery = insertQuery + INSERT_INTO + childNode.getTextContent() + VARIABLE_START_TAG + NAME_FIELD + SQL_VARIABLE_SEP + VALUE_FIELD + VARIABLE_END_TAG + VALUES_START_TAG;
							        } else if (VARIABLE_NAME.equals(childNode.getNodeName())) {
							        	variableName = childNode.getTextContent();
							        	deleteFieldQuery = SINGLE_QUOTE + childNode.getTextContent() + SINGLE_QUOTE + SEMI_COLON + LINE_BREAK;
							        } else if (DEFAULT_VALUE.equals(childNode.getNodeName())) {
							        	dafaultValue = object.toString();
							        }
								   	insertFieldQuery = variableName + SQL_VALUE_SEP + dafaultValue + VALUES_END_TAG;
							   }
						   }
		 			   }
		            }
		            
			        constructedQuery = deleteQuery + deleteFieldQuery + insertQuery + insertFieldQuery ;
			        
					List<File> sqlFolders = getSqlFolders(featureSqlDir);
					for (File sqlFolder : sqlFolders) {
						replaceSqlBlock(sqlFolder, CONFIGURATION + environmentName + DOT_SQL, key, constructedQuery);
					}
			    }
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	public List<File> getSqlFolders(File sqlFolder) throws PhrescoException {
		List<File> sqlFolders = null;
		try {
			FilenameFilter mysqlDirectoryFilter = new FilenameFilter() {
		        public boolean accept(File directory, String fileName) {
		        	return directory.isDirectory() && MYSQL.equalsIgnoreCase(fileName);
		        }
		    };
			
			FileFilter directoryFilter = new FileFilter() {
				public boolean accept(File directory) {
					return directory.isDirectory();
				}
			};
			
			File[] dirs = sqlFolder.listFiles(mysqlDirectoryFilter);
			for (File dir : dirs) {
				File[] versionFiles = dir.listFiles(directoryFilter);
				sqlFolders = Arrays.asList(versionFiles);
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return sqlFolders;
	}
	
	public void replaceSqlBlock(File versionFile, String fileName, String moduleName, String queryString) throws PhrescoException, IOException {
		BufferedReader buff = null;
		try {
			File scriptFile = new File(versionFile + File.separator + fileName);
			StringBuffer sb = new StringBuffer();
			if (scriptFile.isFile()) {
				// if script file is available need to replace the content
				buff = new BufferedReader(new FileReader(scriptFile));
				String readBuff = buff.readLine();
	            String sectionStarts = MODULE_START_TAG + moduleName + START_MODULE_END_TAG;
	            String sectionEnds = MODULE_START_TAG + moduleName + END_MODULE_END_TAG;
	            
	            while (readBuff != null) {
	            	sb.append(readBuff);
	            	sb.append(LINE_BREAK);
	                readBuff = buff.readLine();
	            }
	            
	            int cnt1 = sb.indexOf(sectionStarts);
	            int cnt2 = sb.indexOf(sectionEnds);
	            if (cnt1 != -1 || cnt2 != -1) {
	            	sb.replace(cnt1 + sectionStarts.length(), cnt2, LINE_BREAK + queryString + LINE_BREAK);
	            } else {
	            	// if this module is not added already in the file and need to add this config alone
					sb.append(LINE_BREAK + DOUBLE_HYPHEN + LINE_BREAK);
					sb.append(MODULE_START_TAG + moduleName + START_MODULE_END_TAG + LINE_BREAK);
					sb.append(queryString);
					sb.append(LINE_BREAK);
					sb.append(MODULE_START_TAG + moduleName + END_MODULE_END_TAG + LINE_BREAK);
					sb.append(DOUBLE_HYPHEN + LINE_BREAK);
	            }
	            
			} else {
            // else construct the format and write
				// query string buffer
				sb.append("CREATE TABLE IF NOT EXISTS `variable` (`name` varchar(128) NOT NULL DEFAULT '' COMMENT 'The name of the variable.', `value` longblob NOT NULL COMMENT 'The value of the variable.', PRIMARY KEY (`name`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Named variable/value pairs created by Drupal core or any...';" + LINE_BREAK);
				sb.append(DOUBLE_HYPHEN + LINE_BREAK);
				sb.append(MODULE_START_TAG + moduleName + START_MODULE_END_TAG + LINE_BREAK);
				sb.append(queryString);
				sb.append(LINE_BREAK);
				sb.append(MODULE_START_TAG + moduleName + END_MODULE_END_TAG + LINE_BREAK);
				sb.append(DOUBLE_HYPHEN + LINE_BREAK);
			}
			
            FileUtils.writeStringToFile(scriptFile, sb.toString());
		} catch (Exception e) {
			throw new PhrescoException(e);
		} finally {
			if (buff != null) {
				buff.close();
			}
        }
	}
	
	private String getPropertyValue(ApplicationInfo appInfo, String propertyKey) throws PhrescoException { 
		File pomPath = new File(Utility.getProjectHome() + appInfo.getAppDirName() + File.separator + Constants.POM_NAME);
		try {
			PomProcessor processor = new PomProcessor(pomPath);
			String property = processor.getProperty(propertyKey);
			if(StringUtils.isNotEmpty(property)) {
				return property;
			}
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
		
		return "";
	}
	
	@Override
	public void preBuild(ApplicationInfo appInfo) throws PhrescoException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void postBuild(ApplicationInfo appInfo) throws PhrescoException {
		// TODO Auto-generated method stub
	}

	public void postUpdate(ApplicationInfo arg0, List<ArtifactGroup> arg1)
			throws PhrescoException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void preCreate(ApplicationInfo appInfo) throws PhrescoException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preUpdate(ApplicationInfo appInfo) throws PhrescoException {
		// TODO Auto-generated method stub
	}

	@Override
	public void postCreate(ApplicationInfo appInfo) throws PhrescoException {
		// TODO Auto-generated method stub
	}
	
	private void updateDrupalVersion(File path, ApplicationInfo info) throws PhrescoException {
		// TODO Auto-generated method stub
	}
	
	private void excludeModule(ApplicationInfo appInfo, List<ArtifactGroup> artifactGroups) throws PhrescoException {
		// TODO Auto-generated method stub
	}

}
