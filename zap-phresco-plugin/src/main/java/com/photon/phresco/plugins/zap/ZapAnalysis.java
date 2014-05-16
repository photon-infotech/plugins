package com.photon.phresco.plugins.zap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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


public class ZapAnalysis implements ZapConstants{
	private File baseDir;
	private String environmentName;
	private String url;
	private String host;
	private String protocol;
	private String port;
	private String type;
	private static Log log;

	public void analysis(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException, InterruptedException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		Map<String, String> configs = MojoUtil.getAllValues(configuration);
		environmentName = configs.get(ENVIRONMENT_NAME);
		url = configs.get(URL);
		type = configs.get(TYPE);
		try {
			StringBuffer buffer = new StringBuffer(128);
			buffer.append(baseDir + File.separator);
			buffer.append(DOT_PHRESCO_FOLDER + File.separator);
			buffer.append(CONFIG_FILE);
			ConfigReader reader = new ConfigReader( new File(buffer.toString()));
		
			List<com.photon.phresco.configuration.Configuration> configurationList = reader.getConfigurations(environmentName, SERVER);
			if (CollectionUtils.isNotEmpty(configurationList)) {
				com.photon.phresco.configuration.Configuration config = configurationList.get(0);
				Properties properties = config.getProperties();
				protocol = (String) properties.get(PROTOCOL);
				host = (String) properties.get(HOST);
				port = (String) properties.get(PORT);
				attack(protocol, host, port, type, url, baseDir.getPath(), log);
			} else {
				throw new PhrescoException(CONFIG_FILE_NOT_FOUND_ERROR);
			}
		} catch (ConfigurationException e) {
			throw new PhrescoException(e);
		}
	}	

	private static void attack(String protocol , String host, String port, String type, String urls, String basedir, Log log ) throws PhrescoException {
		try {
			StringBuffer url = new StringBuffer(128);
			url.append(protocol);
			url.append(COLON);
			url.append(DOUBLE_SLASH);
			url.append(host);
			url.append(COLON);
			url.append(port);
			url.append(SLASH);
			url.append(XML);
			url.append(SLASH);
			url.append(type);
			url.append(SLASH);
			url.append(OUTPUT_FORMAT);
			url.append(AMPERSAND);
			url.append(URL);
			url.append(EQUAL);
			url.append(urls);
			Client client = Client.create();
			log.info(url.toString());
			WebResource webResource = client.resource(url.toString());
			ClientResponse response = webResource.accept(APPLICATION_JSON).get(ClientResponse.class);
			int status = response.getStatus();
			if (status == 200) {
				Thread.sleep(10000);
				generateReport(basedir, protocol, host, port,log);
			} else {
				throw new PhrescoException(REPORT_FAIL);
			}
		} catch (UniformInterfaceException e) {
			throw new PhrescoException(e);
		} catch (PhrescoException e) {
			throw new PhrescoException(e);
		} catch (InterruptedException e) {
			throw new PhrescoException(e);
		}

	}

	private static void generateReport(String basedir, String protocol, String host, String port, Log log) throws PhrescoException {
		ClientResponse response = null;
		try {
			StringBuffer url = new StringBuffer();
			url.append(protocol);
			url.append(COLON);
			url.append(DOUBLE_SLASH);
			url.append(host);
			url.append(COLON);
			url.append(port);
			url.append(SLASH);
			url.append(OTHERS);
			url.append(SLASH);
			url.append(CORE);
			url.append(SLASH);
			url.append(OTHER);
			url.append(SLASH);
			url.append(REPORT);
			log.info("url = " + url.toString());
			Client client = Client.create();
			WebResource webResource = client.resource(url.toString());
			response = webResource.accept(APPLICATION_XML).get(ClientResponse.class);
			InputStream stream = response.getEntityInputStream();
			writeOutput(stream, basedir);
		} catch (Exception e) {
			throw new PhrescoException(e);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	private static void writeOutput(InputStream inputStream, String basedir) throws PhrescoException {
		OutputStream outputStream = null;
		try {
			StringBuffer reportPath = new StringBuffer(128);
			reportPath.append(basedir);
			reportPath.append(File.separator);
			reportPath.append(DO_NOT_CHECKIN_FOLDER);
			reportPath.append(File.separator);
			reportPath.append(TARGET);
			reportPath.append(File.separator);
			reportPath.append(ZAP_REPORT);
			File reportDir =  new File(reportPath.toString());
			File reportFile = new File(reportDir + File.separator + REPORT_FILE);
			if (!reportDir.exists()) {
				reportDir.mkdir();
			}
			if (!reportFile.exists()) {
				reportFile.createNewFile();
			}
			if (reportFile.exists()) {
				outputStream =   new FileOutputStream(reportFile);
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
		}
	}

}
