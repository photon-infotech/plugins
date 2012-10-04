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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.plist.XMLPropertyListConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugins.xcode.utils.SdkVerifier;
import com.photon.phresco.plugins.xcode.utils.XMLConstants;

/**
 * APP instrumentation
 * @goal instruments
 */
public class Instrumentation extends AbstractXcodeMojo implements PluginConstants {
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
	 * @parameter expression="${project.basedir}" required="true"
	 * @readonly
	 */
	protected File baseDir;
	
	/**
	 * @parameter expression="${buildNumber}" required="true"
	 */
	protected String buildNumber;
	
	/**
	 * @parameter 
	 */
	private String outputFolder;
	
	/**
	 * @parameter 
	 */
	private static XMLPropertyListConfiguration config;
	private String appPath;
	private File buildInfoFile;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
			getLog().info("Instrumentation command" + command);
			try {	
				if(SdkVerifier.isAvailable("iphonesimulator5.1")) {
					template = "/Applications/Xcode.app/Contents" + template;
			    }
			} catch (IOException e2) {
			    throw new MojoExecutionException("Template not found");
			} catch (InterruptedException e2) {
				throw new MojoExecutionException("Template interrupted!");
			}
				
			if (StringUtils.isEmpty(buildNumber)) {
				throw new MojoExecutionException("Selected build is not available!");
			}
			getLog().info("Build id is " + buildNumber);
			getLog().info("Project Code " + baseDir.getName());
			
			BuildInfo buildInfo = getBuildInfo(Integer.parseInt(buildNumber));
			appPath = buildInfo.getBuildName();
			getLog().info("Application.path = " + appPath);
			
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
			} catch (InterruptedException e) {
				getLog().error(e);
			}

			preparePlistResult();
			generateXMLReport(project.getBasedir().getAbsolutePath()+File.separator+plistResult);
	}

			

	private void preparePlistResult() throws MojoExecutionException {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document xmldoc = dbf.newDocumentBuilder().parse(
					new File(project.getBasedir().getAbsolutePath()+File.separator+plistResult));

			StreamResult out = new StreamResult(project.getBasedir().getAbsolutePath()+File.separator+plistResult);
			
			DOMSource domSource = new DOMSource(xmldoc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "file://localhost/System/Library/DTDs/PropertyList.dtd");
			transformer.transform(domSource , out);
		} catch (Exception e) { 
			getLog().error(e);
		}
	}

	private void generateXMLReport(String location) {
		getLog().info("xml generation started");
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
					
					List<ConfigurationNode> children = (List<ConfigurationNode>) config.getRoot().getChildren();
					String errorTextNodes = XMLConstants.DICT_START;
					for (ConfigurationNode child : children) {
						errorTextNodes = errorTextNodes + XMLConstants.KEY_START + child.getName() + XMLConstants.KEY_END;
						errorTextNodes = errorTextNodes + XMLConstants.STRING_START + child.getValue() + XMLConstants.STRING_END;
					}
					errorTextNodes = errorTextNodes + XMLConstants.DICT_END;
					
					getLog().info("error node " + errorTextNodes);
					
					long differ = getTimeDiff(startTime, endTime);
					startTime = endTime;
					child1.setAttribute(XMLConstants.TIME, differ+"");
					//adding error element
					Element errorElem = doc.createElement(XMLConstants.FAILURE);
					errorElem.setAttribute(XMLConstants.TYPE, "Exception");
					errorElem.setTextContent(errorTextNodes);
					child1.appendChild(errorElem);
					testSuite.appendChild(child1);
				}

			}
			
			testSuite.setAttribute(XMLConstants.TESTS, String.valueOf(total));
			testSuite.setAttribute(XMLConstants.SUCCESS, String.valueOf(pass));
			testSuite.setAttribute(XMLConstants.FAILURES, String.valueOf(fail));

			getLog().info("Total " + total);
			getLog().info("Success " + pass);
			getLog().info("Failure " + fail);

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
			getLog().error(e);
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
	
	private BuildInfo getBuildInfo(int buildNumber) throws MojoExecutionException {
		ProjectAdministrator administrator;
		try {
			administrator = PhrescoFrameworkFactory.getProjectAdministrator();
		} catch (PhrescoException e) {
			throw new MojoExecutionException("Project administrator object creation error!");
		}
		buildInfoFile = new File(baseDir.getPath() + PluginConstants.BUILD_DIRECTORY + BUILD_INFO_FILE);
		if (!buildInfoFile.exists()) {
			throw new MojoExecutionException("Build info is not available!");
		}
		try {
			List<BuildInfo> buildInfos = administrator.readBuildInfo(buildInfoFile);
			
			 if (CollectionUtils.isEmpty(buildInfos)) {
				 throw new MojoExecutionException("Build info is empty!");
			 }

			 for (BuildInfo buildInfo : buildInfos) {
				 if (buildInfo.getBuildNo() == buildNumber) {
					 return buildInfo;
				 }
			 }

			 throw new MojoExecutionException("Build info is empty!");
		} catch (Exception e) {
			throw new MojoExecutionException(e.getLocalizedMessage());
		}
	}
}


