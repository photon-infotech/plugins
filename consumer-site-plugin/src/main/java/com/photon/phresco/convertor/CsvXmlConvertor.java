package com.photon.phresco.convertor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPath;

import sun.util.logging.resources.logging;

import au.com.bytecode.opencsv.CSVReader;

import com.jcraft.jsch.Buffer;
import com.photon.phresco.parser.LocaleExtractor;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.status.ValidationStatus;
import com.photon.phresco.validator.IValidator;
import com.photon.phresco.vo.ColumnVO;
import com.photon.phresco.vo.CsvFileVO;

public class CsvXmlConvertor {

	private static final String NAME = "name";
	private List<String> localDirectories;
	private Document doc;
	private String loc;
	IValidator validator;
	private SAXBuilder builder;

	public CsvXmlConvertor(MavenProjectInfo mavenProjectInfo, String manifestFileName, String phrescoTargetDir)
			throws IOException, Exception {
		builder = new SAXBuilder();
		// disabling xml validation
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		loc = "." + File.separator + phrescoTargetDir;
		doc = builder.build(new File(loc + manifestFileName));

		LocaleExtractor localeExtractor = new LocaleExtractor(mavenProjectInfo, manifestFileName, phrescoTargetDir);
		localDirectories = localeExtractor.getLocaleDirectories();

	}

	public List<CsvFileVO> convert(MavenProjectInfo mavenProjectInfo) throws MojoExecutionException, JDOMException, IOException, Exception {
		List<Element> structureList = ((List<Element>) XPath.selectNodes(doc, "//validations/structure"));
		List<CsvFileVO> fileVOList = new ArrayList<CsvFileVO>();
		for (Element structure : structureList) {
			List<Element> folderList = structure.getChildren("folder");
			for (Element folder : folderList) {
				String nameValue = folder.getAttributeValue(NAME);
				List<Element> folderListOne = folder.getChildren("folder");
				for (Element folderOne : folderListOne) {
					String nameValueOne = folderOne.getAttributeValue(NAME);
					List<Element> fileListTwo = folderOne.getChildren("file");
					for (Element fileTwo : fileListTwo) {
						String fileTypeValueTwo = fileTwo.getAttributeValue("type");
						String fileNameValueTwo = fileTwo.getAttributeValue(NAME);
						fileVOList.addAll(getXMLData(fileTwo, File.separator + nameValue + File.separator
								+ nameValueOne + File.separator + fileNameValueTwo, nameValueOne, fileTypeValueTwo, File.separator + nameValue + File.separator
								+ nameValueOne));
					}
				}
			}
		}
		new PHPCreator().createPHPFile(fileVOList,mavenProjectInfo);
		transferFiles(fileVOList, mavenProjectInfo);
		return fileVOList;
	}

	private List<CsvFileVO> getXMLData(Element file, String stringFile, String parentFolder, String fileType, String currentFolder)
			throws IOException {
		List<CsvFileVO> fileVOList = new ArrayList<CsvFileVO>();
		if (stringFile.indexOf('!') >= 0) {
			String replacementStringFile = "";
			String parentReplacementFile = "";
			for (String localDirectory : localDirectories) {
				replacementStringFile = "";
				if (parentFolder.indexOf('!') >= 0) {
					parentReplacementFile = parentFolder.replace("!languages", localDirectory);
				}
				replacementStringFile = stringFile.replace("!languages", localDirectory);
				File csvFile = new File(loc + replacementStringFile);
				if (fileType.equals("csv")) {
					List<Element> columnList = file.getChildren("column");
					fileVOList.addAll(getCSVData(file, columnList, csvFile, parentReplacementFile, currentFolder));
				}
			}
		}
		return fileVOList;
	}

