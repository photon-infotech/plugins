package com.photon.phresco.plugins.zap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.ws.rs.core.MediaType;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public class ZapAnalysis implements ZapConstants{

	public void activeScan(Log log, String basedir, String environmentName, String zapUrl, String type, String urls, boolean isRemote) throws PhrescoException {
		try {
			StringBuffer url = new StringBuffer(128);
			url.append(zapUrl);
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
			url.append(AMPERSAND);
			url.append(RECURSE);
			url.append(EQUAL);
			url.append(TRUE);
			url.append(AMPERSAND);
			url.append(INSCOPE_ONLY);
			url.append(EQUAL);
			url.append(TRUE);
			Client client = Client.create();
			log.info(url.toString());
			WebResource webResource = client.resource(url.toString());
			ClientResponse response = webResource.accept(APPLICATION_JSON).get(ClientResponse.class);
			int status = response.getStatus();
			log.info("Active scan status = " + status);
		} catch (UniformInterfaceException e) {
			throw new PhrescoException(e);
		}
	}

	public void spiderScan(Log log, String basedir, String environmentName, String zapUrl, String type, String urls) throws PhrescoException {
		try {
			StringBuffer url = new StringBuffer(128);
			url.append(zapUrl);
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
			ClientResponse response = webResource.accept(APPLICATION_XML).get(ClientResponse.class);
			int status = response.getStatus();
			log.info("Spider Status = " + status);
		} catch (UniformInterfaceException e) {
			throw new PhrescoException(e);
		}
	}

	public void generateReport(String basedir, String environmentName, String zapUrl, boolean isRemote, Log log) throws PhrescoException {
		ClientResponse response = null;
		try {
			StringBuffer url = new StringBuffer();
			url.append(zapUrl);
			url.append(SLASH);
			url.append(OTHERS);
			url.append(SLASH);
			url.append(CORE);
			url.append(SLASH);
			url.append(OTHER);
			url.append(SLASH);
			url.append(REPORT);
			Client client = Client.create();
			WebResource webResource = client.resource(url.toString());
			String result = webResource.accept(MediaType.APPLICATION_XML).get(String.class);
			writeOutPut(basedir, isRemote, result, log, zapUrl, environmentName);
		} catch (Exception e) {
			throw new PhrescoException(e);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	private static void writeOutPut(String basedir, boolean isRemote, String result, Log log, String zapUrl, String environmentName) throws PhrescoException {
		PrintWriter writer = null;
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
				reportDir.mkdirs();
			}
			if (reportFile.exists()) {
				reportFile.delete();
			}
			if (!reportFile.exists()) {
				reportFile.createNewFile();
			}
			try {
				FileWriter outFile = new FileWriter(reportFile, true);
				writer = new PrintWriter(outFile);
				try {
					writer.append(result);
				} finally {
					writer.close();
				}
			} catch (IOException e) {
				throw new PhrescoException(e);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.close();
			if (!isRemote) {
				ZapStop zapStop = new ZapStop();
				try {
					zapStop.zapStop(log, basedir, environmentName, zapUrl);
				} catch (PhrescoException e) {
					throw new PhrescoException(e);
				}
			}
		}

	}


}
