package com.photon.phresco.plugins.zap;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.configuration.ConfigReader;
import com.photon.phresco.exception.ConfigurationException;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public class ZapStop implements ZapConstants{
	private File baseDir;
	private String environmentName;
	private String port;
	private static Log log;
	private String protocol;
	private String host;

	public void zapStop(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		try {
			ConfigReader reader = new ConfigReader( new File(baseDir + File.separator + DOT_PHRESCO_FOLDER + File.separator  + CONFIG_FILE));
			List<com.photon.phresco.configuration.Configuration> configurationList = reader.getConfigurations(environmentName, SERVER);
			if (CollectionUtils.isNotEmpty(configurationList)) {
				for (com.photon.phresco.configuration.Configuration config : configurationList) {
					Properties properties = config.getProperties();
					protocol = (String) properties.get(PROTOCOL);
					host = (String) properties.get(HOST);
					port = (String) properties.get(PORT);
				}
				StringBuffer url = new StringBuffer();
				url.append(protocol);
				url.append(COLON);
				url.append(DOUBLE_SLASH);
				url.append(host);
				url.append(COLON);
				url.append(port);
				stopDaemonProcess(baseDir.getPath(), url.toString());
			}
		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		}
	}

	private static void stopDaemonProcess(String baseDir, String targetUrl) throws PhrescoException {
		try {
			StringBuffer url = new StringBuffer();
			url.append(targetUrl);
			url.append(SLASH);
			url.append(XML);
			url.append(SLASH);
			url.append(CORE);
			url.append(SLASH);
			url.append(ACTION);
			url.append(SLASH);
			url.append(SHUTDOWN);
			url.append(SLASH);
			url.append(QUESTION_MARK);
			url.append(APPLICATION_XML_FORMAT);
			Client client = Client.create();
			log.info(url.toString());
			System.out.println("Url = " + url.toString());
			WebResource webResource = client.resource(url.toString());
			ClientResponse clientResponse = webResource.get(ClientResponse.class);
			log.info("Response = " + clientResponse.getStatus());
		} catch (UniformInterfaceException e) {
			throw new PhrescoException(e);
		}
	}

	public static boolean checkIfURLExists(String targetUrl) {
		HttpURLConnection httpUrlConn = null;
		try {
			httpUrlConn = (HttpURLConnection) new java.net.URL(targetUrl).openConnection();
			httpUrlConn.setRequestMethod(HEAD);
			httpUrlConn.setConnectTimeout(30000);
			httpUrlConn.setReadTimeout(30000);
			return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			return false;
		} finally {
			httpUrlConn.disconnect();
		}
	}
	
	public static void main(String[] args) {
		StringBuffer url = new StringBuffer();
		url.append("http");
		url.append(COLON);
		url.append(DOUBLE_SLASH);
		url.append("localhost");
		url.append(COLON);
		url.append("16100");
		System.out.println("URL = " + url.toString());
		String basedir  = "C:/Documents and Settings/saravanan_na/workspace/projects/Demos";
		boolean urlExists = checkIfURLExists(url.toString());			
		System.out.println("URL EXIST = " + urlExists);
		if (urlExists) {
			try {
				stopDaemonProcess(basedir, url.toString());
			} catch (PhrescoException e) {
				e.printStackTrace();
			}
		}
		
	}
	

}
