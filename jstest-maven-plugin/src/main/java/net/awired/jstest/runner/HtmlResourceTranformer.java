/**
 * JsTest Maven Plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.awired.jstest.runner;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.awired.jstest.resource.ResourceResolver;

public class HtmlResourceTranformer {

    public String buildTagsFromResources(Map<String, File> resources) {
        StringBuilder res = new StringBuilder();
        for (String key : resources.keySet()) {
        	if (!key.startsWith("/src/yui") && !key.endsWith("/UseYUI.js")) {
        		appendTag(res, key);
        	}
        	if (key.endsWith("/yui-min.js")){
        		appendTag(res, key);
        	}
        }
        
        return res.toString();
    }
    
    public String buildTagsFromResources(Map<String, File> resources, List<String> testerResources) {
    	
        StringBuilder res = new StringBuilder();
        for (String key : resources.keySet()) {
        	String file = key.replace(ResourceResolver.LIB_RESOURCE_PREFIX, "");
        	if (!testerResources.contains(file)) {
	        	if (!key.startsWith("/src/yui") && !key.endsWith("/UseYUI.js")) {
	        		appendTag(res, key);
	        	}
	        	if (key.endsWith("/yui-min.js")){
	        		appendTag(res, key);
	        	}
        	}
        }
        
        return res.toString();
    }

    public void appendTag(StringBuilder builder, String path) {
        if (path.endsWith(".js")) {
            builder.append("<script type=\"text/javascript\" src=\"");
            builder.append(path);
            builder.append("\"></script>\n");
        } else if (path.endsWith(".css")) {
            builder.append("<link href=\"");
            builder.append(path);
            builder.append("\" rel=\"stylesheet\" type=\"text/css\">\n");
        }
    }
}
