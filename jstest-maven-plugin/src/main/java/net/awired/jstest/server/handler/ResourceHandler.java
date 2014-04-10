/**
 * JsTest Maven Plugin
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
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
package net.awired.jstest.server.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.awired.jstest.resource.ResourceResolver;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jetty.server.Request;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class ResourceHandler {

    private ResourceResolver resourceResolver;
    private final Log log;

    public ResourceHandler(Log log, ResourceResolver resourceResolver) {
        this.log = log;
        this.resourceResolver = resourceResolver;
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        File file = resourceResolver.getResource(target);
        if (file != null) {
            log.debug("Serve resource : " + target + " to target file :" + file);
            String contentType = "text/html";
            if (target.endsWith(".js")) {
                contentType = "application/javascript";
            } else if (target.endsWith(".css")) {
                contentType = "text/css";
            } else if (target.endsWith(".png")) {
                contentType = " image/png";
            } else if (target.endsWith(".gif")) {
                contentType = " image/gif";
            }
            response.setContentType(contentType + ";charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                ByteStreams.copy(fileInputStream, response.getOutputStream());
            } finally {
                Closeables.closeQuietly(fileInputStream);
            }
        }
    }
    
    public void yuiHandler(String target, String instrumentedSrcDir, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException {
    	
    	int srcIndex = target.indexOf("/src");
    	String filePath = instrumentedSrcDir + target.substring(srcIndex + 4);
    	File file = new File(filePath);
    	if (!file.exists()) {
    		file = resourceResolver.getResource(target);
    	}
		if (file != null) {
		    log.debug("Serve resource : " + target + " to target file :" + file);
		    String contentType = "text/html";
		    if (target.endsWith(".js")) {
		        contentType = "application/javascript";
		    } else if (target.endsWith(".css")) {
		        contentType = "text/css";
		    } else if (target.endsWith(".png")) {
		        contentType = " image/png";
		    } else if (target.endsWith(".gif")) {
		        contentType = " image/gif";
		    }
		    response.setContentType(contentType + ";charset=utf-8");
		    response.setStatus(HttpServletResponse.SC_OK);
		    baseRequest.setHandled(true);
		    FileInputStream fileInputStream = null;
		    try {
		        fileInputStream = new FileInputStream(file);
		        ByteStreams.copy(fileInputStream, response.getOutputStream());
		    } finally {
		        Closeables.closeQuietly(fileInputStream);
		    }
		}
	}
}
