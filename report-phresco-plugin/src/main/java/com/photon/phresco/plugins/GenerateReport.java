package com.photon.phresco.plugins;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.lowagie.text.pdf.PdfContentByte;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.plugins.util.PluginPackageUtil;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.model.Model;
import com.phresco.pom.model.Model.Profiles;
import com.phresco.pom.model.Profile;
import com.phresco.pom.util.PomProcessor;

public class GenerateReport implements PluginConstants {
	private static final String PHRESCO_UNIT_TEST = "phresco.unitTest";
	private static final String REPORTS_TYPE = "reportsDataType";
	private static final String PROJECT_NAME = "projectName";
	private static final String LOGO = "logo";
	private static final String PDF_PROJECT_CODE = "projectCode";
	private static final String MMM_DD_YYYY_HH_MM = "MMM dd yyyy HH.mm";
	private MavenProject mavenProject;
	private File baseDir;
	private Log log;
	private PluginPackageUtil util;

	private static final String INDEX = "index";
	private static final String XHTML = "xhtml";
	
	// TODO: tech name and project code
	private String techName = "";
	private String projectCode = null;
	
	private String testType = null;
	private String version = null;
	private String projName = null;
	private String appDir = "";
    private String reportType = null;
    private String sonarUrl = null;
	private boolean isClangReport;
	private boolean showDeviceReport;
	
	// logo and theme objects
	private String logo = null;
	private Map<String, String> theme = null;
	
    //test suite details
	private float noOfTstSuiteTests = 0;
    private float noOfTstSuiteFailures = 0;
    private float noOfTstSuiteErrors = 0;
    
    private String fileName = null;
    
    String REPORTS_JASPER  = "";
    
    public GenerateReport() {
    	final Date today = Calendar.getInstance().getTime();
        final DateFormat yymmdd = new SimpleDateFormat(MMM_DD_YYYY_HH_MM);
        this.fileName = yymmdd.format(today);
    }
    
