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

import net.awired.jstest.executor.Executor;
import net.awired.jstest.executor.PhantomJsExecutor;
import net.awired.jstest.executor.RunnerExecutor;

public enum TestType {
    JASMINE(new RunnerExecutor(), "jasmineManager.js", "jasmine.js", "jasmine-html.js", "jasmine.css"), //
    QUNIT(new PhantomJsExecutor(), "qunitManager.js", "qunit.js", "qunit.css"), //
    YUITEST(new RunnerExecutor(), "yuiTestManager.js", "yui/build/yui/yui-min.js", ""),
    ;

    private String[] testerResources;
    private String testerManager;
    private Executor executor;

    private TestType(Executor executor, String testerManager, String... testerResources) {
        this.testerResources = testerResources;
        this.executor = executor;
        this.testerManager = testerManager;
    }

    public String getTesterManager() {
        return testerManager;
    }

    public void setTesterManager(String testerManager) {
        this.testerManager = testerManager;
    }

    public String[] getTesterResources() {
        return testerResources;
    }

    public void setTesterResources(String[] testerResources) {
        this.testerResources = testerResources;
    }

    public Executor getExecutor() {
        return executor;
    }
}
