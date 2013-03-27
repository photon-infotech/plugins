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

import net.awired.jscoverage.instrumentation.JsInstrumentor;
import net.awired.jstest.resource.ResourceResolver;
import net.awired.jstest.runner.Runner;
import net.awired.jstest.runner.RunnerType;
import net.awired.jstest.server.handler.RunnerResourceHandler;
import org.antlr.stringtemplate.StringTemplate;

public class DefaultRunner extends Runner {

    public DefaultRunner() {
        super(RunnerType.DEFAULT);
    }

    @Override
    public void replaceTemplateVars(StringTemplate template) {
        template.setAttribute("testResources", buildTestResources(resolver));
        template.setAttribute("sources", htmlResourceTranformer.buildTagsFromResources(resolver.FilterSourcesKeys()));
        template.setAttribute("tests", htmlResourceTranformer.buildTagsFromResources(resolver.FilterTestsKeys()));
    }

    private String buildTestResources(ResourceResolver resolver) {
        StringBuilder res = new StringBuilder();
        htmlResourceTranformer.appendTag(res,
                RunnerResourceHandler.RUNNER_RESOURCE_PATH + testType.getTesterManager());
        htmlResourceTranformer.appendTag(res,
                RunnerResourceHandler.RUNNER_RESOURCE_PATH + JsInstrumentor.JSCOV_FILE.substring(1));
        return res.toString();
    }

}
