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
package com.photon.maven.plugins.android.phase09package;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.photon.maven.plugins.android.AbstractAndroidMojoTestCase;
import com.photon.maven.plugins.android.config.ConfigHandler;
import com.photon.maven.plugins.android.phase09package.ApkMojo;

@RunWith( Parameterized.class )
public class ApkMojoTest
extends AbstractAndroidMojoTestCase<ApkMojo>
{

	@Parameters
	static public List<Object[]> suite()
	{
		final List<Object[]> suite = new ArrayList<Object[]>();

		suite.add( new Object[] { "apk-config-project1", new String[0] } );
		suite.add( new Object[] { "apk-config-project2", new String[] { "persistence.xml" } } );
		suite.add( new Object[] { "apk-config-project3", new String[] { "services/**", "persistence.xml" } } );

		return suite;
	}

	private final String	projectName;

	private final String[]	expected;

	public ApkMojoTest( String projectName, String[] expected )
	{
		this.projectName = projectName;
		this.expected = expected;
	}

	@Override
	public String getPluginGoalName()
	{
		return "apk";
	}

	@Override
	@Before
	public void setUp()
	throws Exception
	{
		super.setUp();
	}

	@Override
	@After
	public void tearDown()
	throws Exception
	{
		super.tearDown();
	}

	@Test
	public void testConfigHelper()
	throws Exception
	{
		final ApkMojo mojo = createMojo( this.projectName );

		final ConfigHandler cfh = new ConfigHandler( mojo );

		cfh.parseConfiguration();

		final String[] includes = getFieldValue( mojo, "apkMetaIncludes" );

		Assert.assertNotNull( includes );
		Assert.assertArrayEquals( this.expected, includes );
	}

	protected <T> T getFieldValue( Object object, String fieldName )
	throws IllegalAccessException
	{
		return (T) super.getVariableValueFromObject( object, fieldName );
	}

}
