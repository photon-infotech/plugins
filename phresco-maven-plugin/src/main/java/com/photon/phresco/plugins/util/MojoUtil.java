package com.photon.phresco.plugins.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;

public class MojoUtil {
    
    public static final Map<String, String> getAllValues(Configuration config) {
        List<Parameter> parameters = config.getParameters().getParameter(); 
        Map<String, String> configValues = new HashMap<String, String>(parameters.size() * 2);
        for (Parameter parameter : parameters) {
            String value = parameter.getValue();
            if (StringUtils.isNotEmpty(value)) {
                String key = parameter.getKey();
                configValues.put(key, value); 
            }
        }
        
        return configValues;
    }

}