	public void generatePdfReport()  throws PhrescoException {
		try {
			// Report generation for unit and functional
			if (UNIT.equals(testType) || FUNCTIONAL.equals(testType)) {
				List<String> modules = mavenProject.getModules();
				boolean isMultiModuleProject = false;
				if (CollectionUtils.isNotEmpty(modules)) {
					isMultiModuleProject = true;
				}
				
				//crisp and detail view report generation
				if (isMultiModuleProject) {
					// multi module project....
					List<ModuleSureFireReport> moduleWiseReports = new ArrayList<ModuleSureFireReport>();
					for (String module : modules) {
						ModuleSureFireReport msr = new ModuleSureFireReport();
						SureFireReport sureFireReports = sureFireReports(module);
						
						List<TestSuite> testSuites = sureFireReports.getTestSuites();
						if (CollectionUtils.isNotEmpty(testSuites)) {
							msr.setModuleOrTechName(module);
							msr.setModuleOrTechLabel("Module Name");
							msr.setSureFireReport(Arrays.asList(sureFireReports));
							moduleWiseReports.add(msr);
						}
					}
					generateUnitAndFunctionalReport(moduleWiseReports);
				} else {
					// none module projects....
					SureFireReport sureFireReports = sureFireReports(null);
					generateUnitAndFunctionalReport(sureFireReports);				
				}
			// Report generation for performance
			} else if (PERFORMACE.equals(testType)) {
				boolean deviceReportAvail = isDeviceReportAvail();
				showDeviceReport = deviceReportAvail;
				if(showDeviceReport) { //showDeviceReport
					//android technology reports 
					List<AndroidPerfReport> jmeterTestResultsForAndroid = getJmeterTestResultsForAndroid();
					generateAndroidPerformanceReport(jmeterTestResultsForAndroid);
				} else {
					ArrayList<JmeterTypeReport> jmeterTestResults = getJmeterTestResults();
					// Performance test report generation
					generateJmeterPerformanceReport(jmeterTestResults);
				}
			}  else if (LOAD.equals(testType)) {
				List<LoadTestReport> loadTestResults = getLoadTestResults();
				// Load test report generation
				generateLoadTestReport(loadTestResults);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new PhrescoException(e);
		}
	}
	
	//Consolidated report for all test
	public void cumalitiveTestReport() throws Exception {
		log.debug("Entering GenerateReport.cumalitiveTestReport()");
		try {
			Map<String, Object> cumulativeReportparams = new HashMap<String,Object>();
			//unit and functional details
			testType = UNIT;
			
			boolean isMultiModuleProject = false;
			List<String> modules = mavenProject.getModules();
			if (CollectionUtils.isNotEmpty(modules)) {
				isMultiModuleProject = true;
			}
			
			//crisp and detail view report generation
			ModuleSureFireReport msr = null;
			if (isMultiModuleProject) {
				// multi module project....
				List<ModuleSureFireReport> moduleWiseReports = new ArrayList<ModuleSureFireReport>();
				for (String module : modules) {
					msr = new ModuleSureFireReport();
					SureFireReport sureFireReports = sureFireReports(module);
					
					List<TestSuite> testSuites = sureFireReports.getTestSuites();
					if (CollectionUtils.isNotEmpty(testSuites)) {
						msr.setModuleOrTechName(module);
						msr.setModuleOrTechLabel("Module Name");
						msr.setSureFireReport(Arrays.asList(sureFireReports));
						moduleWiseReports.add(msr);
					}
				}
				
				cumulativeReportparams.put("isMultiModuleProject", true);
				cumulativeReportparams.put("multiModuleUnitTestReports", moduleWiseReports);
			} else {
				SureFireReport unitTestSureFireReports = null;
				unitTestSureFireReports = sureFireReports(null);
				cumulativeReportparams.put("isMultiModuleProject", false);
				cumulativeReportparams.put(UNIT_TEST_REPORTS, Arrays.asList(unitTestSureFireReports));
			}
			
			testType = FUNCTIONAL;
			SureFireReport functionalSureFireReports = null;
			functionalSureFireReports = sureFireReports(null);
			
			testType = "";
			//performance details
			List<AndroidPerfReport> jmeterTestResultsForAndroid = null;
			ArrayList<JmeterTypeReport> jmeterTestResults = null;
			boolean deviceReportAvail = isDeviceReportAvail();
			showDeviceReport = deviceReportAvail;
			
			if(showDeviceReport) { //showDeviceReport
				jmeterTestResultsForAndroid = getJmeterTestResultsForAndroid();
			} else {
				jmeterTestResults = getJmeterTestResults();
			}
			
			//load test details
			List<LoadTestReport> loadTestResults = getLoadTestResults();
			
			cumulativeReportparams.put(PDF_PROJECT_CODE, projectCode);
			cumulativeReportparams.put(PROJECT_NAME, projName);
			cumulativeReportparams.put(TECH_NAME, techName);
			cumulativeReportparams.put(VERSION, version);
			cumulativeReportparams.put(LOGO, logo);
			
			cumulativeReportparams.put(REPORTS_TYPE, reportType);
			cumulativeReportparams.put(FUNCTIONAL_TEST_REPORTS, Arrays.asList(functionalSureFireReports));
			
			if(deviceReportAvail) {
				cumulativeReportparams.put(PERFORMANCE_SPECIAL_HANDLE, true);
				cumulativeReportparams.put(PERFORMANCE_TEST_REPORTS, jmeterTestResultsForAndroid);
			} else {
				cumulativeReportparams.put(PERFORMANCE_SPECIAL_HANDLE, false);
				cumulativeReportparams.put(PERFORMANCE_TEST_REPORTS, jmeterTestResults);
			}
			cumulativeReportparams.put(LOAD_TEST_REPORTS, loadTestResults);
			
			if (!isClangReport) {
				//Sonar details
				List<SonarReport> sonarReports = new ArrayList<SonarReport>();
				String pomPath =  baseDir + File.separator + POM_XML;
				if (StringUtils.isNotEmpty(sonarUrl)) {
					List<String> sonarTechReports = getSonarProfiles(pomPath);
					if (sonarTechReports != null) {
						if(CollectionUtils.isEmpty(sonarTechReports)) {
							sonarTechReports.add(SONAR_SOURCE);
						}
						sonarTechReports.add(FUNCTIONAL);
						for (String sonarTechReport : sonarTechReports) {
							SonarReport srcSonarReport = generateSonarReport(sonarTechReport);
							if(srcSonarReport != null) {
								sonarReports.add(srcSonarReport);
							}
						}
						cumulativeReportparams.put("sonarReport", sonarReports);
					}
				}
			}
			
			generateCumulativeTestReport(cumulativeReportparams);
		} catch (Exception e) {
			log.error("Report generation errorr ");
			throw new PhrescoException(e);
		}
	}
	
	//cumulative test report generation
	public void generateCumulativeTestReport(Map<String, Object> cumulativeReportparams) throws PhrescoException {
		log.debug("Entering Method PhrescoReportGeneration.generateCumulativeTestReport()");
		InputStream reportStream = null;
		BufferedInputStream bufferedInputStream = null;
		String uuid = UUID.randomUUID().toString();
		String outFileNamePDF = "";
		String semiPath = File.separator + baseDir.getName() + STR_UNDERSCORE + reportType + STR_UNDERSCORE + fileName + DOT + PDF;
		try {
			if (isClangReport) {
				outFileNamePDF = Utility.getPhrescoTemp() + uuid + semiPath;
			} else {
				outFileNamePDF = baseDir + File.separator + DO_NOT_CHECKIN_FOLDER + File.separator + ARCHIVES + File.separator + CUMULATIVE + semiPath;
			}
			
			new File(outFileNamePDF).getParentFile().mkdirs();
			String jasperFile = "PhrescoCumulativeReport.jasper";
			reportStream = this.getClass().getClassLoader().getResourceAsStream(REPORTS_JASPER + jasperFile);
			bufferedInputStream = new BufferedInputStream(reportStream);
			JREmptyDataSource  dataSource = new JREmptyDataSource();
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, cumulativeReportparams, dataSource);
			//applying theme
			applyTheme(jasperPrint);
			JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter(); 
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileNamePDF);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.exportReport();
			
			if (isClangReport) {
				String outFinalFileNamePDF = baseDir + File.separator + DO_NOT_CHECKIN_FOLDER + File.separator + ARCHIVES + File.separator + CUMULATIVE + semiPath;
				new File(outFinalFileNamePDF).getParentFile().mkdirs();
				try {
					iphoneSonarHtmlToPdf(uuid);
		            List<String> pdfs = new ArrayList<String>();
		         // get all pdf from that uuid location 
		            String codeValidationPdfs = Utility.getPhrescoTemp() + uuid;
		            File codeValidationsPdfDir = new File(codeValidationPdfs);
		            if (codeValidationsPdfDir.exists()) {
			            String[] extensions = new String[] { PDF };
			            List<File> codeReportPdfs = (List<File>) FileUtils.listFiles(codeValidationsPdfDir, extensions, false);
			            
			            if (codeReportPdfs != null && codeReportPdfs.size() == 0) {
			            	FileUtils.copyFile(new File(outFileNamePDF), new File(outFinalFileNamePDF));
			            } else {
				            for (File codeReportPdf : codeReportPdfs) {
					            pdfs.add(codeReportPdf.getAbsolutePath());
							}
				            mergePdf(pdfs, outFinalFileNamePDF);
//				            concatPDFs(pdfs, outFinalFileNamePDF, true);
			            }
		            }
				} catch (PhrescoException e) {
					// just copy generated generated pdf to archive folder
					FileUtils.copyFile(new File(outFileNamePDF), new File(outFinalFileNamePDF));
				}
			}
			try {
				FileUtils.deleteDirectory(new File(Utility.getPhrescoTemp() + uuid));
			} catch (Exception e) {
			}
		} catch(Exception e) {
			log.error("Report generation error ");
			throw new PhrescoException(e);
		} finally {
			if (reportStream != null) {
				try {
					reportStream.close();
				} catch (IOException e) {
					log.error("Report generation error ");
				}
			}
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					log.error("Report generation error ");
				}
			}
		}
		
	}
	
	// merge pdfs
	public void mergePdf(List<String> PDFFiles,  String outputPDFFile) throws PhrescoException {
		try {
			  // Get the byte streams from any source (maintain order)
			  List<InputStream> sourcePDFs = new ArrayList<InputStream>();
			  for (String PDFFile : PDFFiles) {
				  sourcePDFs.add(new FileInputStream(new File(PDFFile)));
			  }
			  // initialize the Merger utility and add pdfs to be merged
			  PDFMergerUtility mergerUtility = new PDFMergerUtility();
			  mergerUtility.addSources(sourcePDFs);
			  // set the destination pdf name and merge input pdfs
			  mergerUtility.setDestinationFileName(outputPDFFile);
			  mergerUtility.mergeDocuments();
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	//Merge the PDF's
	public void concatPDFs(List<String> PDFFiles,  String outputPDFFile, boolean paginate) {
		log.debug("Entering Method PhrescoReportGeneration.concatPDFs()");
        OutputStream outputStream = null;
		com.lowagie.text.Document document = null;
        try {
    		List<InputStream> pdfs = new ArrayList<InputStream>();
    		for (String PDFFile : PDFFiles) {
    	        pdfs.add(new FileInputStream(new File(PDFFile)));
    		}
    		
    		outputStream = new FileOutputStream(outputPDFFile);
    		document = new com.lowagie.text.Document();
    		
            List<com.lowagie.text.pdf.PdfReader> readers = new ArrayList<com.lowagie.text.pdf.PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();
 
            // Create Readers for the pdfs.
            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                com.lowagie.text.pdf.PdfReader pdfReader = new com.lowagie.text.pdf.PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }
            // Create a writer for the outputstream
            com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, outputStream);
 
            document.open();
            com.lowagie.text.pdf.BaseFont bf = com.lowagie.text.pdf.BaseFont.createFont(com.lowagie.text.pdf.BaseFont.HELVETICA,
            		com.lowagie.text.pdf.BaseFont.CP1252, com.lowagie.text.pdf.BaseFont.NOT_EMBEDDED);
            com.lowagie.text.pdf.PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
            // data
 
            com.lowagie.text.pdf.PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<com.lowagie.text.pdf.PdfReader> iteratorPDFReader = readers.iterator();
 
            // Loop through the PDF files and add to the output.
            while (iteratorPDFReader.hasNext()) {
            	com.lowagie.text.pdf.PdfReader pdfReader = iteratorPDFReader.next();
 
                // Create a new page in the target for each source page.
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader,  pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);
 
                    // Code for pagination.
                    if (paginate) {
                        cb.beginText();
                        cb.setFontAndSize(bf, 9);
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, ""
                                + currentPageNumber + " of " + totalPages, 520,
                                5, 0);
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ioe) {
            	ioe.printStackTrace();
            }
        }
    }
	
	//Conversion of Html to Pdf
	public void iphoneSonarHtmlToPdf(String uuid) throws PhrescoException {
		log.debug("Entering Method  PhrescoReportGeneration.iphoneSonarHtmlToPdf()");
		try {
			// convert all html to xhmt and to pdf
			
			StringBuilder codeValidatePath = new StringBuilder(baseDir.getAbsolutePath());
	    	codeValidatePath.append(mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_VALIDATE_REPORT));
	    	
	    	File codeValidationReportDir = new File(codeValidatePath.toString());
	        
	        if(!codeValidationReportDir.exists()) {
	        	return;
	        }
	    	// if static analysis report dir is available 
	    	List<File> targetFiles = null;
	    	if (codeValidationReportDir.exists() && codeValidationReportDir.isDirectory()) {
				targetFiles = new ArrayList<File>();
				File[] listFiles = codeValidationReportDir.listFiles();
				for (File targrtDir : listFiles) {
					File targetIndexFile = new File(targrtDir, INDEX_HTML);
					if (targrtDir.isDirectory() && targetIndexFile.exists()) {
						targetFiles.add(targetIndexFile);
					}
				}
	    	}
	    	
	    	for (int i = 0; i < targetFiles.size(); i++) {
	    		String tempOutFileNameXHTML = Utility.getPhrescoTemp() + uuid + File.separator + projectCode + STR_UNDERSCORE + INDEX + i + DOT + XHTML;
	    		String tempOutFileNameIphoneSonarPDF = Utility.getPhrescoTemp() + uuid + File.separator + projectCode + STR_UNDERSCORE + fileName + i + DOT + PDF;
	    		File targetFile = targetFiles.get(i);
		        CleanerProperties props = new CleanerProperties();
		        props.setTranslateSpecialEntities(true);
		        props.setTransResCharsToNCR(true);
		        props.setOmitComments(true);
		        
		        //checking of starting and ending tags are in proper
		        TagNode tagNode = new HtmlCleaner(props).clean(targetFile);
		        new PrettyXmlSerializer(props).writeToFile(tagNode, tempOutFileNameXHTML, UTF_8);
		        
		        File xhtmlpath = new File(tempOutFileNameXHTML);
		        File pdfPath = new File(tempOutFileNameIphoneSonarPDF);
		        com.itextpdf.text.Document pdfDocument = null;
		        PdfWriter pdfWriter = null;
		        pdfDocument = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A2); // TODO : issue need to be fixed in html
//		        pdfDocument = new com.itextpdf.text.Document();
		        pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfPath));
		        pdfDocument.open();
		        
		        // Adding heading for the pdf
		        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD);
		        pdfDocument.add(new Paragraph("XCode Target : " + targetFile.getParentFile().getName(), catFont));
		        
		        XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, (com.itextpdf.text.Document) pdfDocument,
		                new FileInputStream(xhtmlpath), null);
		        pdfDocument.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new PhrescoException(e);
		}
	}
	
	 public SonarReport generateSonarReport(String report) throws PhrescoException {
		log.debug("Entering Method PhrescoReportGeneration.generateSonarReport()");
		SonarReport sonarReport = null;
		String serverUrl = "";
		try {
			StringBuilder builder = new StringBuilder(baseDir.getAbsolutePath());
            if (StringUtils.isNotEmpty(report) && FUNCTIONALTEST.equals(report)) {
                builder.append(mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR));
            }
            
            builder.append(File.separatorChar);
        	builder.append(POM_XML);
        	log.debug("Sonar pom path => " + builder.toString());
        	File pomPath = new File(builder.toString());
        	
        	PomProcessor processor = new PomProcessor(pomPath);
        	String groupId = processor.getModel().getGroupId();
        	String artifactId = processor.getModel().getArtifactId();
        	StringBuilder sbuild = new StringBuilder();
        	sbuild.append(groupId);
        	sbuild.append(COLON);
        	sbuild.append(artifactId);
        	if (StringUtils.isNotEmpty(report) && !SONAR_SOURCE.equals(report)) {
        		sbuild.append(COLON);
        		sbuild.append(report);
        	}
        	
        	String artifact = sbuild.toString();
			Sonar sonar = new Sonar(new HttpClient4Connector(new Host(sonarUrl)));
			
			//metric key parameters for sonar 
			String metrickey[] = {"ncloc", "lines", "files", "comment_lines_density" , "comment_lines", "duplicated_lines_density", "duplicated_lines", 
					"duplicated_blocks", "duplicated_files", "function_complexity", "file_complexity", "violations_density", "blocker_violations", 
					"critical_violations", "major_violations", "minor_violations", "info_violations", "weighted_violations",
					"classes", "functions",
					"statements","packages", "accessors", "public_documented_api_density", "public_undocumented_api","package_tangle_index","package_cycles", "package_feedback_edges", "package_tangles", "lcom4", "rfc",
					"directories", "class_complexity", "comment_blank_lines", "coverage", "uncovered_lines"};

			String methodkey[] = {"nonCommentLinesOfCode", "lines", "files", "commentLinesDensity" , "commentLines", "duplicatedLinesDensity", "duplicatedLines", 
					"duplicatedBlocks", "duplicatedFiles", "functionComplexity", "fileComplexity", "violationsDensity", "blockerViolations", 
					"criticalViolations", "majorViolations", "minorViolations", "infoViolations", "weightedViolations",
					"classes", "functions",
					"statements","packages", "accessors", "publicDocumentedApiDensity", "publicUndocumentedApi","packageTangleIndex","packageCycles", "packageFeedbackEdges", "packageTangles", "lackOfCohesionMethods", "responseForCode",
					"directories", "classComplexity", "commentBlankLines", "coverage", "uncoveredLines"};
			Resource resrc = sonar.find(ResourceQuery.createForMetrics(artifact, metrickey));
			BeanUtils bu = new BeanUtils();
			if (resrc != null) {
				sonarReport = new SonarReport();
				for (int i = 0; i < metrickey.length; i++) {
					Measure measure = resrc.getMeasure(metrickey[i]);
					if (measure != null) {
						String formattedValue = resrc.getMeasure(metrickey[i]).getFormattedValue();
						bu.setProperty(sonarReport, methodkey[i], formattedValue);
					} 
				}
				sonarReport.setReportType(report);
			}
			return sonarReport;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Unit and functional pdf report generation
	public void generateUnitAndFunctionalReport(SureFireReport sureFireReports)  throws PhrescoException {
		InputStream reportStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			String outFileNamePDF = Utility.getProjectHome() + appDir + DO_NOT_CHECKIN_FOLDER + File.separator + ARCHIVES + File.separator + testType + File.separator + testType  + STR_UNDERSCORE + reportType + STR_UNDERSCORE + fileName + DOT + PDF;
			new File(outFileNamePDF).getParentFile().mkdirs();
			String containerJasperFile = "PhrescoSureFireReport.jasper";
			reportStream = this.getClass().getClassLoader().getResourceAsStream(REPORTS_JASPER + containerJasperFile);
			bufferedInputStream = new BufferedInputStream(reportStream);
			Map<String, Object> parameters = new HashMap<String,Object>();
			parameters.put(PDF_PROJECT_CODE, projectCode);
			parameters.put(PROJECT_NAME, projName);
			parameters.put(TECH_NAME, techName);
			parameters.put(TEST_TYPE, testType.toUpperCase());
			parameters.put(REPORTS_TYPE, reportType);
			parameters.put(VERSION, version);
			parameters.put(LOGO, logo);
			JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(new SureFireReport[]{sureFireReports});
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			// applying theme
			applyTheme(jasperPrint);
			JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter(); 
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileNamePDF);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.exportReport();
		} catch(Exception e) {
			log.error("Unit and functional  generation error");
			throw new PhrescoException(e);
		} finally {
			if (reportStream != null) {
				try {
					reportStream.close();
				} catch (IOException e) {
					log.error("Report generation errorr ");
				}
			}
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error("Report generation errorr ");
				}
			}
		}
	}
	
	// Unit and functional pdf report generation
	public void generateUnitAndFunctionalReport(List<ModuleSureFireReport> moduleWiseReports)  throws PhrescoException {
		InputStream reportStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			String outFileNamePDF = Utility.getProjectHome() + appDir + DO_NOT_CHECKIN_FOLDER + File.separator + ARCHIVES + File.separator + testType + File.separator + testType  + STR_UNDERSCORE + reportType + STR_UNDERSCORE + fileName + DOT + PDF;
			new File(outFileNamePDF).getParentFile().mkdirs();
			String containerJasperFile = "PhrescoModuleSureFireReport.japer";
			reportStream = this.getClass().getClassLoader().getResourceAsStream("PhrescoModuleSureFireReport.jasper");
			bufferedInputStream = new BufferedInputStream(reportStream);
			Map<String, Object> parameters = new HashMap<String,Object>();
			parameters.put(PDF_PROJECT_CODE, projectCode);
			parameters.put(PROJECT_NAME, projName);
			parameters.put(TECH_NAME, techName);
			parameters.put(TEST_TYPE, testType);
			parameters.put(REPORTS_TYPE, reportType);
			parameters.put(VERSION, version);
			parameters.put(LOGO, logo);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(moduleWiseReports);
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);
//			JasperDesign jasperDesign = JRXmlLoader.load(bufferedInputStream);
//			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			// applying theme
			applyTheme(jasperPrint);
			JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter(); 
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileNamePDF);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.exportReport();
		} catch(Exception e) {
			log.error("Unit and functional  generation error");
			throw new PhrescoException(e);
		} finally {
			if (reportStream != null) {
				try {
					reportStream.close();
				} catch (IOException e) {
					log.error("Report generation errorr ");
				}
			}
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error("Report generation errorr ");
				}
			}
		}
	}
	
	// performance test report
	public void generateJmeterPerformanceReport(ArrayList<JmeterTypeReport> jmeterTestResults)  throws PhrescoException {
		try {
			ArrayList<JmeterTypeReport> jmeterTstResults = jmeterTestResults;
			String outFileNamePDF = Utility.getProjectHome() + appDir + DO_NOT_CHECKIN_FOLDER + File.separator + ARCHIVES + File.separator + testType + File.separator + testType  + STR_UNDERSCORE + reportType + STR_UNDERSCORE + fileName + DOT + PDF;
			String jasperFile = "PhrescoPerfContain.jasper";
			Map<String, Object> parameters = new HashMap<String,Object>();
			parameters.put(PDF_PROJECT_CODE, projectCode);
			parameters.put(PROJECT_NAME, projName);
			parameters.put(TECH_NAME, techName);
			parameters.put(TEST_TYPE, testType.toUpperCase());
			parameters.put(REPORTS_TYPE, reportType);
			parameters.put(VERSION, version);
			parameters.put(LOGO, logo);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(jmeterTstResults);
			reportGenerate(outFileNamePDF, jasperFile, parameters, dataSource);
		} catch (Exception e) {
			log.error("Performance  generation error ");
			throw new PhrescoException(e);
		}
	}
	
	// performance test report
	public void generateAndroidPerformanceReport(List<AndroidPerfReport> androidPerReports)  throws PhrescoException {
		try {
			String outFileNamePDF = Utility.getProjectHome() + appDir + DO_NOT_CHECKIN_FOLDER + File.separator + ARCHIVES + File.separator + testType + File.separator + testType + STR_UNDERSCORE + reportType + STR_UNDERSCORE + fileName + DOT + PDF;
			String jasperFile = "PhrescoAndroidPerfContain.jasper";
			Map<String, Object> parameters = new HashMap<String,Object>();
			parameters.put(PDF_PROJECT_CODE, projectCode);
			parameters.put(PROJECT_NAME, projName);
			parameters.put(TECH_NAME, techName);
			parameters.put(TEST_TYPE, testType.toUpperCase());
			parameters.put(REPORTS_TYPE, reportType);
			parameters.put(VERSION, version);
			parameters.put(LOGO, logo);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(androidPerReports);
			reportGenerate(outFileNamePDF, jasperFile, parameters, dataSource);
		} catch (Exception e) {
			log.error("Android Performance  generation error ");
			throw new PhrescoException(e);
		}
	}
	
	// load test report
	public void generateLoadTestReport(List<LoadTestReport> loadTestResults)  throws PhrescoException {
		try {
			String outFileNamePDF = Utility.getProjectHome() + appDir + DO_NOT_CHECKIN_FOLDER + File.separator + ARCHIVES + File.separator + testType + File.separator + testType + STR_UNDERSCORE + reportType + STR_UNDERSCORE + fileName + DOT + PDF;
			String jasperFile = "PhrescoLoadTestContain.jasper";
			Map<String, Object> parameters = new HashMap<String,Object>();
			parameters.put(PDF_PROJECT_CODE, projectCode);
			parameters.put(PROJECT_NAME, projName);
			parameters.put(TECH_NAME, techName);
			parameters.put(TEST_TYPE, testType.toUpperCase());
			parameters.put(REPORTS_TYPE, reportType);
			parameters.put(VERSION, version);
			parameters.put(LOGO, logo);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(loadTestResults);
			reportGenerate(outFileNamePDF, jasperFile, parameters, dataSource);
		} catch (Exception e) {
			log.error("Load report generation error");
			throw new PhrescoException(e);
		}
	}
	
	public void reportGenerate(String outFileNamePDF, String jasperFile, Map<String, Object> parameters, JRBeanCollectionDataSource dataSource) throws PhrescoException {
		InputStream reportStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			new File(outFileNamePDF).getParentFile().mkdirs();
			reportStream = this.getClass().getClassLoader().getResourceAsStream(REPORTS_JASPER + jasperFile);
			bufferedInputStream = new BufferedInputStream(reportStream);
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			// applying theme
			applyTheme(jasperPrint);
			JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter(); 
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileNamePDF);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.exportReport(); 
		} catch (Exception e) {
			log.error("Load report generation error");
			throw new PhrescoException(e);
		} finally {
			if (reportStream != null) {
				try {
					reportStream.close();
				} catch (IOException e) {
					log.error("Report generation errorr ");
				}
			}
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					log.error("Report generation errorr ");
				}
			}
		}
	}
	
	public List<LoadTestReport> getLoadTestResults()  throws Exception {
		List<String> testResultsTypes = new ArrayList<String>();
        testResultsTypes.add("server");
        testResultsTypes.add("webservice");
        
		List<LoadTestReport> loadTestReports = new ArrayList<LoadTestReport>();
		String reportFilePath = baseDir + mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_LOADTEST_RPT_DIR);
		
		List<File> testResultFiles = new ArrayList<File>();
		
		// if the load is having dir_type key
		if (reportFilePath.contains(DIR_TYPE)) {
			for(String loadType: testResultsTypes) {
				Pattern p = Pattern.compile(DIR_TYPE);
                Matcher matcher = p.matcher(reportFilePath);
                String loadReportFilePath = matcher.replaceAll(loadType);
                List<File> resultFiles = getTestResultFilesAsList(loadReportFilePath);
                if (CollectionUtils.isNotEmpty(resultFiles)) {
                	testResultFiles.addAll(resultFiles);
                }
			}
		} else {
			List<File> resultFiles = getTestResultFilesAsList(reportFilePath);
            if (CollectionUtils.isNotEmpty(resultFiles)) {
            	testResultFiles.addAll(resultFiles);
            }
		}
		
		for (File resultFile : testResultFiles) {
			Document doc = getDocumentOfFile(resultFile);
			List<TestResult> loadTestResults = getLoadTestResult(doc);
			for (TestResult testResult : loadTestResults) {
//				log.info("testResult name .. " + testResult.getThreadName());
			}
			// Adding report data to bean object
			LoadTestReport loadTestReport = new LoadTestReport();
			loadTestReport.setFileName(resultFile.getName());
			loadTestReport.setTestResults(loadTestResults);
			loadTestReports.add(loadTestReport);
		}
		return loadTestReports;
	}
	
	public ArrayList<JmeterTypeReport> getJmeterTestResults() throws Exception {
        List<String> testResultsTypes = new ArrayList<String>();
        testResultsTypes.add("server");
        testResultsTypes.add("database");
        testResultsTypes.add("webservice");
        
        // List of performance test types
        ArrayList<JmeterTypeReport> jmeterTypeReports = new ArrayList<JmeterTypeReport>();
        for(String perType: testResultsTypes) {
            String performanceReportDir = baseDir + mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_PERFORMANCETEST_RPT_DIR);
            
            if (StringUtils.isNotEmpty(performanceReportDir) && StringUtils.isNotEmpty(perType)) {
                Pattern p = Pattern.compile(DIR_TYPE);
                Matcher matcher = p.matcher(performanceReportDir);
                performanceReportDir = matcher.replaceAll(perType);
            }
            List<String> testResultFiles = getTestResultFiles(performanceReportDir);
			String deviceId = null; // for android alone
			
			// List of performance test reports
			List<JmeterReport> jmeterReports = new ArrayList<JmeterReport>();
            for (String testResultFile : testResultFiles) {
            	Document document = getDocumentOfFile(performanceReportDir, testResultFile);
            	JmeterReport jmeterReport = getPerformanceReport(document, testResultFile, deviceId); // need to pass tech id and tag name
            	jmeterReports.add(jmeterReport);
			}
            // When data is not available dont show in i report
            if (!jmeterReports.isEmpty()) {
	            JmeterTypeReport jmeterTypeReport = new JmeterTypeReport();
	            jmeterTypeReport.setType(perType);
	            jmeterTypeReport.setFileReport(jmeterReports);
	            // adding final data to jmeter type reports
	            jmeterTypeReports.add(jmeterTypeReport);
            }
        }
        
        for (JmeterTypeReport jmeterTypeReport : jmeterTypeReports) {
        	String type = jmeterTypeReport.getType();
        	List<JmeterReport> fileReports = jmeterTypeReport.getFileReport();
		}
        return jmeterTypeReports;
	}
	
	public List<AndroidPerfReport> getJmeterTestResultsForAndroid() throws Exception {
        // List of performance test types
        String performanceReportDir = baseDir + mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_PERFORMANCETEST_RPT_DIR);
        List<String> testResultFiles = getTestResultFiles(performanceReportDir);
		
		// List of performance test reports
        List<AndroidPerfReport> androidPerfFilesWithDatas = new ArrayList<AndroidPerfReport>(); //kalees
        for (String testResultFile : testResultFiles) {
        	Document document = getDocumentOfFile(performanceReportDir, testResultFile);

        	Map<String, String> deviceNamesWithId = getDeviceNames(document);
        	
            Set st = deviceNamesWithId.entrySet();
            Iterator it = st.iterator();
            List<JmeterReport> androidDeviceWithDatas = new ArrayList<JmeterReport>();
            while (it.hasNext()) {
                Map.Entry m = (Map.Entry) it.next();
                String androidDeviceId = (String) m.getKey();
                String androidDeviceName = (String) m.getValue();
            	JmeterReport jmeterReport = getPerformanceReport(document, testResultFile, androidDeviceId); // need to pass tech id and tag name
            	jmeterReport.setFileName(androidDeviceName);
                androidDeviceWithDatas.add(jmeterReport);
            }
            AndroidPerfReport androidPerReport = new AndroidPerfReport();
            androidPerReport.setFileName(testResultFile);
            androidPerReport.setDeviceReport(androidDeviceWithDatas);
            androidPerfFilesWithDatas.add(androidPerReport);
		}
        
        for (AndroidPerfReport androidPerfFilesWithData : androidPerfFilesWithDatas) {
        	List<JmeterReport> deviceReports = androidPerfFilesWithData.getDeviceReport();
        	for (JmeterReport jmeterReport : deviceReports) {
//        		log.info("getTotalAvg .. " + jmeterReport.getTotalAvg());
			}
		}
        return androidPerfFilesWithDatas;
	}
	
	public boolean isDeviceReportAvail() throws Exception {
        // List of performance test types
        String performanceReportDir = baseDir + mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_PERFORMANCETEST_RPT_DIR);
        List<String> testResultFiles = getTestResultFiles(performanceReportDir);
		
		// List of performance test reports
        for (String testResultFile : testResultFiles) {
        	Document document = getDocumentOfFile(performanceReportDir, testResultFile);
        	Map<String, String> deviceNamesWithId = getDeviceNames(document);
        	if (MapUtils.isNotEmpty(deviceNamesWithId)) {
        		return true;
        	}
		}
        return false;
	}
	
	// unit and functional test report
	public ArrayList<XmlReport> sureFireReport() throws Exception {
		ArrayList<XmlReport> xmlReports = new ArrayList<XmlReport>();
		String reportFilePath = "";
		if (UNIT.equals(testType)) {
			reportFilePath = baseDir + mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_DIR);
		} else {
			reportFilePath = baseDir + mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_DIR);
		}
		List<String> testResultFiles = getTestResultFiles(reportFilePath);
		for (String resultFile : testResultFiles) {
			XmlReport xmlReport = new XmlReport();
			xmlReport.setFileName(resultFile);
			ArrayList<TestSuite> testSuiteWithTestCase = new ArrayList<TestSuite>();
			Document doc = getDocumentOfFile(reportFilePath, resultFile);
			List<TestSuite> testSuites = getTestSuite(doc);
			for (TestSuite testSuite : testSuites) {	// test suite ll have graph details
				List<TestCase> testCases = getTestCases(doc, testSuite.getName());
				testSuite.setTestCases(testCases);
				testSuiteWithTestCase.add(testSuite);
			}
			xmlReport.setTestSuites(testSuiteWithTestCase);
			xmlReports.add(xmlReport);
		}
		return xmlReports;
	}
	
	// unit and functional test report
	public SureFireReport sureFireReports(String module) throws Exception {

		Map<String, String> reportDirWithTestSuitePath = new HashMap<String, String>(); // <file
																						// -
																						// testsuitePath,testcasePath>
		if (UNIT.equals(testType)) {
			String reportFilePath = baseDir.getAbsolutePath();
			if (StringUtils.isNotEmpty(module)) {
				reportFilePath = reportFilePath + File.separatorChar + module;
			}
			getUnitTestXmlFilesAndXpaths(reportFilePath, reportDirWithTestSuitePath);
		} else {
			String reportFilePath = baseDir.getAbsolutePath();
			String functionalTestDir = mavenProject.getProperties()
					.getProperty(Constants.POM_PROP_KEY_FUNCTEST_RPT_DIR);
			String unitTestSuitePath = mavenProject.getProperties().getProperty(
					Constants.POM_PROP_KEY_FUNCTEST_TESTSUITE_XPATH);
			String unitTestCasePath = mavenProject.getProperties().getProperty(
					Constants.POM_PROP_KEY_FUNCTEST_TESTCASE_PATH);
			String reportPath = "";
			if (StringUtils.isNotEmpty(functionalTestDir)) {
				reportPath = reportFilePath + functionalTestDir;
			}
			if (StringUtils.isNotEmpty(module)) {
				reportFilePath = reportFilePath + File.separatorChar + module;
			}
			List<File> testResultFiles = getTestResultFilesAsList(reportPath);
			for (File testResultFile : testResultFiles) {
				reportDirWithTestSuitePath.put(testResultFile.getPath(), unitTestSuitePath + "," + unitTestCasePath);
			}
		}

		SureFireReport sureFireReport = new SureFireReport();
		ArrayList<TestSuite> testSuiteWithTestCase = null;
		ArrayList<AllTestSuite> allTestSuiteDetails = null;
		// detailed information object
		testSuiteWithTestCase = new ArrayList<TestSuite>();
		// crisp information of the test
		allTestSuiteDetails = new ArrayList<AllTestSuite>();

		// Iterate over each file
		// testsuite path and testcase path - kalees
		for (Map.Entry entry : reportDirWithTestSuitePath.entrySet()) {
			String mapKey = (String) entry.getKey();
			String mapValue = (String) entry.getValue();
//			log.info("key .. " + entry.getKey());
//			log.info("Value .. " + entry.getValue());
			String[] testsuiteAndTestcasePath = mapValue.split(",");
			File reportFile = new File(mapKey);
			String testSuitePath = testsuiteAndTestcasePath[0];
			String testCasePath = testsuiteAndTestcasePath[1];

			Document doc = getDocumentOfFile(reportFile);
			if (doc == null) {
				// if doc is null, the file does not have any values (i.e) zero
				// bytes.
				continue;
			}

			List<TestSuite> testSuites = getTestSuite(doc, testSuitePath);

			// crisp info
			float totalTestSuites = 0;
			float successTestSuites = 0;
			float failureTestSuites = 0;
			float errorTestSuites = 0;

			for (TestSuite testSuite : testSuites) { // test suite ll have graph
														// details
				List<TestCase> testCases = getTestCases(doc, testSuite.getName(), testSuitePath, testCasePath);

				float tests = 0;
				float failures = 0;
				float errors = 0;
				failures = getNoOfTstSuiteFailures();
				errors = getNoOfTstSuiteErrors();
				tests = getNoOfTstSuiteTests();
				float success = 0;

				if (failures != 0 && errors == 0) {
					if (failures > tests) {
						success = failures - tests;
					} else {
						success = tests - failures;
					}
				} else if (failures == 0 && errors != 0) {
					if (errors > tests) {
						success = errors - tests;
					} else {
						success = tests - errors;
					}
				} else if (failures != 0 && errors != 0) {
					float failTotal = (failures + errors);
					if (failTotal > tests) {
						success = failTotal - tests;
					} else {
						success = tests - failTotal;
					}
				} else {
					success = tests;
				}

				totalTestSuites = totalTestSuites + tests;
				failureTestSuites = failureTestSuites + failures;
				errorTestSuites = errorTestSuites + errors;
				successTestSuites = successTestSuites + success;
				String rstValues = tests + "," + success + "," + failures + "," + errors;
//				log.info("rstValues ... " + rstValues);
				AllTestSuite allTestSuiteDetail = new AllTestSuite(testSuite.getName(), tests, success, failures,
						errors);
				allTestSuiteDetails.add(allTestSuiteDetail);

				testSuite.setTestCases(testCases);
				testSuiteWithTestCase.add(testSuite);
			}
			// }
		}
		// detailed info
		sureFireReport.setTestSuites(testSuiteWithTestCase);
		// printDetailedObj(testSuiteWithTestCase);
		// crisp info
		sureFireReport.setAllTestSuites(allTestSuiteDetails);
		return sureFireReport;
	}

	private void getUnitTestXmlFilesAndXpaths(String reportFilePath,
			Map<String, String> reportDirWithTestSuitePath) {
		String unitTestDir = mavenProject.getProperties().getProperty("phresco.unitTest");
		// when it is having multiple values
		if (StringUtils.isEmpty(unitTestDir)) {
			unitTestDir = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_RPT_DIR);
			String unitTestSuitePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_TESTSUITE_XPATH);
			String unitTestCasePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_TESTCASE_PATH);
			String reportPath = reportFilePath + unitTestDir;
			List<File> testResultFiles = getTestResultFilesAsList(reportPath);
			for (File testResultFile : testResultFiles) {
				reportDirWithTestSuitePath.put(testResultFile.getPath(), unitTestSuitePath + "," + unitTestCasePath);
			}
		} else {
			List<String> unitTestTechs = Arrays.asList(unitTestDir.split(","));
			for (String unitTestTech : unitTestTechs) {
				unitTestDir = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_RPT_DIR_START + unitTestTech + Constants.POM_PROP_KEY_UNITTEST_RPT_DIR_END);
				String unitTestSuitePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_RPT_DIR_START + unitTestTech + Constants.POM_PROP_KEY_UNITTEST_TESTSUITE_XPATH_END);
				String unitTestCasePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_RPT_DIR_START + unitTestTech + Constants.POM_PROP_KEY_UNITTEST_TESTCASE_PATH_END);
				if (StringUtils.isNotEmpty(unitTestDir)) { // kalees
					String reportPath = reportFilePath + unitTestDir;
					List<File> testResultFiles = getTestResultFilesAsList(reportPath);
					for (File testResultFile : testResultFiles) {
						reportDirWithTestSuitePath.put(testResultFile.getPath(), unitTestSuitePath + "," + unitTestCasePath);
					}
				}
			}
		}
	}

	//detailed object info
	private void printDetailedObj(ArrayList<TestSuite> testSuiteWithTestCase) {
		log.debug("printing required values!!!!!");
		for (TestSuite testSuite : testSuiteWithTestCase) {
//			log.info("getName " + testSuite.getName() + " tests " + testSuite.getTests() + " Failure " + testSuite.getFailures() + " Error" + testSuite.getErrors() + " testcases size " + testSuite.getTestCases().size());
		}
	}
	
    private List<TestSuite> getTestSuite(Document doc) throws TransformerException, PhrescoException {
        try {
            String testSuitePath = null;
    		if (UNIT.equals(testType)) {
    			testSuitePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_TESTSUITE_XPATH);
    		} else {
    			testSuitePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_TESTSUITE_XPATH);
    		}
            NodeList nodelist = org.apache.xpath.XPathAPI.selectNodeList(doc, XPATH_MULTIPLE_TESTSUITE);
            if (nodelist.getLength() == 0) {
                nodelist = org.apache.xpath.XPathAPI.selectNodeList(doc, testSuitePath);
            }

            List<TestSuite> testSuites = new ArrayList<TestSuite>(2);
            TestSuite testSuite = null;

            for (int i = 0; i < nodelist.getLength(); i++) {
                testSuite =  new TestSuite();
                Node node = nodelist.item(i);
                NamedNodeMap nameNodeMap = node.getAttributes();

                for (int k = 0; k < nameNodeMap.getLength(); k++){
                    Node attribute = nameNodeMap.item(k);
                    String attributeName = attribute.getNodeName();
                    String attributeValue = attribute.getNodeValue();

                    if (ATTR_ASSERTIONS.equals(attributeName)) {
                        testSuite.setAssertions(attributeValue);
                    } else if (ATTR_ERRORS.equals(attributeName)) {
                        testSuite.setErrors(Float.parseFloat(attributeValue));
                    } else if (ATTR_FAILURES.equals(attributeName)) {
                        testSuite.setFailures(Float.parseFloat(attributeValue));
                    } else if (ATTR_FILE.equals(attributeName)) {
                        testSuite.setFile(attributeValue);
                    } else if (ATTR_NAME.equals(attributeName)) {
                        testSuite.setName(attributeValue);
                    } else if (ATTR_TESTS.equals(attributeName)) {
                        testSuite.setTests(Float.parseFloat(attributeValue));
                    } else if (ATTR_TIME.equals(attributeName)) {
                        testSuite.setTime(attributeValue);
                    }
                }
                testSuites.add(testSuite);
            }
            return testSuites;
        } catch (Exception e) {
            throw new PhrescoException(e);
        }
    }
    
	private List<TestSuite> getTestSuite(Document doc, String testSuitePath) throws TransformerException,
			PhrescoException {
		try {
			NodeList nodelist = org.apache.xpath.XPathAPI.selectNodeList(doc, XPATH_MULTIPLE_TESTSUITE);
			if (nodelist.getLength() == 0) {
				nodelist = org.apache.xpath.XPathAPI.selectNodeList(doc, testSuitePath);
			}

			List<TestSuite> testSuites = new ArrayList<TestSuite>(2);
			TestSuite testSuite = null;

			for (int i = 0; i < nodelist.getLength(); i++) {
				testSuite = new TestSuite();
				Node node = nodelist.item(i);
				NamedNodeMap nameNodeMap = node.getAttributes();

				for (int k = 0; k < nameNodeMap.getLength(); k++) {
					Node attribute = nameNodeMap.item(k);
					String attributeName = attribute.getNodeName();
					String attributeValue = attribute.getNodeValue();

					if (ATTR_ASSERTIONS.equals(attributeName)) {
						testSuite.setAssertions(attributeValue);
					} else if (ATTR_ERRORS.equals(attributeName)) {
						testSuite.setErrors(Float.parseFloat(attributeValue));
					} else if (ATTR_FAILURES.equals(attributeName)) {
						testSuite.setFailures(Float.parseFloat(attributeValue));
					} else if (ATTR_FILE.equals(attributeName)) {
						testSuite.setFile(attributeValue);
					} else if (ATTR_NAME.equals(attributeName)) {
						testSuite.setName(attributeValue);
					} else if (ATTR_TESTS.equals(attributeName)) {
						testSuite.setTests(Float.parseFloat(attributeValue));
					} else if (ATTR_TIME.equals(attributeName)) {
						testSuite.setTime(attributeValue);
					}
				}
				testSuites.add(testSuite);
			}
			return testSuites;
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

    private List<TestCase> getTestCases(Document doc, String testSuiteName) throws TransformerException, PhrescoException {
        try {
            String testCasePath = null;
            String testSuitePath = null;
    		if (UNIT.equals(testType)) {
                testSuitePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_TESTSUITE_XPATH);
                testCasePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_UNITTEST_TESTCASE_PATH);
    		} else {
                testSuitePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_TESTSUITE_XPATH);
                testCasePath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_FUNCTEST_TESTCASE_PATH);
    		}
    		
