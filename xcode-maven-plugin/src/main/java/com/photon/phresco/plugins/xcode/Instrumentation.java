/*******************************************************************************
 * Copyright (c)  2012 Photon infotech.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Photon Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.photon.in/legal/ppl-v10.html
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 *  Contributors:
 *  	  Photon infotech - initial API and implementation
 ******************************************************************************/
package com.photon.phresco.plugins.xcode;


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.plist.XMLPropertyListConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import org.w3c.dom.*;

import com.photon.phresco.plugins.xcode.utils.XcodeUtil;
import com.photon.phresco.plugins.xcode.utils.XMLConstants;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;




/**
 * APP instrumentation
 * @goal instruments
 */
public class Instrumentation extends AbstractXcodeMojo {
	/**
	 * @parameter experssion="${command}" default-value="instruments"
	 */
	private String command;
	
	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	
	/**
	 * @parameter expression="${template}" default-value="/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS.platform/Developer/Library/Instruments/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate"
	 */
	private String template;
	
	/**
	 * This should be either device or template
	 * @parameter expression="${deviceid}"
	 */
	private String deviceid;
	
	/**
	 * @parameter expression="${pid}"
	 */
	private String pid;
	
	/**
	 * @parameter expression="${verbose}" default-value="false"
	 */
	private boolean verbose;
	
	/**
	 * @parameter expression="${application.path}"
	 */
	private String appPath;
	
	/**
	 * @parameter expression="${script.name}" default-value="test/functional/src/AllTests.js"
	 */
	private String script;
	
	/**
	 * @parameter expression="${script.name}" default-value="Run 1/Automation Results.plist"
	 */
	private String plistResult;
	
	/**
	 * @parameter expression="${script.name}" default-value="do_not_checkin/functional.xml"
	 */
	public String xmlResult;
	
	/**
	 * @parameter 
	 */
	private String outputFolder;
	
	/**
	 * @parameter 
	 */
	private static XMLPropertyListConfiguration config;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
			getLog().info("Instrumentation command" + command);
			
		    try {
				outputFolder = project.getBasedir().getAbsolutePath();
				File f = new File(outputFolder);
				File files[] = f.listFiles();
				for(File file : files) {
					if(file.getName().startsWith("Run 1" ) || file.getName().endsWith(".trace")) {
						FileUtils.deleteDirectory(file);		
					}
				}
			} catch (IOException e) {
				getLog().error(e);
			}
			
			Runnable runnable = new Runnable() {
				public void run() {
					ProcessBuilder pb = new ProcessBuilder(command);
					//device takes the highest priority
					if(StringUtils.isNotBlank(deviceid)) {
						pb.command().add("-w");
						pb.command().add(deviceid);
					}
					pb.command().add("-t");
					pb.command().add(template);

					if(StringUtils.isNotBlank(appPath)) {
						pb.command().add(appPath);
					} else {
						getLog().error("Application should not be empty");
					}
					if(StringUtils.isNotBlank(script)) {
						pb.command().add("-e");
						pb.command().add("UIASCRIPT");
						String scriptPath = project.getBasedir().getAbsolutePath()+File.separator+script;
						pb.command().add(scriptPath);
					} else {
						getLog().error("script is empty");
					}
					
					pb.command().add("-e");
					pb.command().add("UIARESULTSPATH");					
					pb.command().add(outputFolder);
					
					// Include errors in output
					pb.redirectErrorStream(true);

					getLog().info("List of commands"+pb.command());
					Process child;
					try {
						child = pb.start();
						// Consume subprocess output and write to stdout for debugging
						InputStream is = new BufferedInputStream(child.getInputStream());
						int singleByte = 0;
						while ((singleByte = is.read()) != -1) {
							System.out.write(singleByte);
						}
					} catch (IOException e) {
						getLog().error(e);
					}
										
				}
		
			};
			
		   
		   
			Thread t = new Thread(runnable, "iPhoneSimulator");
			t.start();
			getLog().info("Thread started");
			try {
				//Thread.sleep(5000);
				t.join();
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			preparePlistResult();
			generateXMLReport(project.getBasedir().getAbsolutePath()+File.separator+plistResult);
	}

			

