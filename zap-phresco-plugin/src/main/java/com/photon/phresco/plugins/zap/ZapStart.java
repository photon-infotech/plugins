package com.photon.phresco.plugins.zap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import com.photon.phresco.configuration.ConfigReader;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.Constants;

public class ZapStart implements ZapConstants { 
	private File baseDir;
	private String environmentName;
	private String zapDirectory;
	private String port;
	private String protocol;
	private String host;
	private String url;
	private boolean isRemote;

	public void start(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		baseDir = mavenProjectInfo.getBaseDir();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		url =  configs.get(URL);
		isRemote = Boolean.parseBoolean(configs.get(REMOTE));
		File configPath = new File(baseDir + File.separator + DOT_PHRESCO_FOLDER + File.separator  + CONFIG_FILE);
		if (!configPath.exists()) {
			throw new PhrescoException(CONFIG_FILE_NOT_FOUND_ERROR);
		}
		try {
			ConfigReader reader = new ConfigReader(configPath);
			List<com.photon.phresco.configuration.Configuration> configurationList = reader.getConfigurations(environmentName, SERVER);
			if (CollectionUtils.isNotEmpty(configurationList)) {
				com.photon.phresco.configuration.Configuration config = configurationList.get(0);
				if (config != null) {
					Properties properties = config.getProperties();
					zapDirectory = (String) properties.get(ZAPDIR);
					protocol = (String) properties.get(PROTOCOL);
					host = (String) properties.get(HOST);
					port = (String) properties.get(ZAP_PORT);
					String zapUrl = protocol + COLON + DOUBLE_SLASH + host + COLON + port;
					if (!isRemote) {
						validateZapDirectory(zapDirectory);
						startDaemonProcess(baseDir.getPath(),zapUrl , zapDirectory, url,log);
					} else {
						boolean checkIfUrlExists = checkIfUrlExists(zapUrl);
						ZapAnalysis  analysis = new ZapAnalysis();
						if (checkIfUrlExists) {
							analysis.spiderScan(log, baseDir.getPath(), environmentName, zapUrl, SPIDER, url);
							analysis.activeScan(log, baseDir.getPath(), environmentName, zapUrl, ASCAN, url, isRemote);
							analysis.generateReport(baseDir.getPath(), environmentName, zapUrl, isRemote, log);
						} else {
							log.info(ZAP_NOT_STARTED_IN_REMOTE);
						}
					}
				}
			}

		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		}
	}	

	private void validateZapDirectory(String zapDirectory) throws PhrescoException {
		try {
			File targetDir = new File(zapDirectory);
			File ZapFile = new File(targetDir + File.separator + ZAP_JAR);
			if (!ZapFile.exists()) {
				throw new PhrescoException(INVALID_ZAP_DIR);
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void startDaemonProcess(String path, String zapUrl, String zapDirectory, String url, Log log) throws PhrescoException {
		BufferedReader reader = null;
		String command = "";
		if (System.getProperty(Constants.OS_NAME).startsWith(Constants.WINDOWS_PLATFORM)) {
			command = ZAP_WINDOWS_BATCH_COMMAND;
		} else  {
			command = ZAP_MAC_BATCH_COMMAND;
		} 
		Commandline commandline = new Commandline(command);
		commandline.setWorkingDirectory(zapDirectory);
		try {
			String line;
			Process execute = commandline.execute();
			InputStream inputStream = execute.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			ZapAnalysis  analysis = new ZapAnalysis();
			while ((line = reader.readLine()) != null) {
				log.info(line);
				if (line.contains(NEW_SESSION)) {
					analysis.spiderScan(log, baseDir.getPath(), environmentName, zapUrl, SPIDER, url);
				}
				if (line.contains(SPIDER_COMPLETED)) {
					analysis.activeScan(log, baseDir.getPath(), environmentName, zapUrl, ASCAN, url, isRemote);
				}
				if (line.contains(ACTIVE_SCAN_COMPLETED)) {
					Thread.sleep(6000);
					analysis.generateReport(baseDir.getPath(), environmentName, zapUrl, isRemote, log);
				}
			}
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (CommandLineException e) {
			throw new PhrescoException(e);
		} catch (InterruptedException e) {
			throw new PhrescoException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
		}
	}

	public static boolean checkIfUrlExists(String targetUrl) {
		HttpURLConnection httpUrlConn = null;
		try {
			httpUrlConn = (HttpURLConnection) new java.net.URL(targetUrl).openConnection();
			httpUrlConn.setRequestMethod(HEAD_REVISION);
			httpUrlConn.setConnectTimeout(30000);
			httpUrlConn.setReadTimeout(30000);
			return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			return false;
		} finally {
			httpUrlConn.disconnect();
		}
	}

	/*	public static void main(String[] args) {
		 Runtime runtime;
		try {
			runtime = Runtime.getRuntime();
			String command = "netstat -a -o | find " +  "\"15100\"" ;
			System.out.println("Line = " + command);
			Process process = runtime.exec(command);
			System.out.println("Process = " + process);
			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			System.out.println("reader = " + reader);
			System.out.println("reader Line = " + reader.readLine());
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


	/* public static void main(String[] args) throws Exception {
		    URL mac = new URL("http://172.16.22.65:15100/");
		    BufferedReader in = new BufferedReader(
		                      new InputStreamReader(mac.openStream()));

		    String inputLine;
		    while ((inputLine = in.readLine()) != null)
		        System.out.println(inputLine);

		    in.close();
	 }*/

	public static void main(String[] args) {
		try {
			String command = "top -n1 -b -p " +  "\"5476\"";
			Process p = Runtime.getRuntime().exec(command);
			InputStream in = p.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String inputLine;
			while ((inputLine = reader.readLine()) != null)
				System.out.println(inputLine);

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