//    		log.info("testSuitePath " + testSuitePath);
//    		log.info("testCasePath " + testCasePath);
            StringBuilder sb = new StringBuilder(); //testsuites/testsuite[@name='yyy']/testcase
            //sb.append(XPATH_SINGLE_TESTSUITE);
            sb.append(testSuitePath);
            sb.append(NAME_FILTER_PREFIX);
            sb.append(testSuiteName);
            sb.append(NAME_FILTER_SUFIX);
            sb.append(testCasePath);
            //sb.append(XPATH_TESTCASE);

            NodeList nodelist = org.apache.xpath.XPathAPI.selectNodeList(doc, sb.toString());
            List<TestCase> testCases = new ArrayList<TestCase>();

            int failureTestCases = 0;
            int errorTestCases = 0;

            for (int i = 0; i < nodelist.getLength(); i++) {
                Node node = nodelist.item(i);
                NodeList childNodes = node.getChildNodes();
                NamedNodeMap nameNodeMap = node.getAttributes();
                TestCase testCase = new TestCase();

                if (childNodes != null && childNodes.getLength() > 0) {

                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);

                        if (ELEMENT_FAILURE.equals(childNode.getNodeName())) {
                        	failureTestCases++;
                            TestCaseFailure failure = getFailure(childNode);
                            if (failure != null) {
                                testCase.setTestCaseFailure(failure);
                            } 
                        }

                        if (ELEMENT_ERROR.equals(childNode.getNodeName())) {
                        	errorTestCases++;
                            TestCaseError error = getError(childNode);
                            if (error != null) {
                                testCase.setTestCaseError(error);
                            }
                        }
                    }
                }

                for (int k = 0; k < nameNodeMap.getLength(); k++){
                    Node attribute = nameNodeMap.item(k);
                    String attributeName = attribute.getNodeName();
                    String attributeValue = attribute.getNodeValue();
                    if (ATTR_NAME.equals(attributeName)) {
                        testCase.setName(attributeValue);
                    } else if (ATTR_CLASS.equals(attributeName) || ATTR_CLASSNAME.equals(attributeName)) {
                        testCase.setTestClass(attributeValue);
                    } else if (ATTR_FILE.equals(attributeName)) {
                        testCase.setFile(attributeValue);
                    } else if (ATTR_LINE.equals(attributeName)) {
                        testCase.setLine(Float.parseFloat(attributeValue));
                    } else if (ATTR_ASSERTIONS.equals(attributeName)) {
                        testCase.setAssertions(Float.parseFloat(attributeValue));
                    } else if (ATTR_TIME.equals(attributeName)) {
                        testCase.setTime(attributeValue);
                    } 
                }
                testCases.add(testCase);
            }
            
            setNoOfTstSuiteFailures(failureTestCases);
            setNoOfTstSuiteErrors(errorTestCases);
            setNoOfTstSuiteTests(nodelist.getLength());
            return testCases;
        } catch (Exception e) {
            throw new PhrescoException(e);
        }
    }
    
	private List<TestCase> getTestCases(Document doc, String testSuiteName, String testSuitePath, String testCasePath)
			throws TransformerException, PhrescoException {
		try {
//			log.info("testSuitePath " + testSuitePath);
//			log.info("testCasePath " + testCasePath);
			StringBuilder sb = new StringBuilder(); // testsuites/testsuite[@name='yyy']/testcase
			// sb.append(XPATH_SINGLE_TESTSUITE);
			sb.append(testSuitePath);
			sb.append(NAME_FILTER_PREFIX);
			sb.append(testSuiteName);
			sb.append(NAME_FILTER_SUFIX);
			sb.append(testCasePath);
			// sb.append(XPATH_TESTCASE);

			NodeList nodelist = org.apache.xpath.XPathAPI.selectNodeList(doc, sb.toString());
			List<TestCase> testCases = new ArrayList<TestCase>();

			int failureTestCases = 0;
			int errorTestCases = 0;

			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				NodeList childNodes = node.getChildNodes();
				NamedNodeMap nameNodeMap = node.getAttributes();
				TestCase testCase = new TestCase();

				if (childNodes != null && childNodes.getLength() > 0) {

					for (int j = 0; j < childNodes.getLength(); j++) {
						Node childNode = childNodes.item(j);

						if (ELEMENT_FAILURE.equals(childNode.getNodeName())) {
							failureTestCases++;
							TestCaseFailure failure = getFailure(childNode);
							if (failure != null) {
								testCase.setTestCaseFailure(failure);
							}
						}

						if (ELEMENT_ERROR.equals(childNode.getNodeName())) {
							errorTestCases++;
							TestCaseError error = getError(childNode);
							if (error != null) {
								testCase.setTestCaseError(error);
							}
						}
					}
				}

				for (int k = 0; k < nameNodeMap.getLength(); k++) {
					Node attribute = nameNodeMap.item(k);
					String attributeName = attribute.getNodeName();
					String attributeValue = attribute.getNodeValue();
					if (ATTR_NAME.equals(attributeName)) {
						testCase.setName(attributeValue);
					} else if (ATTR_CLASS.equals(attributeName) || ATTR_CLASSNAME.equals(attributeName)) {
						testCase.setTestClass(attributeValue);
					} else if (ATTR_FILE.equals(attributeName)) {
						testCase.setFile(attributeValue);
					} else if (ATTR_LINE.equals(attributeName)) {
						testCase.setLine(Float.parseFloat(attributeValue));
					} else if (ATTR_ASSERTIONS.equals(attributeName)) {
						testCase.setAssertions(Float.parseFloat(attributeValue));
					} else if (ATTR_TIME.equals(attributeName)) {
						testCase.setTime(attributeValue);
					}
				}
				testCases.add(testCase);
			}

			setNoOfTstSuiteFailures(failureTestCases);
			setNoOfTstSuiteErrors(errorTestCases);
			setNoOfTstSuiteTests(nodelist.getLength());
			return testCases;
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
    
    private TestCaseFailure getFailure(Node failureNode) throws TransformerException {
        TestCaseFailure failure = new TestCaseFailure();
        try {
            failure.setDescription(failureNode.getTextContent());
            failure.setFailureType(REQ_TITLE_EXCEPTION);
            NamedNodeMap nameNodeMap = failureNode.getAttributes();

            if (nameNodeMap != null && nameNodeMap.getLength() > 0) {
                for (int k = 0; k < nameNodeMap.getLength(); k++){
                    Node attribute = nameNodeMap.item(k);
                    String attributeName = attribute.getNodeName();
                    String attributeValue = attribute.getNodeValue();

                    if (ATTR_TYPE.equals(attributeName)) {
                        failure.setFailureType(attributeValue);
                    }
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return failure;
    }

    private TestCaseError getError(Node errorNode) throws TransformerException {
        TestCaseError tcError = new TestCaseError();
        try {
            tcError.setDescription(errorNode.getTextContent());
            tcError.setErrorType(REQ_TITLE_ERROR);
            NamedNodeMap nameNodeMap = errorNode.getAttributes();

            if (nameNodeMap != null && nameNodeMap.getLength() > 0) {
                for (int k = 0; k < nameNodeMap.getLength(); k++){
                    Node attribute = nameNodeMap.item(k);
                    String attributeName = attribute.getNodeName();
                    String attributeValue = attribute.getNodeValue();

                    if (ATTR_TYPE.equals(attributeName)) {
                        tcError.setErrorType(attributeValue);
                    }
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return tcError;
    }
    
	public JmeterReport getPerformanceReport(Document document, String fileName, String deviceId) throws Exception {  // deviceid is the tag name for android
		JmeterReport jmeterReport = new JmeterReport();
		List<PerformanceTestResult> performanceTestResultOfFile = new ArrayList<PerformanceTestResult>();
		String xpath = "/*/*";	// For other technologies
		String device = "*";
		if(StringUtils.isNotEmpty(deviceId)) {
			device = "deviceInfo[@id='" + deviceId + "']";
		}

		if(showDeviceReport) {
			xpath = "/*/" + device + "/*";
		}
		NodeList nodeList = org.apache.xpath.XPathAPI.selectNodeList(document, xpath);
		Map<String, PerformanceTestResult> results = new LinkedHashMap<String, PerformanceTestResult>(100);
		double maxTs = 0;
		double minTs = 0;
		int lastTime = 0;
		int noOfSamples = nodeList.getLength();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NamedNodeMap nameNodeMap = node.getAttributes();
			Node timeAttr = nameNodeMap.getNamedItem(ATTR_JM_TIME);
			int time = Integer.parseInt(timeAttr.getNodeValue());
			Node bytesAttr = nameNodeMap.getNamedItem(ATTR_JM_BYTES);
			int bytes = Integer.parseInt(bytesAttr.getNodeValue());
			Node successFlagAttr = nameNodeMap.getNamedItem(ATTR_JM_SUCCESS_FLAG);
			boolean success = Boolean.parseBoolean(successFlagAttr.getNodeValue()) ? true : false ;
			Node labelAttr = nameNodeMap.getNamedItem(ATTR_JM_LABEL);
			String label = labelAttr.getNodeValue();
			Node timeStampAttr = nameNodeMap.getNamedItem(ATTR_JM_TIMESTAMP);
			double timeStamp = Long.parseLong(timeStampAttr.getNodeValue());
			boolean firstEntry = false;

			PerformanceTestResult performanceTestResult = results.get(label);
			if (performanceTestResult == null) {
				performanceTestResult = new PerformanceTestResult();
				firstEntry = true;
			} else {
				firstEntry = false;
			}

			performanceTestResult.setLabel(label.trim());
			performanceTestResult.setNoOfSamples(performanceTestResult.getNoOfSamples() + 1);
			performanceTestResult.getTimes().add(time);
			performanceTestResult.setTotalTime(performanceTestResult.getTotalTime() + time);
			performanceTestResult.setTotalBytes(performanceTestResult.getTotalBytes() + bytes);

			if (time < performanceTestResult.getMin() || firstEntry) {
				performanceTestResult.setMin(time);
			}

			if (time > performanceTestResult.getMax()) {
				performanceTestResult.setMax(time);
			}

			// Error calculation
			if (!success) {
				performanceTestResult.setErr(performanceTestResult.getErr() + 1);
			}

			// Throughput calculation

			if (timeStamp >= performanceTestResult.getMaxTs()) {
				performanceTestResult.setMaxTs(timeStamp);
				performanceTestResult.setLastTime(time);
			}

			if(i == 0 || (performanceTestResult.getMaxTs() > maxTs)) {
				maxTs = performanceTestResult.getMaxTs();
				lastTime = performanceTestResult.getLastTime();
			}

			if (timeStamp < performanceTestResult.getMinTs() || firstEntry) {
				performanceTestResult.setMinTs(timeStamp);
			}

			if(i == 0 ) {
				minTs = performanceTestResult.getMinTs();
			} else if(performanceTestResult.getMinTs() < minTs) {
				minTs = performanceTestResult.getMinTs();
			}

			Double calThroughPut = new Double(performanceTestResult.getNoOfSamples());
			calThroughPut = calThroughPut / (performanceTestResult.getMaxTs() + performanceTestResult.getLastTime() -
					performanceTestResult.getMinTs());
			double throughPut = calThroughPut * 1000;

			performanceTestResult.setThroughtPut(throughPut);

			results.put(label, performanceTestResult);
		}
		
		// Total Throughput calculation
		double totalThroughput = (noOfSamples /((maxTs + lastTime) - minTs)) * 1000;
		double stdDev = setStdDevToResults(results);
		
		// Getting all performance result objects
		// calculating total values
		int totalValue = results.keySet().size();
		int NoOfSample = 0; 
		double avg = 0; 
  		int min = 0;
  		int max = 0;
  		double StdDev = 0;
  		int Err = 0;
  		double KbPerSec = 0;
  		double sumOfBytes = 0;
  		int i = 1;
  		
		for (String key : results.keySet()) {
			PerformanceTestResult performanceTestResult = results.get(key);
			performanceTestResultOfFile.add(performanceTestResult);
			
			// calculating min,max, avgbytes, kbsec
        	NoOfSample = NoOfSample + performanceTestResult.getNoOfSamples();
        	avg = avg + performanceTestResult.getAvg();
        	if (i == 1) {
        		min = performanceTestResult.getMin();
        		max = performanceTestResult.getMax();
        	}
        	if (i != 1 && performanceTestResult.getMin() < min) {
        		min = performanceTestResult.getMin();
        	}
        	if (i != 1 && performanceTestResult.getMax() > max) {
        		max = performanceTestResult.getMax();
        	}
        	StdDev = StdDev + performanceTestResult.getStdDev();
        	Err = Err + performanceTestResult.getErr();
        	sumOfBytes = sumOfBytes + performanceTestResult.getAvgBytes();
        	
        	i++;
		}
		// Calculation of avg bytes of a file
      	double avgBytes = sumOfBytes / totalValue;
      	KbPerSec = (avgBytes / 1024) * totalThroughput;
		
		// setting performance file name and list objects
		jmeterReport.setFileName(fileName);
		jmeterReport.setJmeterTestResult(performanceTestResultOfFile);
		jmeterReport.setTotalThroughput(roundFloat(2,totalThroughput)+"");
		jmeterReport.setTotalStdDev(roundFloat(2,stdDev)+"");
		jmeterReport.setTotalNoOfSample(NoOfSample+"");
		jmeterReport.setTotalAvg((avg/totalValue)+"");
		jmeterReport.setMin(min+"");
		jmeterReport.setMax(max+"");
		jmeterReport.setTotalErr((Err/totalValue)+"");
		jmeterReport.setTotalAvgBytes(avgBytes+"");
		jmeterReport.setTotalKbPerSec(roundFloat(2,KbPerSec)+"");
		
		return jmeterReport;
	}

	private static double setStdDevToResults(Map<String, PerformanceTestResult> results) {
		Set<String> keySet = results.keySet();
		long xBar = 0;  		//XBar Calculation
		long sumOfTime = 0;
		int totalSamples = 0;
		double sumMean = 0;
		List<Integer> allTimes = new ArrayList<Integer>();
		for (String key : keySet) {
			PerformanceTestResult performanceTestResult = results.get(key);
			// calculation of average time
			double avg = performanceTestResult.getTotalTime() / performanceTestResult.getNoOfSamples();
			sumOfTime = sumOfTime + performanceTestResult.getTotalTime();
			totalSamples = totalSamples + performanceTestResult.getNoOfSamples();
			performanceTestResult.setAvg(avg);

			// calculation of average bytes
			double avgBytes = (double) performanceTestResult.getTotalBytes() / performanceTestResult.getNoOfSamples();
			performanceTestResult.setAvgBytes(Math.round(avgBytes));
			// KB/Sec calculation
			Double calKbPerSec = new Double(performanceTestResult.getThroughtPut());
			calKbPerSec = calKbPerSec * (((double) avgBytes) / 1024)   ;
			performanceTestResult.setKbPerSec(calKbPerSec);

			// Std.Dev calculation
			List<Integer> times = performanceTestResult.getTimes();
			allTimes.addAll(times);
			Double totalMean = new Double(0);
			for (Integer time : times) {
				totalMean += Math.pow(time - avg, 2);
			}
			performanceTestResult.setStdDev((float) Math.sqrt(totalMean / performanceTestResult.getNoOfSamples()));

			performanceTestResult.setErr((performanceTestResult.getErr() / performanceTestResult.getNoOfSamples()) * 100);
		}

		//Total Std.Dev calculation
		xBar = sumOfTime / totalSamples;
		for (Integer time : allTimes) {
			sumMean += Math.pow(time - xBar, 2);
		}
		double stdDev = Math.sqrt(sumMean / totalSamples);
		return stdDev;
	}
	
    private List<TestResult> getLoadTestResult(Document doc) throws TransformerException, PhrescoException, ParserConfigurationException, SAXException, IOException {
    	 List<TestResult> testResults = new ArrayList<TestResult>(2);
	     try {
	         NodeList nodeList = org.apache.xpath.XPathAPI.selectNodeList(doc, XPATH_TEST_RESULT);
	         TestResult testResult = null;
	         for (int i = 0; i < nodeList.getLength(); i++) {
	             testResult =  new TestResult();
	             Node node = nodeList.item(i);
	             //              NodeList childNodes = node.getChildNodes();
	             NamedNodeMap nameNodeMap = node.getAttributes();
	
	             for (int k = 0; k < nameNodeMap.getLength(); k++) {
	                 Node attribute = nameNodeMap.item(k);
	                 String attributeName = attribute.getNodeName();
	                 String attributeValue = attribute.getNodeValue();
	
	                 if (ATTR_JM_TIME.equals(attributeName)) {
	                     testResult.setTime(Integer.parseInt(attributeValue));
	                 } else if (ATTR_JM_LATENCY_TIME.equals(attributeName)) {
	                     testResult.setLatencyTime(Integer.parseInt(attributeValue));
	                 } else if (ATTR_JM_TIMESTAMP.equals(attributeName)) {
	                     Date date = new Date(Long.parseLong(attributeValue));
	                     DateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
	                     String strDate = format.format(date);
	                     testResult.setTimeStamp(strDate);
	                 } else if (ATTR_JM_SUCCESS_FLAG.equals(attributeName)) {
	                     testResult.setSuccess(Boolean.parseBoolean(attributeValue));
	                 } else if (ATTR_JM_LABEL.equals(attributeName)) {
	                     testResult.setLabel(attributeValue);
	                 } else if (ATTR_JM_THREAD_NAME.equals(attributeName)) {
	                     testResult.setThreadName(attributeValue);
	                 }
	             }
	             testResults.add(testResult);
	         }
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
	     return testResults;
    }
    
	private Document getDocumentOfFile(File reportFile) {
		Document doc = null;
		InputStream fis = null;
		DocumentBuilder builder = null;
		try {
			fis = new FileInputStream(reportFile); // here should be new
													// File(path + "/" +
													// selectedTestResultFileName);
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(false);
			builder = domFactory.newDocumentBuilder();
			doc = builder.parse(fis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return doc;
	}

    private Document getDocumentOfFile(String path, String fileName) {
    	Document doc = null;
        InputStream fis = null;
        DocumentBuilder builder = null;
        try {
            fis = new FileInputStream(new File(path + "/" + fileName)); // here should be new File(path + "/" + selectedTestResultFileName);
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(false);
            builder = domFactory.newDocumentBuilder();
            doc = builder.parse(fis);
        } catch (Exception e) {
        	e.printStackTrace();
		} finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        }
		return doc;
    }
    
   
    private List<String> getTestResultFiles(String path) {
        File testDir = new File(path);
        List<String> testResultFileNames = new ArrayList<String>();
        if(testDir.isDirectory()){
            FilenameFilter filter = new PhrescoFileFilter("", XML);
            File[] listFiles = testDir.listFiles(filter);
            for (File file : listFiles) {
                if (file.isFile()) {
                    testResultFileNames.add(file.getName());
                }
            }
        }
        return testResultFileNames;
    }

	private List<File> getTestResultFilesAsList(String path) {
		File testDir = new File(path);
		List<File> testResultFileNames = new ArrayList<File>();
		if (testDir.isDirectory()) {
			FilenameFilter filter = new PhrescoFileFilter("", XML);
			File[] listFiles = testDir.listFiles(filter);
			for (File file : listFiles) {
				if (file.isFile()) {
					testResultFileNames.add(file);
				}
			}
		}
		return testResultFileNames;
	}

	public static Map<String, String> getDeviceNames(Document document)  throws Exception {
		NodeList nodeList = org.apache.xpath.XPathAPI.selectNodeList(document, "/*/deviceInfo");
		Map<String, String> deviceList = new LinkedHashMap<String, String>(100);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NamedNodeMap nameNodeMap = node.getAttributes();
			String deviceId = "";
			String deviceName = "";
			Node idAttr = nameNodeMap.getNamedItem(ATTR_ID);
			deviceId = idAttr.getNodeValue();
			Node nameAttr = nameNodeMap.getNamedItem(ATTR_NAME);
			deviceName = nameAttr.getNodeValue();
			deviceList.put(deviceId, deviceName);
		}
		return deviceList;
	}
	
    public static float roundFloat(int decimal, double value) {
		BigDecimal roundThroughPut = new BigDecimal(value);
		return roundThroughPut.setScale(decimal, BigDecimal.ROUND_HALF_EVEN).floatValue();
	}
    
	public float getNoOfTstSuiteTests() {
		return noOfTstSuiteTests;
	}

	public void setNoOfTstSuiteTests(float noOfTstSuiteTests) {
		this.noOfTstSuiteTests = noOfTstSuiteTests;
	}

	public float getNoOfTstSuiteFailures() {
		return noOfTstSuiteFailures;
	}

	public void setNoOfTstSuiteFailures(float noOfTstSuiteFailures) {
		this.noOfTstSuiteFailures = noOfTstSuiteFailures;
	}

	public float getNoOfTstSuiteErrors() {
		return noOfTstSuiteErrors;
	}

	public void setNoOfTstSuiteErrors(float noOfTstSuiteErrors) {
		this.noOfTstSuiteErrors = noOfTstSuiteErrors;
	}
	
	private ApplicationInfo getApplicationInfo(File projectInfoFile) throws MojoExecutionException {
		try {
	        Gson gson = new Gson();
	        BufferedReader reader = null;
	        reader = new BufferedReader(new FileReader(projectInfoFile));
	        ProjectInfo projectInfo = gson.fromJson(reader, ProjectInfo.class);
	        this.projName = projectInfo.getName();
	        List<ApplicationInfo> appInfos = projectInfo.getAppInfos();
	        for (ApplicationInfo appInfo : appInfos) {
//	        	log.info("appInfo dir name ... " + appInfo.getAppDirName());
				return appInfo;
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return null;
	}
	
	public void generate(Configuration config, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		try {
			this.log = log;
	        baseDir = mavenProjectInfo.getBaseDir();
	        mavenProject = mavenProjectInfo.getProject();
	        
	        // get projects plugin info file path
	        File projectInfo = new File(baseDir, DOT_PHRESCO_FOLDER + File.separator + PROJECT_INFO_FILE);
			if (!projectInfo.exists()) {
				throw new MojoExecutionException("Project info file is not found in jenkins workspace dir " + baseDir.getCanonicalPath());
			}
			ApplicationInfo appInfo = getApplicationInfo(projectInfo);
			if (appInfo == null) {
				throw new MojoExecutionException("AppInfo value is Null ");
			}
	        
	        Map<String, String> configs = MojoUtil.getAllValues(config);
	        String reportType = configs.get(REPORT_TYPE);
	        String testType = configs.get(TEST_TYPE);
	        String sonarUrl = configs.get("sonarUrl");
	        String appVersion = appInfo.getVersion();
	        
	        logo = configs.get("logo");
	        String themeJson = configs.get("theme");
	        if (StringUtils.isNotEmpty(themeJson)) {
	        	Gson gson = new Gson();
	        	Type mapType = new TypeToken<Map<String, String>>() {}.getType();
		        theme = (Map<String, String>)gson.fromJson(themeJson, mapType);
	        }
	        
	        this.testType = testType;
	        this.reportType = reportType;
	        this.appDir = appInfo.getAppDirName();
	        this.projectCode = appInfo.getName();
//	        this.projName = appInfo.getName();
	        this.techName = appInfo.getTechInfo().getId();
	        this.version = appVersion;
	        this.sonarUrl = sonarUrl;
	        
	        if (StringUtils.isEmpty(reportType)) {
	        	throw new PhrescoException("Report type is empty ");
	        }
	        
	        if (StringUtils.isEmpty(testType)) {
	        	throw new PhrescoException("Test Type is empty ");
	        }
	        
	    	String clangReportPath = mavenProject.getProperties().getProperty(Constants.POM_PROP_KEY_VALIDATE_REPORT);
	    	if (StringUtils.isNotEmpty(clangReportPath)) {
	    		isClangReport = true;
	    	}
	    	
	        if ("All".equalsIgnoreCase(testType)) {
	        	log.info("all report generation started ... "); // all report
	        	cumalitiveTestReport();
	        } else {
	        	log.info("indivudal report generation started ... "); // specified type report
	        	generatePdfReport();
	        }
	        log.info("Report generation completed ... ");
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	public List<String> getSonarProfiles(String pomPath) throws PhrescoException {
		List<String> sonarTechReports = new ArrayList<String>(6);
		try {
			PomProcessor pomProcessor = new PomProcessor(new File(pomPath));
			Model model = pomProcessor.getModel();
			Profiles modelProfiles = model.getProfiles();
			if (modelProfiles == null) {
				return sonarTechReports;
			}
			List<Profile> profiles = modelProfiles.getProfile();
			if (profiles == null) {
				return sonarTechReports;
			}
			for (Profile profile : profiles) {
				if (profile.getProperties() != null) {
					List<Element> any = profile.getProperties().getAny();
					int size = any.size();
					
					for (int i = 0; i < size; ++i) {
						boolean tagExist = 	any.get(i).getTagName().equals(SONAR_LANGUAGE);
						if (tagExist){
							sonarTechReports.add(profile.getId());
						}
					}
				}
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return sonarTechReports;
	}
	
	private void applyTheme(JasperPrint jasperPrint) throws Exception {
		String titleColor = "#00CC66"; // TitleRectLogo
		String titleLabelColor = "#333333"; // TitleRectDetail
		
		String headingForeColor = "#D0B48E"; // heading yellow color
		String headingBackColor = "#00CC66";

//		String headingLabelBackColor = "#FFFFFF"; //HeadingRo
//		String headingLabelForeColor = "#000000"; //HeadingR
//		String headingTextBackColor = "#FFFFFF"; //HeadingRo
//		String headingTextForeColor = "#000000"; //HeadingRoo

		String headingRowBackColor = "#DDFCDD"; //HeadingRow - light color
		String headingRowLabelBackColor = "#FFFFFF"; //Done
		String headingRowLabelForeColor = "#333333"; //Done - font color
		String headingRowTextBackColor = "#FFFFFF"; //Done
		String headingRowTextForeColor = "#333333"; //Done
		
		String copyRightBackColor = "#333333";
		String copyRightForeColor = "#FFFFFF";
		
		String copyRightPageNumberForeColor = "#FFFFFF";
		String copyRightPageNumberBackColor = "#00CC66";
		
		if (MapUtils.isNotEmpty(theme)) {
			titleColor = theme.get("PageHeaderColor");
			titleLabelColor = theme.get("PageHeaderColor");
			
			headingForeColor = theme.get("brandingColor"); // heading yellow color
			headingBackColor = theme.get("PageHeaderColor");

			headingRowBackColor = theme.get("PageHeaderColor"); //HeadingRow - light color
			headingRowLabelBackColor = theme.get("PageHeaderColor");
			headingRowTextBackColor = theme.get("PageHeaderColor");
			headingRowLabelForeColor = theme.get("brandingColor");
			headingRowTextForeColor = theme.get("brandingColor");
			
			copyRightBackColor = theme.get("PageHeaderColor");
			copyRightForeColor = theme.get("brandingColor");
			
			copyRightPageNumberForeColor = theme.get("brandingColor");
			copyRightPageNumberBackColor = theme.get("PageHeaderColor");
		}
		
		java.awt.Color userTitleColor = java.awt.Color.decode(titleColor);
		java.awt.Color userTitleLabelColor = java.awt.Color.decode(titleLabelColor);
		
		java.awt.Color userHeadingForeColor = java.awt.Color.decode(headingForeColor); // heading yellow color
		java.awt.Color userHeadingBackColor = java.awt.Color.decode(headingBackColor);
		
		java.awt.Color userHeadingRowBackColor = java.awt.Color.decode(headingRowBackColor); // HeadingRow - light color
		java.awt.Color userHeadingRowLabelBackColor = java.awt.Color.decode(headingRowLabelBackColor); // HeadingRow - light color
		java.awt.Color userHeadingRowLabelForeColor = java.awt.Color.decode(headingRowLabelForeColor); // HeadingRow - light color
		java.awt.Color userHeadingRowTextBackColor = java.awt.Color.decode(headingRowTextBackColor); // HeadingRow - light color
		java.awt.Color userHeadingRowTextForeColor = java.awt.Color.decode(headingRowTextForeColor); // HeadingRow - light color
		
		java.awt.Color userCopyRightBackColor = java.awt.Color.decode(copyRightBackColor);
		java.awt.Color userCopyRightForeColor = java.awt.Color.decode(copyRightForeColor);
		java.awt.Color userCopyRightPageNumberForeColor = java.awt.Color.decode(copyRightPageNumberForeColor);
		java.awt.Color userCopyRightPageNumberBackColor = java.awt.Color.decode(copyRightPageNumberBackColor);
		
		
		JRStyle[] styleList = jasperPrint.getStyles();
		for (int j = 0; j < styleList.length; j++) {
			if (styleList[j].getName().endsWith("TitleRectLogo")) {
		        styleList[j].setBackcolor(userTitleColor);
		        jasperPrint.addStyle(styleList[j], true);
			} else if (styleList[j].getName().endsWith("TitleRectDetail")) {
				styleList[j].setForecolor(userTitleLabelColor);
				JRPen linePen = styleList[j].getLinePen();
				linePen.setLineColor(userTitleColor);
				linePen.setLineWidth(1);
		        jasperPrint.addStyle(styleList[j], true);
		    } else if (styleList[j].getName().endsWith("TitleLabel")) {
		        styleList[j].setForecolor(userTitleLabelColor);
		        jasperPrint.addStyle(styleList[j], true);
		    } else if (styleList[j].getName().endsWith("TitleLabelValue")) {
		        styleList[j].setForecolor(userTitleLabelColor);
		        jasperPrint.addStyle(styleList[j], true);
		    }  else if (styleList[j].getName().endsWith("Heading")) {
		    	styleList[j].setForecolor(userHeadingForeColor);
		    	styleList[j].setBackcolor(userHeadingBackColor);
	        	jasperPrint.addStyle(styleList[j], true);
		    }  else if (styleList[j].getName().endsWith("CopyRight")) {
		    	styleList[j].setForecolor(userCopyRightForeColor);
		    	styleList[j].setBackcolor(userCopyRightBackColor);
	        	jasperPrint.addStyle(styleList[j], true);
		    }  else if (styleList[j].getName().endsWith("CopyRightPageNo")) {
		    	styleList[j].setForecolor(userCopyRightPageNumberForeColor);
		    	styleList[j].setBackcolor(userCopyRightPageNumberBackColor);
	        	jasperPrint.addStyle(styleList[j], true);
		    }  else if (styleList[j].getName().endsWith("HeadingRow")) {
		    	styleList[j].setBackcolor(userHeadingRowBackColor);
	        	jasperPrint.addStyle(styleList[j], true);
		    }  else if (styleList[j].getName().endsWith("HeadingRowLabel")) {
		    	styleList[j].setForecolor(userHeadingRowLabelForeColor);
		    	styleList[j].setBackcolor(userHeadingRowLabelBackColor);
	        	jasperPrint.addStyle(styleList[j], true);
		    }  else if (styleList[j].getName().endsWith("HeadingRowLabelValue")) {
		    	styleList[j].setForecolor(userHeadingRowTextForeColor);
		    	styleList[j].setBackcolor(userHeadingRowTextBackColor);
	        	jasperPrint.addStyle(styleList[j], true);
		   	// table related styles
		    }  else if (styleList[j].getName().endsWith("table_CH")) {
		    	styleList[j].setBackcolor(userHeadingRowBackColor);
	        	jasperPrint.addStyle(styleList[j], true);
		    }  else if (styleList[j].getName().endsWith("table_CH_Label")) {
		    	styleList[j].setForecolor(userHeadingRowLabelForeColor);
		    	styleList[j].setBackcolor(userHeadingRowLabelBackColor);
	        	jasperPrint.addStyle(styleList[j], true);
		    }
			//
//		    else if (styleList[j].getName().endsWith("table_TD_Label")) {
//		    	styleList[j].setForecolor(userHeadingRowLabelForeColor);
//		    	styleList[j].setBackcolor(userHeadingRowLabelBackColor);
//	        	jasperPrint.addStyle(styleList[j], true);
//		    }
		}
	}
}

class PhrescoFileFilter implements FilenameFilter {
	private String name;
	private String extension;

	public PhrescoFileFilter(String name, String extension) {
		this.name = name;
		this.extension = extension;
	}

	public boolean accept(File directory, String filename) {
		boolean fileOK = true;

		if (name != null) {
			fileOK &= filename.startsWith(name);
		}

		if (extension != null) {
			fileOK &= filename.endsWith('.' + extension);
		}
		return fileOK;
	}
}

class FileExtensionFileFilter implements FilenameFilter {
    private String filter_;
    public FileExtensionFileFilter(String filter) {
        filter_ = filter;
    }

    public boolean accept(File dir, String name) {
        return name.endsWith(filter_);
    }
    
}