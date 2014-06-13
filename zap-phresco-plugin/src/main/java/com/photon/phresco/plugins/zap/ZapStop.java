package com.photon.phresco.plugins.zap;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public class ZapStop implements ZapConstants {
	private File baseDir;

	public void zapStop(Log log, String basedir, String environmentName, String zapUrl) throws PhrescoException {
		baseDir = new File(basedir);
		stopDaemonProcess(baseDir.getPath(), zapUrl, log);
	}

	private static void stopDaemonProcess(String baseDir, String zapUrl, Log log) throws PhrescoException {
		ClientResponse clientResponse = null;
		try {
			StringBuffer url = new StringBuffer();
			url.append(zapUrl);
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
			WebResource webResource = client.resource(url.toString());
			clientResponse = webResource.get(ClientResponse.class);
			log.info("Response = " + clientResponse.getStatus());
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
			throw new PhrescoException(e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (clientResponse != null) {
				clientResponse.close();
			}
		}
	}

}
