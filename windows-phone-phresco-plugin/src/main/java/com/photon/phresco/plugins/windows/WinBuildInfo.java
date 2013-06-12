package com.photon.phresco.plugins.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.text.html.ListView;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.plugin.commons.PluginConstants;
import com.photon.phresco.plugin.commons.PluginUtils;

public class WinBuildInfo implements PluginConstants {


	public void generateBuildInfo(boolean isBuildSuccess, String platform, String buildNumber, int nextBuildNo, String environmentName, String buildName, Date currentDate, File buildInfoFile, String zipName)
			throws MojoExecutionException
			{
			List<BuildInfo> buildInfoList = new ArrayList<BuildInfo>();
		    List<BuildInfo> buildInfos = null;
		    int buildNo = 0;
		    try {
		      Gson gson = new Gson();
		      if (StringUtils.isNotEmpty(buildNumber))
		        buildNo = Integer.parseInt(buildNumber);

		      PluginUtils pu = new PluginUtils();
		      BuildInfo buildInfo = new BuildInfo();
		      List<String> envList = pu.csvToList(environmentName);

		      if ((buildInfoFile.exists()) && (buildInfoFile.length() > 0L)) {
		        Type type = new TypeToken<List<BuildInfo>>() {}  .getType();
		        FileReader reader = new FileReader(buildInfoFile);
		        List<BuildInfo> fromJson = (List<BuildInfo>)gson.fromJson(reader, type);
				buildInfos = fromJson;
		        if (CollectionUtils.isNotEmpty(buildInfos)) {
		          BuildInfo info = (BuildInfo)Collections.max(buildInfos, sortByNo());
		          int no = info.getBuildNo();
		          if ((buildNo == 0) || (buildNo == no) || (no > buildNo))
		            buildNo = no + 1;
		        }

		      }

		      if (buildNo > 0)
		        buildInfo.setBuildNo(buildNo);
		      else
		        buildInfo.setBuildNo(nextBuildNo);

		      platform = platform.replaceAll("\\s+", "");
		      if (StringUtils.isNotEmpty(buildName))
		        buildInfo.setTimeStamp(platform + STR_UNDERSCORE + buildName + STR_UNDERSCORE + getTimeStampForDisplay(currentDate));
		      else {
		        buildInfo.setTimeStamp(platform + STR_UNDERSCORE + getTimeStampForDisplay(currentDate));
		      }

		      if (isBuildSuccess)
		        buildInfo.setBuildStatus("SUCCESS");
		      else
		        buildInfo.setBuildStatus("FAILURE");

		      buildInfo.setBuildName(platform.replace("\\s+", "") + METRO_BUILD_SEPERATOR + zipName);
		      buildInfo.setEnvironments(envList);

		      buildInfoList.add(buildInfo);
		      if (CollectionUtils.isNotEmpty(buildInfos))
		        buildInfoList.addAll(buildInfos);

		      FileWriter writer = new FileWriter(buildInfoFile);
		      gson.toJson(buildInfoList, writer);
		      writer.close();
		    } catch (NumberFormatException e) {
		      e.printStackTrace();
		    } catch (JsonIOException e) {
		      e.printStackTrace();
		    } catch (JsonSyntaxException e) {
		      e.printStackTrace();
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
	
	
	public String getTimeStampForDisplay(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_STAMP_FOR_DISPLAY);
		return formatter.format(currentDate.getTime());
	}

	public String getTimeStampForBuildName(Date currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_STAMP_FOR_BUILD_NAME);
		return formatter.format(currentDate.getTime());
	}

	
	public static Comparator<BuildInfo> sortByNo()
	  {
	    return new Comparator() {
	      public int compare(Object firstObject, Object secondObject) {
	        BuildInfo info1 = (BuildInfo)firstObject;
	        BuildInfo info2 = (BuildInfo)secondObject;
	        return (info1.getBuildNo() - info2.getBuildNo());
	      }
	    };
	  }


}
