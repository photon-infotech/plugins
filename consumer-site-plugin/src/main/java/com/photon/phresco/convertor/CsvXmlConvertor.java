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

import au.com.bytecode.opencsv.CSVReader;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.parser.LocaleExtractor;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.status.ConversionStatus;
import com.photon.phresco.validator.IValidator;
import com.photon.phresco.vo.ColumnVO;
import com.photon.phresco.vo.CsvFileVO;
import com.photon.phresco.vo.ImageVO;

import edu.emory.mathcs.backport.java.util.Arrays;

public class CsvXmlConvertor {

	private static final String NAME = "name";
	private List<String> localDirectories;
	private Document doc;
	private String loc;
	IValidator validator;
	private SAXBuilder builder;

	public CsvXmlConvertor(MavenProjectInfo mavenProjectInfo, String manifestFileName, String phrescoTargetDir)
			throws IOException, Exception {
		System.setProperty("javax.xml.parsers.SAXParserFactory",
				"com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
		builder = new SAXBuilder();

		// disabling xml validation
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		loc = "." + File.separator + phrescoTargetDir;
		doc = builder.build(new File(loc + manifestFileName));

		LocaleExtractor localeExtractor = new LocaleExtractor(mavenProjectInfo, manifestFileName, phrescoTargetDir);
		localDirectories = localeExtractor.getLocaleDirectories();

	}

	public List<CsvFileVO> convert(MavenProjectInfo mavenProjectInfo) throws JDOMException, IOException, Exception {
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
								+ nameValueOne + File.separator + fileNameValueTwo, nameValueOne, fileTypeValueTwo,
								File.separator + nameValue + File.separator + nameValueOne));
					}
				}
			}
		}
		transferFiles(fileVOList, mavenProjectInfo);
		new PHPCreator().createPHPFile(fileVOList, mavenProjectInfo);
		return fileVOList;
	}

	private List<CsvFileVO> getXMLData(Element file, String stringFile, String parentFolder, String fileType,
			String currentFolder) throws IOException, PhrescoException {
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

	private List<CsvFileVO> getCSVData(Element file, List<Element> columnList, File csvFile, String parentFile,
			String currentFolder) throws IOException, PhrescoException {
		List<String[]> allCsvStringValues = getAllValuesFromCSVFile(csvFile);
		HashMap<String, ColumnVO> columnVOSet = getColumnData(columnList);
		String headerArr[] = null;
		List<CsvFileVO> fileVOList = new ArrayList<CsvFileVO>();
		headerArr = allCsvStringValues.get(0);
		int minCount = Integer.parseInt(file.getAttributeValue("minCount"));
		String fileName = file.getAttributeValue("name");
		if (minCount > 0 && allCsvStringValues.size() <= 1) {
			throw new PhrescoException("File " + fileName + " needs to contain at least one line");
		}
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
				ColumnVO columnVO = columnVOSet.get(headerArr[j].trim());
				Set<String> keys = columnVOSet.keySet();
				boolean flag = true;
				for (String k : keys) {
					if(!Arrays.asList(headerArr).contains(k)){
						flag = false;
					}
				}
				if(flag==false){
					throw new PhrescoException("The column names in file " + csvFile.getAbsolutePath()
							+ " are not as per manifest file -");
				}
				if (columnVO == null) {
					throw new PhrescoException("The column names in file " + csvFile.getAbsolutePath()
							+ " are not as per manifest file +");
				}
				String fieldName = columnVO.getFieldName();
				String fieldType = columnVO.getFieldType();
				if (!columnVO.isEmpty() && (arr[j] == null || arr[j].length() <= 0)) {
					throw new PhrescoException("Column " + fieldName + " cannot be empty for file " + fileName);
				}
				csvFileVO.setFieldType(fieldType);
				csvFileVO.setCurrentFolderPath(currentFolder.replace("!languages", "en"));
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
							ImageVO imageVO = new ImageVO();
							imageVO.setFileName(arr[j]);
							imageVO.setDestination(columnVO.getDestination());
							imageVO.setKey(fieldName);
							imageVO.setFileNameSize(columnVO.getFileNameSize());
							imageVO.setDatatype(columnVO.getDatatype());
							csvFileVO.getImageMap().put(fieldName, imageVO);
							if (arr[j].length() > (columnVO.getFileNameSize())
									&& columnVO.getDatatype().equals("varchar")) {
								throw new PhrescoException("File Size not as per specifications for " + fileName
										+ " image " + arr[j]);
							}
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
			String columnName = column.getAttribute("name").getValue().trim();
			ColumnVO columnVO = new ColumnVO();
			columnVO.setFieldName(column.getAttribute("fieldname").getValue());
			columnVO.setFieldType(column.getAttribute("fieldtype").getValue());
			if (column.getAttribute("fieldtype").getValue().equals("image")) {
				columnVO.setDestination(column.getChild("destination").getValue());
			}
			columnVO.setEmpty(new Boolean(column.getAttributeValue("isEmpty")));
			if (column.getChild("limit").getValue() != null && column.getChild("limit").getValue().length() > 0) {
				columnVO.setFileNameSize(Integer.parseInt(column.getChild("limit").getValue()));
			}
			columnVO.setDatatype(column.getChild("datatype").getValue());
			columnVOSet.put(columnName, columnVO);
		}
		return columnVOSet;
	}

	private List<String[]> getAllValuesFromCSVFile(File csvFile) throws FileNotFoundException, IOException {
		CSVReader reader = new CSVReader(new FileReader(csvFile));
		return reader.readAll();
	}

	public void transferFiles(List<CsvFileVO> fileList, MavenProjectInfo mavenProjectInfo)
			throws MojoExecutionException, JDOMException, IOException, PhrescoException {
		for (CsvFileVO csvoFile : fileList) {
			Set<String> keys = csvoFile.getImageMap().keySet();
			String destination = "";
			String source = "";
			ImageVO imageVO = new ImageVO();
			for (String k : keys) {
				imageVO = csvoFile.getImageMap().get(k);
				source = mavenProjectInfo.getProject().getBasedir()
						+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.content.target.dir")
						+ csvoFile.getCurrentFolderPath() + File.separator + "assets" + File.separator + "images"
						+ File.separator + imageVO.getFileName();
				destination = mavenProjectInfo.getProject().getBasedir() + File.separator + "source" + File.separator
						+ csvoFile.getImageMap().get(k).getDestination() + imageVO.getFileName();
				System.out.println("csvoFile.getImageMap().get(k).getDestination()   "
						+ csvoFile.getImageMap().get(k).getDestination());
				File sourceFile = new File(source);
				File destinationFile = new File(destination);
				System.out.println("source  :  " + source);
				System.out.println();
				System.out.println("destination :  " + destination);
				transferFile(mavenProjectInfo.getProject().getBasedir() + File.separator + "source" + File.separator
						+ csvoFile.getImageMap().get(k).getDestination(), sourceFile, destinationFile);
			}
		}
	}

	private List<ConversionStatus> transferFile(String destinationDir, File sourceFile, File destinationFile)
			throws MojoExecutionException, IOException, PhrescoException {
		if (!sourceFile.exists()) {
			throw new PhrescoException("File " + sourceFile.getAbsolutePath() + " not found");
		}
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