	private List<CsvFileVO> getCSVData(Element file, List<Element> columnList, File csvFile, String parentFile, String currentFolder)
			throws IOException {
		List<String[]> allCsvStringValues = getAllValuesFromCSVFile(csvFile);
		HashMap<String, ColumnVO> columnVOSet = getColumnData(columnList);
		String headerArr[] = null;
		List<CsvFileVO> fileVOList = new ArrayList<CsvFileVO>();
		headerArr = allCsvStringValues.get(0);
		for (int i = 1; i < allCsvStringValues.size(); i++) {
			String arr[] = allCsvStringValues.get(i);
			CsvFileVO csvFileVO = new CsvFileVO();
			csvFileVO.setContentType(file.getAttributeValue("contentType"));
			csvFileVO.setContentTypeName(file.getAttributeValue("contentTypeName"));
			csvFileVO.setLanguage(parentFile);
			if (file.getAttributeValue("addFunction") != null && file.getAttributeValue("addFunction").length() > 0) {
				csvFileVO.setPhpFunction(file.getAttributeValue("addFunction"));
			}
			for (int j = 0; j < arr.length; j++) {
				ColumnVO columnVO = columnVOSet.get(headerArr[j]);
				String fieldName = columnVO.getFieldName();
				String fieldType = columnVO.getFieldType();
				csvFileVO.setFieldType(fieldType);
				csvFileVO.setCurrentFolderPath(currentFolder.replace("!languages", "en"));
				csvFileVO.setTargetFolder(columnVO.getDestination());
				if (fieldName.equals("title") && fieldType.equals("argument")
						&& (arr[j] != null && arr[j].length() > 0)) {
					csvFileVO.getTitleMap().put("title", arr[j]);
				} else if (fieldName.equals("description") && fieldType.equals("argument")
						&& (arr[j] != null && arr[j].length() > 0)) {
					csvFileVO.getDescriptionMap().put("description", arr[j]);					
				} else {
					if (arr[j] != null && arr[j].length() > 0) {
						if (fieldType.equals("urlmap")) {
							csvFileVO.getUrlMap().put(fieldName, arr[j]);
						}
						if (fieldType.equals("extra")) {
							csvFileVO.getExtraMap().put(fieldName, arr[j]);
						}
						if (fieldType.equals("image")) {
							csvFileVO.getImageMap().put(fieldName, arr[j]);
							csvFileVO.setImageFileName(arr[j]);
						}
						if (fieldType.equals("metadata")) {
							csvFileVO.getMetadataMap().put(fieldName, arr[j]);
						}
						if (fieldType.equals("category")) {
							csvFileVO.getCategoryMap().put(fieldName, arr[j]);
						}
					}
				}
			}
			fileVOList.add(csvFileVO);
		}
		return fileVOList;
	}

	private HashMap<String, ColumnVO> getColumnData(List<Element> columnList) {
		HashMap<String, ColumnVO> columnVOSet = new HashMap<String, ColumnVO>();
		for (Element column : columnList) {
			String columnName = column.getAttribute("name").getValue();
			ColumnVO columnVO = new ColumnVO();
			columnVO.setFieldName(column.getAttribute("fieldname").getValue());
			columnVO.setFieldType(column.getAttribute("fieldtype").getValue());
			columnVO.setDestination(column.getChild("destination").getValue());
			columnVOSet.put(columnName, columnVO);
		}
		return columnVOSet;
	}

	private List<String[]> getAllValuesFromCSVFile(File csvFile) throws FileNotFoundException, IOException {
		CSVReader reader = new CSVReader(new FileReader(csvFile));
		return reader.readAll();
	}
	
	private void transferFiles(List<CsvFileVO> fileList, MavenProjectInfo mavenProjectInfo) throws MojoExecutionException, JDOMException, IOException {
		for (CsvFileVO csvoFile : fileList) {
			Set<String> keys = csvoFile.getImageMap().keySet();
			String imageFileName ="";
			String destination = "";
			String source ="";
			for (String k : keys) {
				if (csvoFile.getFieldType().equals("image")) {
					imageFileName = csvoFile.getImageMap().get(k);
					source = mavenProjectInfo.getProject().getBasedir()+mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.target.dir")
							+csvoFile.getCurrentFolderPath()+File.separator+ "assets"+File.separator+"images"+File.separator+csvoFile.getImageFileName();
					File sourceFile = new File(source);
					destination = mavenProjectInfo.getProject().getBasedir()+File.separator+"source"+
					File.separator+csvoFile.getTargetFolder()
							+ csvoFile.getImageFileName();
					System.out.println("1. "+ mavenProjectInfo.getProject().getBasedir()+File.separator+"source"+
					File.separator+csvoFile.getTargetFolder()
							+ csvoFile.getImageFileName());
					File destinationFile = new File(destination);
					destination = csvoFile.getImageFileName();
					System.out.println("2. "+mavenProjectInfo.getProject().getBasedir()+File.separator+"source"+
							File.separator+csvoFile.getTargetFolder());
					transferFile(mavenProjectInfo.getProject().getBasedir()+File.separator+"source"+
							File.separator+csvoFile.getTargetFolder(),sourceFile, destinationFile);
				}
			}
		}
	}

	private List<ValidationStatus> transferFile(String destinationDir, File sourceFile, File destinationFile) throws MojoExecutionException,
			IOException {
		new File(destinationDir).mkdirs();
		FileInputStream fileInputStream = new FileInputStream(sourceFile);
		FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
		int bufferSize;
		byte[] buffer = new byte[512];
		while ((bufferSize = fileInputStream.read(buffer)) > 0) {
			fileOutputStream.write(buffer, 0, bufferSize);
		}
		return null;
	}
}
