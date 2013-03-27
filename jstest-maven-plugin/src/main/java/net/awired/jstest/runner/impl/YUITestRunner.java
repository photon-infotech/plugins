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
package net.awired.jstest.runner.impl;

import java.io.File;
import java.util.Map;

import net.awired.jstest.resource.ResourceResolver;
import net.awired.jstest.runner.RunnerType;
import net.awired.jstest.server.handler.RunnerResourceHandler;

import org.antlr.stringtemplate.StringTemplate;

public class YUITestRunner extends DefaultRunner {
	
	 

    public YUITestRunner() {
        this.runnerType = RunnerType.YUI;
    }
    
    @Override
    public void replaceTemplateVars(StringTemplate template) {
        template.setAttribute("testResources", buildTestResources());
        template.setAttribute("sources", htmlResourceTranformer.buildTagsFromResources(resolver.FilterNonYUISourcesKeys()));
        buildSrcNames(template, "srcNames", resolver.FilterSourcesKeysAlone());
        buildTestModules(template, resolver.FilterTestsKeys());
    }
    
   

	private String buildTestResources() {
        StringBuilder res = new StringBuilder();
        
        if (runnerType.getAmdFile() != null) {
            htmlResourceTranformer.appendTag(res, ResourceResolver.SRC_RESOURCE_PREFIX + runnerType.getAmdFile());
        }
        
        for (String testerResource : testType.getTesterResources()) {
            htmlResourceTranformer.appendTag(res, ResourceResolver.SRC_RESOURCE_PREFIX + testerResource);
        }
        
        htmlResourceTranformer.appendTag(res,
                RunnerResourceHandler.RUNNER_RESOURCE_PATH + testType.getTesterManager());
        
       /* htmlResourceTranformer.appendTag(res,
                RunnerResourceHandler.RUNNER_RESOURCE_PATH + JsInstrumentor.JSCOV_FILE.substring(1));*/
        return res.toString();
    }
	
	private void buildSrcNames(StringTemplate template, String templateKey, Map<String, File> src) {
		StringBuilder srcNames = new StringBuilder();
		for (String key : src.keySet()) {
			if (key.endsWith(".js")) {
				File file = new File(key);
				String fileNameWithExt = file.getName();
				String fileName = fileNameWithExt.substring(0, fileNameWithExt.indexOf("."));

				srcNames.append("'");
				srcNames.append(fileName);
				srcNames.append("',");
			}
		}
		template.setAttribute(templateKey, srcNames.toString().substring(0, srcNames.length() - 1));
	}
	
    private void buildTestModules(StringTemplate template, Map<String, File> testSources) {
        StringBuilder modules = new StringBuilder();
        StringBuilder testNames = new StringBuilder();
        for (String key : testSources.keySet()) {
        	if (key.endsWith(".js")) {
        		File file = new File(key);
        		String fileNameWithExt = file.getName();
        		String fileName = fileNameWithExt.substring(0, fileNameWithExt.indexOf("."));
        		appendModule(modules, key, fileName);
        		testNames.append("'");
        		testNames.append(fileName);
        		testNames.append("',");
        	}
        	
        	//'logintest': {
            //fullpath: './testJs/LoginTest.js',
            //requires: [ 'test']
        	//}
        }
        
        template.setAttribute("modules", modules.toString().substring(0, modules.length() - 2));
        template.setAttribute("testNames", testNames.toString().substring(0, testNames.length() - 1));

	}

    private void appendModule(StringBuilder builder, String path, String fileName) {
    	builder.append("'");
    	builder.append(fileName);
    	builder.append("': { ");
    	builder.append("fullpath: '.");
    	builder.append(path);
    	builder.append("',");
    	builder.append("requires: ['test'] },\n");
    }

}
