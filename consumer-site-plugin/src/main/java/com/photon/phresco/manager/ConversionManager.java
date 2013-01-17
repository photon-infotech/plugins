package com.photon.phresco.manager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.photon.phresco.convertor.CsvXmlConvertor;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.validator.IValidator;
import com.photon.phresco.vo.CsvFileVO;

public class ConversionManager {

	IValidator validator;
	private SAXBuilder builder;
	CsvXmlConvertor csvXmlParser;

	public ConversionManager(MavenProjectInfo mavenProjectInfo, String manifestFileName, String phrescoTargetDir)
			throws IOException, JDOMException, Exception {
		csvXmlParser = new CsvXmlConvertor(mavenProjectInfo, File.separator + "manifest.xml", mavenProjectInfo
				.getProject().getProperties().getProperty("phresco.content.target.dir"));
	}

	public List<CsvFileVO> convert(MavenProjectInfo mavenProjectInfo) throws Exception {
		List<CsvFileVO> fileVOList = csvXmlParser.convert(mavenProjectInfo);
		return fileVOList;
	}
}
