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
package com.photon.maven.plugins.android.common;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;

import com.photon.maven.plugins.android.common.NativeHelper;


public class NativeHelperTest {

    @Test
    public void invalidVersions()
    {
        String[] versions = {"r4", "r5", "r5b", "r5c", "r6", "r6b"};

        for (int i = 0; i < versions.length; i++) {
            String version = versions[i];
            try {
                NativeHelper.validateNDKVersion(7,version);
                Assert.fail("Version should fail: " + version);
            } catch (MojoExecutionException e) {
            }
        }
    }

    @Test
    public void validVersions()
    {
        String[] versions = {"r7", "r8a", "r8z", "r10", "r19b", "r25", "r100", "r100b"};

        for (int i = 0; i < versions.length; i++) {
            String version = versions[i];
            try {
                NativeHelper.validateNDKVersion(7,version);
            } catch (MojoExecutionException e) {
                Assert.fail("Version should not fail: " + version);
            }
        }
    }


}
