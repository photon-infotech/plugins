/**
 * Android Maven Plugin - android-maven-plugin
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
package com.photon.maven.plugins.android.config;

import com.photon.maven.plugins.android.config.ConfigPojo;
import com.photon.maven.plugins.android.config.PullParameter;

public class DummyMojo {

	@ConfigPojo
	private DummyConfigPojo configPojo;

	// Maven injected parameters
	private String configPojoStringValue;
	private Boolean configPojoBooleanValue;
    private String[] configPojoMethodValue;

	@PullParameter(defaultValue = "hello")
	private String parsedStringValue;

	@PullParameter(defaultValue = "true")
	private Boolean parsedBooleanValue;

	@PullParameter(defaultValueGetterMethod = "getDefaultMethodValue")
	private String[] parsedMethodValue;

	public String[] getDefaultMethodValue()
	{
		return new String[] {"a","b"};
	}
	
	public void setConfigPojo(DummyConfigPojo configPojo) {
		this.configPojo = configPojo;
	}

	public void setConfigPojoStringValue(String configPojoStringValue) {
		this.configPojoStringValue = configPojoStringValue;
	}

	public void setConfigPojoBooleanValue(Boolean configPojoBooleanValue) {
		this.configPojoBooleanValue = configPojoBooleanValue;
	}

	public DummyConfigPojo getConfigPojo() {
		return configPojo;
	}

	public String getConfigPojoStringValue() {
		return configPojoStringValue;
	}

	public Boolean getConfigPojoBooleanValue() {
		return configPojoBooleanValue;
	}

	public String getParsedStringValue() {
		return parsedStringValue;
	}

	public Boolean getParsedBooleanValue() {
		return parsedBooleanValue;
	}

	public String[] getParsedMethodValue() {
		return parsedMethodValue;
	}

}
