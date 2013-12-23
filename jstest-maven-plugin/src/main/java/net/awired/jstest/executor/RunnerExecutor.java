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
package net.awired.jstest.executor;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;

import org.apache.maven.plugin.logging.Log;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

public class RunnerExecutor implements Executor {

    private final WebDriver driver;
    private Log log = null;

    public RunnerExecutor() {
        this.driver = createDriver();
        if (!(driver instanceof JavascriptExecutor)) {
            throw new RuntimeException("The provided web driver can't execute JavaScript: " + driver.getClass());
        }
    }

    private WebDriver createDriver() {
        if (!HtmlUnitDriver.class.getName().equals("org.openqa.selenium.htmlunit.HtmlUnitDriver")) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends WebDriver> klass = (Class<? extends WebDriver>) Class
                        .forName("org.openqa.selenium.htmlunit.HtmlUnitDriver");
                Constructor<? extends WebDriver> ctor = klass.getConstructor();
                return ctor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Couldn't instantiate webDriverClassName", e);
            }
        }

        // We have extra configuration to do to the HtmlUnitDriver
        BrowserVersion htmlUnitBrowserVersion;
        try {
        	
          htmlUnitBrowserVersion = (BrowserVersion) BrowserVersion.class.getField("FIREFOX_3_6").get(
                    BrowserVersion.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HtmlUnitDriver driver = new HtmlUnitDriver(htmlUnitBrowserVersion) {
            @Override
            protected WebClient modifyWebClient(WebClient client) {
                client.setAjaxController(new NicelyResynchronizingAjaxController());

                //Disables stuff like this "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl notify WARNING: Obsolete content type encountered: 'text/javascript'."
                if (!false) {
                    client.setIncorrectnessListener(new IncorrectnessListener() {
                        public void notify(String arg0, Object arg1) {
                        }
                    });
                }

                return client;
            };
        };
        driver.setJavascriptEnabled(true);
        return driver;
    }

    public void execute(String runnerUrl) {
        try {
        	log.info("Running RunnerExecutor");
        	System.out.println("########## runnerUrl " + runnerUrl+"?emulator=true");
            driver.get(runnerUrl+"?emulator=true");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

    public void close() {
        driver.quit();
    }

    public void setTargetSrcDir(File targetSourceDirectory) {
        // TODO Auto-generated method stub
        
    }
    
    public void setLog(Log log) {
		this.log = log;
	}
 
}
