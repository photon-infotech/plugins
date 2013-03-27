/**
 * Phresco Plugins
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
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
package com.photon.phresco.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPath;

import com.photon.phresco.convertor.CsvXmlConvertor;
import com.photon.phresco.parser.LocaleExtractor;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.validator.IValidator;
import com.photon.phresco.vo.CsvFileVO;

public class ConversionManager {

	IValidator validator;
	private SAXBuilder builder;
	CsvXmlConvertor csvXmlParser;
	private Document envXML;
	Map<String, String> stringEnvDetails = new HashMap<String, String>();
	
	public ConversionManager() {
		
	}

	public ConversionManager(MavenProjectInfo mavenProjectInfo, String manifestFileName, String phrescoTargetDir)
			throws IOException, JDOMException, Exception {
		csvXmlParser = new CsvXmlConvertor(mavenProjectInfo, File.separator + "manifest.xml", mavenProjectInfo
				.getProject().getProperties().getProperty("phresco.content.target.dir"));
	}

	public List<CsvFileVO> convert(MavenProjectInfo mavenProjectInfo) throws Exception {
		List<CsvFileVO> fileVOList = csvXmlParser.convert(mavenProjectInfo);
		return fileVOList;
	}
	
	public void replaceParameter(MavenProjectInfo mavenProjectInfo,String phrescoTargetDir) throws Exception {
		try {
			Map getEnv = getEnvironmentDetails(mavenProjectInfo, phrescoTargetDir);
			System.out.println(getEnv);
			String installFile = mavenProjectInfo.getBaseDir()
					+ File.separator
					+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.install.site.file.path")
					+ File.separator
					+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.install.site.file.name");
			try {
				String line = "", content = "";
				String currentLine;
				BufferedReader reader = null;
				
				//Read the file content as string
				reader = new BufferedReader(new FileReader(installFile));
				while ((currentLine = reader.readLine()) != null) {
					content += currentLine + "\n";
				}
				reader.close();
				
				//Replace strings
				String newtext = content.replaceAll("SITE_FOLDER", (String) getEnv.get("context"));
				newtext = newtext.replaceAll("PATH_TO_SITE", (String) getEnv.get("deploy_dir"));
				newtext = newtext.replaceAll("DB_USER", (String) getEnv.get("username"));
				newtext = newtext.replaceAll("DB_PASS", (String) getEnv.get("password"));
				newtext = newtext.replaceAll("DB_HOST", (String) getEnv.get("host"));
				newtext = newtext.replaceAll("DB_DATABASE", (String) getEnv.get("dbname"));
				
				//Write the update string to the same file
	            FileWriter writer = new FileWriter(installFile);
	            writer.write(newtext);
	            writer.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			} 			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
		
	public Map getEnvironmentDetails(MavenProjectInfo mavenProjectInfo, String phrescoTargetDir) throws JDOMException, IOException {
		System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");		
		builder = new SAXBuilder();
		// disabling xml validation
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		envXML = builder.build(new File(mavenProjectInfo.getBaseDir()
				+ File.separator
				+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.environment.xml.file.path")));
		
		//Get server details
		List<Element> serverDetails = ((List<Element>) XPath.selectNodes(envXML, "//Server"));
		System.out.println("serverDetails => " + serverDetails);
		
		//Get database details
		List<Element> dbDetails = ((List<Element>) XPath.selectNodes(envXML, "//Database"));
		System.out.println("dbDetails => " + dbDetails);
		
		for (Element server : serverDetails) {
			stringEnvDetails.put("context", server.getChildText("context"));
			stringEnvDetails.put("deploy_dir", server.getChildText("deploy_dir"));
		}
		
		for (Element db : dbDetails) {
			stringEnvDetails.put("username", db.getChildText("username"));
			stringEnvDetails.put("password", db.getChildText("password"));
			stringEnvDetails.put("host", db.getChildText("host"));
			stringEnvDetails.put("dbname", db.getChildText("dbname"));
		}
		return stringEnvDetails;
	}
}