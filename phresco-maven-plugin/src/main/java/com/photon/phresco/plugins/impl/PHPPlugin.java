package com.photon.phresco.plugins.impl;

import java.io.File;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.plugins.util.MojoUtil;

public class PHPPlugin extends PhrescoAbstractPlugin {
    
    public PHPPlugin(Log log) {
        super(log);
    }
    
    public void pack(Configuration configuration) throws PhrescoException {
        Map<String, String> paramValues = MojoUtil.getAllValues(configuration);
        log.info(paramValues.toString());
    }

    public void deploy() throws PhrescoException {
        // TODO Auto-generated method stub
        
    }

}
