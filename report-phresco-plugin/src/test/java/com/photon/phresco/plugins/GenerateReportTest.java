package com.photon.phresco.plugins;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.eclipse.jgit.util.FileUtils;
import org.junit.Test;
import org.w3c.dom.Element;

import com.photon.phresco.plugin.commons.PluginConstants;
import com.phresco.pom.util.PomProcessor;

public class GenerateReportTest extends TestCase implements PluginConstants {
	
//	private static final String DOT_REPORT_FILE = ".jasper";
	private static final String DOT_REPORT_FILE = ".jrxml";
	
	GenerateReport genReport = new GenerateReport();
	
	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}
	
//	@Test
	public void PdfWithImageAsStream() throws Exception {
		try {
			System.out.println("report123 ");
			String outFileNamePDF = "/Users/kaleeswaran/Tried/POC/imageStreamToPdf/RQ1.pdf";
			new File(outFileNamePDF).getParentFile().mkdirs();
			String containerJrxmlFile = "/Users/kaleeswaran/Tried/POC/imageStreamToPdf/imageToStreamPdfRQ.jrxml";
			
			Map<String, Object> parameters = new HashMap<String,Object>();
			File img = new File("/Users/kaleeswaran/Desktop/RQ.jpg");
			InputStream fis = new FileInputStream(img);
			parameters.put("logo", fis);
			
			InputStream reportStream = new FileInputStream(containerJrxmlFile);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(reportStream);
			JasperDesign jasperDesign = JRXmlLoader.load(bufferedInputStream);
			String desinationPath = "/Users/kaleeswaran/Tried/POC/imageStreamToPdf/imageToStreamPdfRQ.jasper";
			JasperCompileManager.compileReportToFile(jasperDesign, desinationPath);
			
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			System.out.println("Compiling... ");
//			
			JasperDesign jasperDesign1 = JRXmlLoader.load("/Users/kaleeswaran/Tried/POC/imageStreamToPdf/imageToStreamPdfRQ_subreport1.jrxml");
			String desinationPath1 = "/Users/kaleeswaran/Tried/POC/imageStreamToPdf/imageToStreamPdfRQ_subreport1.jasper";
			JasperCompileManager.compileReportToFile(jasperDesign1, desinationPath1);
//			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter(); 
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileNamePDF);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.exportReport();
			
			System.out.println("pdf report created123 .... ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReport() throws Exception {
		try {
//			genReport.compileReports();
			
//			String baseDirPath = "/Users/kaleeswaran/workspace/projects/multijnone-html5multichanneljquerywidget"; //widget project check
//			String baseDirPath = "/Users/kaleeswaran/workspace/projects/1-php";	// sample report 
//			String baseDirPath = "/Users/kaleeswaran/workspace/projects/TTSP-javawebservice"; // multi module
//			String baseDirPath = "/Users/kaleeswaran/workspace/projects/load-php"; // load test check
//			String baseDirPath = "/Users/kaleeswaran/workspace/projects/androidNativ-androidnative"; // android perforamce
			
			// BBY
			String baseDirPath = "/Users/kaleeswaran/workspace/PDFReportCheck2.2/drupal7none-drupal7";
//			String baseDirPath = "/Users/kaleeswaran/Downloads/Transfered-135/search-foundation";
//			String baseDirPath = "/Users/kaleeswaran/workspace/PDFReportCheck2.2/ASA-iphonehybrid";
//			String baseDirPath = "/Users/kaleeswaran/workspace/PDFReportCheck2.2/Androidnative-shoppingcart-androidnative";
//			String baseDirPath = "/Users/kaleeswaran/workspace/PDFReportCheck2.2/yuimultieshop-html5multichannelyuiwidget";
			
			String dotPhrescoFilePath = baseDirPath + "/.phresco" + File.separator + PROJECT_INFO_FILE;
			String testType = "All"; // testtype
			String reportType = "detail"; // report type
//			String projectCode = "php-php"; // maven pom.xml name tag value
			String sonarUrl = "http://localhost:2468/sonar"; // sonar url
			Properties properties = new Properties();
			
			String pomPath = baseDirPath + "/pom.xml";
			System.out.println("pomPath => " + pomPath);
			
			String logoImgBase64 = "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";
			String theme = "{&quot;ButtonColor&quot;:&quot;#51A1C9&quot;,&quot;CopyRight&quot;:&quot;© 2003-2013 BBY Solutions, Inc. All rights reserved&quot;,&quot;LabelColor&quot;:&quot;#000000&quot;,&quot;DisabledLabelColor&quot;:&quot;#FDEA00&quot;,&quot;brandingColor&quot;:&quot;#12C2E9&quot;,&quot;accordionBackGroundColor&quot;:&quot;#638E97&quot;,&quot;MenuBackGround&quot;:&quot;#223D62&quot;,&quot;CopyRightColor&quot;:&quot;#6E6E6E&quot;,&quot;bodyBackGroundColor&quot;:&quot;#FFFFFF&quot;,&quot;PageHeaderColor&quot;:&quot;#005088&quot;,&quot;MenufontColor&quot;:&quot;#FFFFFF&quot;}";
			theme = theme.replace("&quot;", "\"");
			
			String technologyName = "HTML5 JQuery Mobile Widget";
//			technologyName = "";
			
			PomProcessor pp = new PomProcessor(new File(pomPath));
			com.phresco.pom.model.Model.Properties properties2 = pp.getModel().getProperties();
			List<Element> propElem = properties2.getAny();
			for (Element element : propElem) {
				properties.put(element.getTagName(), element.getTextContent());
				System.out.println("element.getTagName() > " + element.getTagName() + " element.getTextContent() " + element.getTextContent());
			}
			
			System.out.println("Generating ....  ");
			genReport.generateTest(baseDirPath, dotPhrescoFilePath, testType, reportType, sonarUrl, properties, logoImgBase64, theme, technologyName, pomPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public void compileReports() throws Exception {
//		File pdfFileDir = new File("/Users/kaleeswaran/work/photon-infotech/2.2/plugins/report-phresco-plugin/src/main/resources/");
//		File[] listFiles = pdfFileDir.listFiles(new FileExtensionFileFilter(".jrxml"));
//		for (File jrxmlFile : listFiles) {
//			System.out.println("jrxmlFile ===> " + jrxmlFile.getAbsolutePath());
//			JasperDesign jasperDesign = JRXmlLoader.load(jrxmlFile.getAbsolutePath());
//			String desinationPath = "/Users/kaleeswaran/work/photon-infotech/2.2/plugins/report-phresco-plugin/src/main/resources/" + FilenameUtils.removeExtension(jrxmlFile.getName()) + ".jasper";
//			System.out.println("desinationPath ====> " + desinationPath);
//			JasperCompileManager.compileReportToFile(jasperDesign, desinationPath);
//		}
//		
//	}

}
