/**
 * Phresco Plugins
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

public class LocaleExtractor {

	private Document loopDoc;
	private String loc;
	private SAXBuilder builder;

	public LocaleExtractor(MavenProjectInfo mavenProjectInfo, String manifestFileName, String phrescoTargetDir)
			throws Exception {
		try {
			System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
			builder = new SAXBuilder();
			// disabling xml validation
			builder.setValidation(false);
			builder.setIgnoringElementContentWhitespace(true);
			loc = mavenProjectInfo.getBaseDir() + File.separator + phrescoTargetDir;
			loopDoc = builder.build(new File(mavenProjectInfo.getBaseDir()
					+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.target.dir")
					+ File.separator + 
					mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.config.name")));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getLocaleDirectories() throws JDOMException {
		List<Element> langAddedList = ((List<Element>) XPath.selectNodes(loopDoc, "//langAdded"));
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
