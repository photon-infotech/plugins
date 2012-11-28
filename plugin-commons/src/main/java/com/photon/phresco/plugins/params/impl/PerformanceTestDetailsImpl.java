package com.photon.phresco.plugins.params.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.photon.phresco.api.DynamicPageParameter;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class PerformanceTestDetailsImpl implements DynamicPageParameter {

    @Override
    public List<Object> getObjects(Map<String, Object> paramsMap) throws PhrescoException {
        try {
            ApplicationInfo applicationInfo = (ApplicationInfo) paramsMap.get(KEY_APP_INFO);
            String testAgainst = (String) paramsMap.get(KEY_TEST_AGAINST);
            String testResultName = (String) paramsMap.get(KEY_TEST_RESULT_NAME);
            testResultJsonFile(applicationInfo.getAppDirName());
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return null;
    }
    
    private StringBuilder testResultJsonFile(String appDirName) throws PhrescoPomException {
        StringBuilder builder = new StringBuilder(Utility.getProjectHome());
        builder.append(appDirName);
        builder.append(File.separator);
        
        PomProcessor processor = new PomProcessor(getPOMFile(appDirName));
        
        
        return builder;
    }
    
    private File getPOMFile(String appDirName) {
        StringBuilder builder = new StringBuilder(Utility.getProjectHome())
        .append(appDirName)
        .append(File.separatorChar)
        .append("pom.xml");
        return new File(builder.toString());
    }

}