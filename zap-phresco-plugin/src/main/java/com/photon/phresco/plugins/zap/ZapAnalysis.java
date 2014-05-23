package com.photon.phresco.plugins.zap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public class ZapAnalysis implements ZapConstants{

	public void attack(Log log, String basedir, String environmentName, String protocol , String host, String port, String type, String urls) throws PhrescoException {
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
				generateReport(basedir, environmentName,  protocol, host, port, log);
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

	private static void generateReport(String basedir, String environmentName, String protocol, String host, String port, Log log) throws PhrescoException {
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
			writeOutput(stream, basedir, log, environmentName, protocol, host, port);
		} catch (Exception e) {
			throw new PhrescoException(e);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	private static void writeOutput(InputStream inputStream, String basedir, Log log, String environmentName, String protocol, String host, String port) throws PhrescoException {
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
				ZapStop zapStop = new ZapStop();
				zapStop.zapStop(log, basedir, environmentName, protocol, host, port);
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
