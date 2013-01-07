package com.photon.phresco.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPath;

import com.photon.phresco.plugin.commons.MavenProjectInfo;

public class LocaleExtractor{

	private Document loopDoc;
	private String loc;
	private SAXBuilder builder;
	
	public LocaleExtractor(MavenProjectInfo mavenProjectInfo, String manifestFileName, String phrescoTargetDir) throws Exception {
		try {
			builder = new SAXBuilder();
			// disabling xml validation
			builder.setValidation(false);
			builder.setIgnoringElementContentWhitespace(true);
			loc = mavenProjectInfo.getBaseDir() + File.separator + phrescoTargetDir;
			loopDoc = builder.build(new File(loc+File.separator+"config.xml"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getLocaleDirectories() throws JDOMException {
		List<Element> langAddedList = ((List<Element>) XPath.selectNodes(
				loopDoc, "//langAdded"));
		List<String> stringLocaleDirectories = new ArrayList<String>();
		List<Element> localeDirectories = new ArrayList<Element>();
		for (Element langAdded : langAddedList) {
			localeDirectories = langAdded.getChildren("language");
			for (Element localeDirectory : localeDirectories) {
				stringLocaleDirectories.add(localeDirectory.getValue());
			}
		}
		return stringLocaleDirectories;
	}
}
