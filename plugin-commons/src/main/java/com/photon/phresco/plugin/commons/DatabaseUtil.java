/*
 * ###
 * Phresco Commons
 * 
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ###
 */
package com.photon.phresco.plugin.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBDecoder;
import com.mongodb.DBObject;
import com.mongodb.DefaultDBDecoder;
import com.mongodb.Mongo;
import com.photon.phresco.api.ConfigManager;
import com.photon.phresco.configuration.Configuration;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class DatabaseUtil {

	private Map<String, String> dbDriverMap = new HashMap<String, String>(8);

	private String getDbDriver(String dbtype) {
		return dbDriverMap.get(dbtype);
	}

	private void initDriverMap() {
		dbDriverMap.put("mysql", "com.mysql.jdbc.Driver");
		dbDriverMap.put("oracle", "oracle.jdbc.OracleDriver");
		dbDriverMap.put("hsql", "org.hsql.jdbcDriver");
		dbDriverMap.put("mssql", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dbDriverMap.put("db2", "com.ibm.db2.jcc.DB2Driver");
		dbDriverMap.put("mongodb", "com.mongodb.DBConnector");
	}

	private void executeMongodb(File basedir, List<String> filepaths, String host, String port, String databaseName)
			throws PhrescoException {
		FileInputStream fis = null;
		try {
			int portNo = Integer.parseInt(port);
			Mongo mongo = new Mongo(host, portNo);
			DB db = mongo.getDB(databaseName);
			for (String bsonFile : filepaths) {
				File bsonfile = new File(basedir.getPath() + bsonFile);
				fis = new FileInputStream(bsonfile);
				String name = bsonfile.getName();
				String splitBsonFileName = SplitBsonFileName(name);
				DBDecoder decoder = new DefaultDBDecoder();
				while (fis.available() > 0) {
					DBObject dbObj = decoder.decode(fis, (DBCollection) null);
					DBCollection collection = db.getCollection(splitBsonFileName);
					collection.insert(dbObj);
				}
			}
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			Utility.closeStream(fis);
		}
	}

	private String SplitBsonFileName(String str) {
		String delimiter = "\\.";
		String[] temp;
		String collName = null;
		temp = str.split(delimiter, 2);
		for (int i = 0; i < temp.length;) {
			collName = temp[i];
			break;

		}
		return collName;
	}

	private void executeSql(Configuration dbConfiguration, File basedir, List<String> filepaths) throws PhrescoException {
		initDriverMap();
		String host = dbConfiguration.getProperties().getProperty(Constants.DB_HOST);
		String port = dbConfiguration.getProperties().getProperty(Constants.DB_PORT);
		String userName = dbConfiguration.getProperties().getProperty(Constants.DB_USERNAME);
		String password = dbConfiguration.getProperties().getProperty(Constants.DB_PASSWORD);
		String databaseName = dbConfiguration.getProperties().getProperty(Constants.DB_NAME);
		String databaseType = dbConfiguration.getProperties().getProperty(Constants.DB_TYPE).toLowerCase();
		String connectionProtocol = findConnectionProtocol(databaseType, host, port, databaseName);
		Connection con = null;
		FileInputStream fis = null;
		Statement st = null;
		try {
			if (databaseType.equals(Constants.MONGO_DB)) {
				executeMongodb(basedir, filepaths, host, port, databaseName);
				return;
			}
			Class.forName(getDbDriver(databaseType)).newInstance();
			con = DriverManager.getConnection(connectionProtocol, userName, password);
			con.setAutoCommit(false);
			for (String sqlFile : filepaths) {
				fis = new FileInputStream(basedir.getPath()+ sqlFile);
				Scanner s = new Scanner(fis);
				s.useDelimiter("(;(\r)?\n)|(--\n)");
				st = con.createStatement();
				while (s.hasNext()) {
					String line = s.next().trim();
					if (databaseType.equals(Constants.ORACLE_DB)) {
						if (line.startsWith("--")) {
							String comment = line.substring(line.indexOf("--"), line.lastIndexOf("--"));
							line = line.replace(comment, "");
							line = line.replace("--", "");
						}
						if (line.startsWith(Constants.REM_DELIMETER)) {
							String comment = line.substring(0, line.lastIndexOf("\n"));
							line = line.replace(comment, "");
						}
					}

					if (line.startsWith("/*!") && line.endsWith("*/")) {
						line = line.substring(line.indexOf("/*"), line.indexOf("*/") + 2);
					}

					if (line.trim().length() > 0) {
						st.execute(line);
					}
				}
			}
		} catch (SQLException e) {
			throw new PhrescoException(e);
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		} catch (InstantiationException e) {
			throw new PhrescoException(e);
		} catch (IllegalAccessException e) {
			throw new PhrescoException(e);
		} catch (ClassNotFoundException e) {
			throw new PhrescoException(e);
		} finally {
			Utility.closeStream(fis);
			try {
				if (con != null) {
					con.commit();
					con.close();
				}
			} catch (Exception e) {
				throw new PhrescoException(e);
			}
		}
	}

	public void  fetchSqlConfiguration(String sqlPath, Boolean importSql, File baseDir, String environmentName) throws PhrescoException {
		String dbType;
		String version;
		if (StringUtils.isEmpty(sqlPath)) {
			return ;
		}
		try {
			ConfigManager configManager = PhrescoFrameworkFactory.getConfigManager(new File(baseDir.getPath() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + Constants.CONFIGURATION_INFO_FILE));
			JsonParser parser = new JsonParser();
			JsonElement parse = parser.parse(sqlPath);
			JsonObject jsonObject = parse.getAsJsonObject();
			for ( Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				dbType = entry.getKey();
				JsonElement value = entry.getValue();
				JsonArray paths = value.getAsJsonArray();
				for (JsonElement jsonElement : paths) {
					version = jsonElement.getAsString();
					version = version.substring(version.indexOf(dbType), version.lastIndexOf("/"));
					version = (version.substring(version.indexOf("/")).substring(1));
					if (importSql) {
						List<com.photon.phresco.configuration.Configuration> configuration =configManager.getConfigurations(environmentName, Constants.SETTINGS_TEMPLATE_DB);
						for (com.photon.phresco.configuration.Configuration config : configuration) {
							String databaseType = config.getProperties().getProperty(Constants.DB_TYPE).toLowerCase();
							String databaseVersion = config.getProperties().getProperty(Constants.DB_VERSION);
							if (databaseType.equals(dbType) && databaseVersion.equals(version)) {
								getSqlFilePath(config, baseDir, databaseType, sqlPath);
							}
						}
					}
				}
			}
		} catch (PhrescoException e) {
			throw new PhrescoException(e);
		} catch (ConfigurationException e) {
			throw new PhrescoException(e);

		}
	}
	
	public void getSqlFilePath(Configuration dbConfiguration, File basedir,	String databaseType, String sqlJson) throws PhrescoException {
		List<String> filepaths = new ArrayList<String>();
		try {
			Gson gson = new Gson();
			Type mapObjectType = new TypeToken<Map<String, List<String>>>() {}.getType();
			String json = gson.toJson(sqlJson);
			String jsonPath = json.replace("\\", "").replaceAll("^\"|\"$","");
			Map<String, List<String>> dbMap = gson.fromJson(jsonPath, mapObjectType);
			Iterator<Entry<String, List<String>>> iterator = dbMap.entrySet().iterator();
			List<String> paths = new ArrayList<String>();
			while (iterator.hasNext()) {
				Entry<String, List<String>> entry = iterator.next();
				if (entry.getKey().equals(databaseType)) {
					filepaths = entry.getValue();
					String version = dbConfiguration.getProperties().getProperty(Constants.DB_VERSION);
					String dbType = dbConfiguration.getProperties().getProperty(Constants.DB_TYPE).toLowerCase();
					String pathVersion = "" ;
					for (String sourcePath : filepaths) {
						pathVersion = sourcePath.substring(sourcePath.indexOf(dbType), sourcePath.lastIndexOf("/"));
						pathVersion = (pathVersion.substring(pathVersion.indexOf("/")).substring(1));
						if (pathVersion.equals(version)) {
							paths.add(sourcePath);
						}
					}
					executeSql(dbConfiguration, basedir, paths);
				}
			}
		} catch (JsonIOException e) {
			throw new PhrescoException(e);
		} catch (JsonSyntaxException e) {
			throw new PhrescoException(e);
		}
	}

	private String findConnectionProtocol(String databaseType, String host, String port, String databaseName) {
		String connectionProtocol = null;
		if (databaseType.equals(Constants.MYSQL_DB) || databaseType.equals(Constants.HSQL_DB)
				|| databaseType.equals(Constants.DB2_DB)) {
			connectionProtocol = "jdbc:" + databaseType.toLowerCase() + "://" + host + ":" + port + "/" + databaseName;
		} else if (databaseType.equals(Constants.ORACLE_DB)) {
			connectionProtocol = "jdbc:" + "oracle:thin:@" + host + ":" + port + "/" + databaseName;
		} else if (databaseType.equals(Constants.MSSQL_DB)) {
			connectionProtocol = "jdbc:" + "sqlserver" + "://" + host + ":" + port + ";" + "DatabaseName="
					+ databaseName;
		}
		return connectionProtocol;
	}

	public void updateSqlQuery(Configuration dbConfiguration, String serverHost, String context, String serverport)
			throws PhrescoException {
		Connection conn = null;
		String updateQuery;
		String updateHomeQuery;
		try {
			String host = dbConfiguration.getProperties().getProperty(Constants.DB_HOST);
			String port = dbConfiguration.getProperties().getProperty(Constants.DB_PORT);
			String username = dbConfiguration.getProperties().getProperty(Constants.DB_USERNAME);
			String password = dbConfiguration.getProperties().getProperty(Constants.DB_PASSWORD);
			String database = dbConfiguration.getProperties().getProperty(Constants.DB_NAME);
			String databasetype = dbConfiguration.getProperties().getProperty(Constants.DB_TYPE);
			String dbUrl = "jdbc:" + databasetype + "://" + host + ":" + port;
			String url = dbUrl + PluginConstants.FORWARD_SLASH + database;
			conn = DriverManager.getConnection(url, username, password);
			Statement stmt = conn.createStatement();
			String wordPressUrl = "";
			if (serverport.equals(PluginConstants.APACHE_DEFAULT_PORT)) {
				wordPressUrl = serverHost;
			} else {
				wordPressUrl = serverHost + ':' + serverport;
			}
			updateQuery = PluginConstants.WORDPRESS_UPDATE_TABLE + "http://" + wordPressUrl
					+ PluginConstants.FORWARD_SLASH + context + PluginConstants.WORDPRESS_UPDATE_WHERE;
			updateHomeQuery = PluginConstants.WORDPRESS_UPDATE_TABLE + "http://" + wordPressUrl
					+ PluginConstants.FORWARD_SLASH + context + PluginConstants.WORDPRESS_UPDATE_HOME_WHERE;
			stmt.executeUpdate(updateQuery);
			stmt.executeUpdate(updateHomeQuery);
		} catch (Exception e) {
			// FIXME log exception
		} finally {
			Utility.closeConnection(conn);
		}
	}

}
