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
package net.awired.jstest.runner;

import java.util.List;
import net.awired.jstest.resource.ResourceResolver;
import net.awired.jstest.runner.impl.AlmondRunner;
import net.awired.jstest.runner.impl.CurlRunner;
import net.awired.jstest.runner.impl.DefaultRunner;
import net.awired.jstest.runner.impl.JasmineRunner;
import net.awired.jstest.runner.impl.RequireJsRunner;
import net.awired.jstest.runner.impl.YUITestRunner;
import net.awired.jstest.runner.impl.UseYUIRunner;

public enum RunnerType {
    DEFAULT(DefaultRunner.class, "/runnerTemplate/defaultRunner.tpl"), //
    JASMINE(JasmineRunner.class, "/runnerTemplate/defaultRunner.tpl"), //
    JASMINE2(JasmineRunner.class, "/runnerTemplate/defaultRunner.tpl"), //
    REQUIREJS(RequireJsRunner.class, "/runnerTemplate/requireJsRunner.tpl", "require.js"), //
    ALMOND(AlmondRunner.class, "/runnerTemplate/almondRunner.tpl", "almond.js"), //
    CURL(CurlRunner.class, "/runnerTemplate/curlRunner.tpl", "curl.js"), //
    USEYUI(UseYUIRunner.class, "/runnerTemplate/useYUIRunner.tpl", "require.js"), //
    YUI(YUITestRunner.class, "/runnerTemplate/yuiTestRunner.tpl"), //
    ;

    private String template;
    private String amdFile;
    private final Class<? extends Runner> runnerClass;

    private RunnerType(Class<? extends Runner> runnerClass, String template) {
        this.runnerClass = runnerClass;
        this.template = template;
    }

    private RunnerType(Class<? extends Runner> runnerClass, String template, String amdFile) {
        this.runnerClass = runnerClass;
        this.template = template;
        this.amdFile = amdFile;
    }

    ///////////////////

    public void setAmdFile(String amdFile) {
        this.amdFile = amdFile;
    }

    public String getAmdFile() {
        return amdFile;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public Runner buildRunner(TestType testType, ResourceResolver resolver, boolean serverMode, boolean debug,
            List<String> amdPreloads) {
        try {
            Runner runner = runnerClass.newInstance();
            runner.setTestType(testType);
            runner.setResolver(resolver);
            runner.setDebug(debug);
            runner.setServerMode(serverMode);
            runner.setAmdPreloads(amdPreloads);
            return runner;
        } catch (Exception e) {
            throw new RuntimeException("Cannot instanciate runner", e);
        }
    }
}
