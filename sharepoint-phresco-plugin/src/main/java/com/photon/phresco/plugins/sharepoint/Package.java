package com.photon.phresco.plugins.sharepoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.commons.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoUtil;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.Utility;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;

public class Package implements PluginConstants {

	private File baseDir;
	private String environmentName;
	private String buildName;
	private String buildNumber;
	private int buildNo;

	private File wspDir;
	private File buildDir;
	private File buildInfoFile;
	private File tempDir;
	private List<BuildInfo> buildInfoList;
	private int nextBuildNo;
	private String zipName;
	private Date currentDate;
	private String sourceDirectory = "/source";
	private Log log;
	
	public void pack(Configuration configuration, MavenProjectInfo mavenProjectInfo, Log log) throws PhrescoException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
        Map<String, String> configs = MojoUtil.getAllValues(configuration);
        environmentName = configs.get("environmentName");
        
        try {
			init();
			executeExe();
			boolean buildStatus = build();
			writeBuildInfo(buildStatus);
			cleanUp();
		} catch (MojoExecutionException e) {
			throw new PhrescoException();
		}
		
		
	}
	
	private void init() throws MojoExecutionException {
		try {
			unPackCabLib();
			replaceValue();
			buildInfoList = new ArrayList<BuildInfo>(); // initialization
			buildDir = new File(baseDir.getPath() + BUILD_DIRECTORY);
			if (!buildDir.exists()) {
				buildDir.mkdirs();
				log.info("Build directory created..." + buildDir.getPath());
			}
			buildInfoFile = new File(buildDir.getPath() + BUILD_INFO_FILE);
			nextBuildNo = generateNextBuildNo();
			currentDate = Calendar.getInstance().getTime();
		} catch (Exception e) {
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	private void unPackCabLib() throws MojoExecutionException  {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(MVN_CMD);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_CLEAN);
			sb.append(STR_SPACE);
			sb.append(MVN_PHASE_VALDATE);
			bufferedReader = Utility.executeCommand(sb.toString(), baseDir.getPath());
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line); // do not use log here as this line already contains the log type.
				if (line.startsWith("[ERROR]")) {
					errorParam = true;
				}
			}
			if (errorParam) {
				throw new MojoExecutionException("Download CabLib.dll Failed");
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage());
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}

	private void replaceValue() throws MojoExecutionException {
		SAXBuilder builder = new SAXBuilder();
		try {
			File xmlFile = new File(baseDir.getPath() + sourceDirectory + SHAREPOINT_WSP_CONFIG_FILE);
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			Element appSettings = rootNode.getChild(SHAREPOINT_APPSETTINGS, rootNode.getNamespace());
			if (appSettings != null) {
				List children = appSettings.getChildren(SHAREPOINT_ADD, appSettings.getNamespace());
				for (Object object : children) {
					Element dependent = (Element) object;
					String keyValue = dependent.getAttributeValue(SHAREPOINT_KEY);
					Attribute attribute = dependent.getAttribute(SHAREPOINT_VALUE);
					if (keyValue.equals(SHAREPOINT_SOLUTION_PATH)) {
						attribute.setValue(baseDir.getPath() + sourceDirectory);
					}
					if (keyValue.equals(SHAREPOINT_OUTPUT_PATH)) {
						attribute.setValue(baseDir.getPath() + sourceDirectory);
					}
					if (keyValue.equals(SHAREPOINT_WSPNAME)) {
						attribute.setValue(baseDir.getName() + ".wsp");
					}
				}
				saveFile(xmlFile, doc);
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage());
		} 
	}
	
	public void saveFile(File projectPath, Document doc) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(projectPath);
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, writer);
		} finally {
				Utility.closeStream(writer);
		}
	}

	private void executeExe() throws MojoExecutionException {
		BufferedReader bufferedReader = null;
		boolean errorParam = false;
		try {
			log.info("Executing ...");
			bufferedReader = Utility.executeCommand("WSPBuilder.exe", baseDir.getPath() + sourceDirectory);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line); // do not use log here as this line already contains the log type.
				if (line.startsWith("[ERROR]")) {
					errorParam = true;
				}
			}
			if (errorParam) {
				throw new MojoExecutionException("Overwritten of WSPBuilder.exe.config File Failed");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			Utility.closeStream(bufferedReader);
		}
	}

	private boolean build() throws MojoExecutionException {
		boolean isBuildSuccess = true;
		try {
			log.info("Building the project...");
			wspDir = new File(baseDir + sourceDirectory);
			createPackage();
		} catch (Exception e) {
			isBuildSuccess = false;
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return isBuildSuccess;
	}

	private void createPackage() throws MojoExecutionException {
		try {
			String context = baseDir.getName();
			if (buildName != null) {
				zipName = buildName + ".zip";
			} else {
				if (buildNumber != null) {
					zipName = PROJECT_CODE + buildNumber + STR_UNDERSCORE + getTimeStampForBuildName(currentDate)
							+ ".zip";
				} else {
					zipName = PROJECT_CODE + nextBuildNo + STR_UNDERSCORE + getTimeStampForBuildName(currentDate)
							+ ".zip";
				}
			}
			String zipFilePath = buildDir.getPath() + File.separator + zipName;
			String zipNameWithoutExt = zipName.substring(0, zipName.lastIndexOf('.'));
			copyWspToPackage(zipNameWithoutExt, context);
			ArchiveUtil.createArchive(tempDir.getPath(), zipFilePath, ArchiveType.ZIP);
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage(), e);
		}
	}

	private void copyWspToPackage(String zipNameWithoutExt, String context) throws MojoExecutionException {
		try {
			String[] list = wspDir.list(new WarFileNameFilter());
			if (list.length > 0) {
				File warFile = new File(wspDir.getPath() + File.separator + list[0]);
				tempDir = new File(buildDir.getPath() + File.separator + zipNameWithoutExt);
				tempDir.mkdir();
				File contextWarFile = new File(wspDir.getPath() + File.separator + context + ".wsp");
				warFile.renameTo(contextWarFile);
				FileUtils.copyFileToDirectory(contextWarFile, tempDir);
			} else {
				throw new MojoExecutionException("Compilation Failure...");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void writeBuildInfo(boolean isBuildSuccess) throws MojoExecutionException {
		try {
			if (buildNumber != null) {
				buildNo = Integer.parseInt(buildNumber);
			}

			PluginUtils pu = new PluginUtils();
			BuildInfo buildInfo = new BuildInfo();
			List<String> envList = pu.csvToList(environmentName);
			if (buildNo > 0) {
				buildInfo.setBuildNo(buildNo);
			} else {
				buildInfo.setBuildNo(nextBuildNo);
			}
			buildInfo.setTimeStamp(getTimeStampForDisplay(currentDate));
			if (isBuildSuccess) {
				buildInfo.setBuildStatus(SUCCESS);
			} else {
				buildInfo.setBuildStatus(FAILURE);
			}
			buildInfo.setBuildName(zipName);
			buildInfo.setEnvironments(envList);
			buildInfoList.add(buildInfo);
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(buildInfoFile);
			gson.toJson(buildInfoList, writer);

			writer.close();
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private String getTimeStampForDisplay(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
		String timeStamp = formatter.format(currentDate.getTime());
		return timeStamp;
	}

	private String getTimeStampForBuildName(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
		String timeStamp = formatter.format(currentDate.getTime());
		return timeStamp;
	}

	private int generateNextBuildNo() throws IOException {
		int nextBuildNo = 1;
		if (!buildInfoFile.exists()) {
			return nextBuildNo;
		}

		BufferedReader read = new BufferedReader(new FileReader(buildInfoFile));
		String content = read.readLine();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<BuildInfo>>() {
		}.getType();
		buildInfoList = (List<BuildInfo>) gson.fromJson(content, listType);
		if (buildInfoList == null || buildInfoList.size() == 0) {
			return nextBuildNo;
		}
		int buildArray[] = new int[buildInfoList.size()];
		int count = 0;
		for (BuildInfo buildInfo : buildInfoList) {
			buildArray[count] = buildInfo.getBuildNo();
			count++;
		}
		// sort to the array to find the max build no
		Arrays.sort(buildArray);

		// increment 1 to the max in the build list
		nextBuildNo = buildArray[buildArray.length - 1] + 1;
		return nextBuildNo;
	}

	private void cleanUp() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

	class WarFileNameFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.endsWith(".wsp");
		}
	}
}
