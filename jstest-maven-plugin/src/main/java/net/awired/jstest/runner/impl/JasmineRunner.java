package net.awired.jstest.runner.impl;

import java.util.Arrays;
import java.util.List;

import net.awired.jscoverage.instrumentation.JsInstrumentor;
import net.awired.jstest.resource.ResourceResolver;
import net.awired.jstest.runner.Runner;
import net.awired.jstest.runner.RunnerType;
import net.awired.jstest.server.handler.RunnerResourceHandler;

import org.antlr.stringtemplate.StringTemplate;

public class JasmineRunner extends Runner {

	public JasmineRunner() {
        super(RunnerType.JASMINE);
    }

    @Override
    public void replaceTemplateVars(StringTemplate template) {
        String buildTestResources = buildTestResources(resolver);
		template.setAttribute("testResources", buildTestResources);
        
        List<String> testerResources = Arrays.asList(testType.getTesterResources());
        
        String sourceResources = htmlResourceTranformer.buildTagsFromResources(resolver.FilterSourcesKeys(), testerResources);
		template.setAttribute("sources", sourceResources);
        String testsResources = htmlResourceTranformer.buildTagsFromResources(resolver.FilterTestsKeys());
		template.setAttribute("tests", testsResources);
    }

    private String buildTestResources(ResourceResolver resolver) {
        StringBuilder res = new StringBuilder();
        htmlResourceTranformer.appendTag(res,
                RunnerResourceHandler.RUNNER_RESOURCE_PATH + testType.getTesterManager());
        htmlResourceTranformer.appendTag(res,
                RunnerResourceHandler.RUNNER_RESOURCE_PATH + JsInstrumentor.JSCOV_FILE.substring(1));
        for (String testerResource : testType.getTesterResources()) {
            htmlResourceTranformer.appendTag(res, ResourceResolver.LIB_RESOURCE_PREFIX + testerResource);
        }
        return res.toString();
    }

}

