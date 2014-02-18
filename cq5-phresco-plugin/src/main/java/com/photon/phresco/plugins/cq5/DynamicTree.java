package com.photon.phresco.plugins.cq5;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.photon.phresco.api.DynamicParameter;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues.Value;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class DynamicTree implements DynamicParameter, Constants {

	public PossibleValues getValues(Map<String, Object> paramsMap) throws PhrescoException {
		PossibleValues possibleValues = new PossibleValues();
		try {
			ApplicationInfo applicationInfo = (ApplicationInfo) paramsMap.get(KEY_APP_INFO);
			String browsePath = Utility.getProjectHome() + applicationInfo.getAppDirName() + File.separator + "src/main/content/jcr_root";
			DOMSource createXML = Utility.createXML(browsePath, "Folder");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(createXML, result);
			String xmlFormatString = writer.toString();
			File file = new File(browsePath);
			xmlFormatString = xmlFormatString.replace(file.getPath(), "");
			Value value = new Value();
			value.setValue(xmlFormatString);
			possibleValues.getValue().add(value);
		} catch (TransformerConfigurationException e) {
			throw new PhrescoException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new PhrescoException(e);
		} catch (TransformerException e) {
			throw new PhrescoException(e);
		}
		return possibleValues;
	}
}
