/**
 * Android Maven Plugin - android-maven-plugin
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
package com.photon.maven.plugins.android.config;

import static org.junit.Assert.*;

import org.junit.Test;

import com.photon.maven.plugins.android.config.ConfigHandler;

public class ConfigHandlerTest {

	private DummyMojo mojo = new DummyMojo();

	@Test
	public void testParseConfigurationDefault() throws Exception {
		ConfigHandler configHandler = new ConfigHandler(mojo);
		configHandler.parseConfiguration();
		assertTrue(mojo.getParsedBooleanValue());
	}

	@Test
	public void testParseConfigurationFromConfigPojo() throws Exception {
		mojo.setConfigPojo(new DummyConfigPojo("from config pojo", null));
		ConfigHandler configHandler = new ConfigHandler(mojo);
		configHandler.parseConfiguration();
		assertEquals("from config pojo",mojo.getParsedStringValue());
	}

	@Test
	public void testParseConfigurationFromMaven() throws Exception {
		mojo.setConfigPojoStringValue("maven value");
		ConfigHandler configHandler = new ConfigHandler(mojo);
		configHandler.parseConfiguration();
		assertEquals("maven value",mojo.getParsedStringValue());
	}

	@Test
	public void testParseConfigurationDefaultMethodValue() throws Exception {
		ConfigHandler configHandler = new ConfigHandler(mojo);
		configHandler.parseConfiguration();
		assertArrayEquals(new String[] {"a","b"},mojo.getParsedMethodValue());
	}
}