	private void preparePlistResult() throws MojoExecutionException {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document xmldoc = dbf.newDocumentBuilder().parse(
					new File(project.getBasedir().getAbsolutePath()+File.separator+plistResult));
			Element root = xmldoc.getDocumentElement();

			Node pi = xmldoc.createProcessingInstruction
					("DOCTYPE plist SYSTEM \\", "file://localhost/System/Library/DTDs/PropertyList.dtd>");
			xmldoc.insertBefore(pi, root);

			StreamResult out = new StreamResult(project.getBasedir().getAbsolutePath()+File.separator+plistResult);
			
			DOMSource domSource = new DOMSource(xmldoc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource , out);
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}

	private void generateXMLReport(String location) {

		try {
			String startTime = "";
			int total,pass,fail;
			total=pass=fail=0;
			config = new XMLPropertyListConfiguration(location);
			ArrayList list =  (ArrayList) config.getRoot().getChild(0).getValue();
			String key;


			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();


			Element root = doc.createElement(XMLConstants.TESTSUITES_NAME);
			doc.appendChild(root);
			Element testSuite = doc.createElement(XMLConstants.TESTSUITE_NAME);
			testSuite.setAttribute(XMLConstants.NAME, "FunctionalTestSuite");
			root.appendChild(testSuite);


			for (Object object : list) {

				XMLPropertyListConfiguration config = (XMLPropertyListConfiguration) object;

				startTime = config.getRoot().getChild(2).getValue().toString();

				break;
			}

			for (Object object : list) {

				XMLPropertyListConfiguration config = (XMLPropertyListConfiguration) object;

				ConfigurationNode con = config.getRoot().getChild(0);
				key = con.getName();

				if(key.equals(XMLConstants.LOGTYPE) && con.getValue().equals(XMLConstants.PASS)){
					pass++;total++;
					Element child1 = doc.createElement(XMLConstants.TESTCASE_NAME);
					child1.setAttribute(XMLConstants.NAME,(String) config.getRoot().getChild(1).getValue());
					child1.setAttribute(XMLConstants.RESULT,(String) con.getValue());

					String endTime = config.getRoot().getChild(2).getValue().toString();
					
					long differ = getTimeDiff(startTime, endTime);
					startTime = endTime;
					child1.setAttribute(XMLConstants.TIME, differ+"");
					testSuite.appendChild(child1);
				}
				else if(key.equals(XMLConstants.LOGTYPE) && con.getValue().equals(XMLConstants.ERROR)){
					fail++;total++;
					Element child1 = doc.createElement(XMLConstants.TESTCASE_NAME);
					child1.setAttribute(XMLConstants.NAME,(String) config.getRoot().getChild(1).getValue());
					child1.setAttribute(XMLConstants.RESULT,(String) con.getValue());
					
					String endTime = config.getRoot().getChild(2).getValue().toString();
					
					long differ = getTimeDiff(startTime, endTime);
					startTime = endTime;
					child1.setAttribute(XMLConstants.TIME, differ+"");
					testSuite.appendChild(child1);
				}

			}
			
			testSuite.setAttribute(XMLConstants.TESTS, String.valueOf(total));
			testSuite.setAttribute(XMLConstants.SUCCESS, String.valueOf(pass));
			testSuite.setAttribute(XMLConstants.FAILURES, String.valueOf(fail));

			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			
			File file = new File(project.getBasedir().getAbsolutePath()+File.separator+xmlResult);
			Writer bw = new BufferedWriter(new FileWriter(file));
			StreamResult result = new StreamResult(bw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);

		}
		catch (Exception e) {
			getLog().error("Interrupted while generating XML file");
		}
	}

	private long getTimeDiff(String dateStart, String dateStop) {
		//TODO try to get the system time format.
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");  
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);
		} catch (ParseException e) {
			e.printStackTrace();
		}    

		long diff = d2.getTime() - d1.getTime();
		return diff;
	}
}


