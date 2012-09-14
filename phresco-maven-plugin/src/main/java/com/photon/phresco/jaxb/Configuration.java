package com.photon.phresco.jaxb;

import java.util.List;

public class Configuration {

    private Configuration.Parameters parameters;
    
    public Configuration.Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Configuration.Parameters parameters) {
        this.parameters = parameters;
    }
    
    public static class Parameters {
     
        private List<Parameter> parameter;
        
        public List<Parameter> getParameter() {
            return parameter;
        }
    }
}
