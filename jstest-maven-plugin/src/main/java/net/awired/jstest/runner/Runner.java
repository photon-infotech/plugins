package net.awired.jstest.runner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.awired.jstest.resource.ResourceResolver;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.codehaus.jackson.map.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;

public abstract class Runner {

    protected HtmlResourceTranformer htmlResourceTranformer = new HtmlResourceTranformer();
    protected ObjectMapper mapper = new ObjectMapper();
    protected ResourceResolver resolver;
    protected RunnerType runnerType;
    protected TestType testType;
    protected boolean serverMode;
    protected boolean debug;
    protected List<String> amdPreloads = new ArrayList<String>();

    protected Runner(RunnerType runnerType) {
        this.runnerType = runnerType;
    }

    abstract public void replaceTemplateVars(StringTemplate template) throws Exception;

    private void replaceCommonTemplateVars(StringTemplate template, int browserId, boolean emulator, UUID runId) {
        template.setAttribute("serverMode", serverMode ? "true" : "false");
        template.setAttribute("debug", debug);
        template.setAttribute("browserId", browserId);
        template.setAttribute("emulator", emulator);
        template.setAttribute("runId", runId);
    }

    public String generate(int browserId, boolean emulator, UUID runId) throws Exception {
        InputStream templateStream = getClass().getResourceAsStream(runnerType.getTemplate());
        if (templateStream == null) {
            throw new RuntimeException("Cannot found runner template : " + runnerType.getTemplate());
        }

        StringTemplate template;
        try {
            template = new StringTemplate(CharStreams.toString(new InputStreamReader(templateStream, "UTF-8")),
                    DefaultTemplateLexer.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse template " + runnerType.getTemplate(), e);
        }

        replaceCommonTemplateVars(template, browserId, emulator, runId);
        replaceTemplateVars(template);
        return template.toString();
    }

    public ResourceResolver getResolver() {
        return resolver;
    }

    public void setResolver(ResourceResolver resolver) {
        this.resolver = resolver;
    }

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public RunnerType getRunnerType() {
        return runnerType;
    }

    public boolean isServerMode() {
        return serverMode;
    }

    public void setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public List<String> getAmdPreloads() {
        return amdPreloads;
    }

    public void setAmdPreloads(List<String> amdPreloads) {
        Preconditions.checkNotNull(amdPreloads, "preload cannot be null");
        this.amdPreloads = amdPreloads;

    }

}
