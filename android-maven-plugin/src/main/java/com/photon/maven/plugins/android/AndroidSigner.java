/*
 * ###
 * Android Maven Plugin - android-maven-plugin
 * 
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ###
 */
/*
 * Copyright (C) 2009 Jayway AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.photon.maven.plugins.android;

/**
 * Can sign Android applications (apk's).
 *
 */
public class AndroidSigner {

    public enum Debug {TRUE, FALSE, BOTH, AUTO}

    ;

    private final Debug debug;

    public AndroidSigner(String debug) {
        if (debug == null) {
            throw new IllegalArgumentException("android.sign.debug must be 'true', 'false', 'both' or 'auto'.");
        }
        try {
            this.debug = Debug.valueOf(debug.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("android.sign.debug must be 'true', 'false', 'both' or 'auto'.", e);
        }
    }

    public AndroidSigner(Debug debug) {
        this.debug = debug;
    }

    public boolean isSignWithDebugKeyStore() {
//    	System.out.println("Debug Value - isSignWithDebugKeyStore() ===============>>>>>>>>>>>>>>>>> " + debug.toString());
        if (debug == Debug.TRUE) {
            return true;
        }
        if (debug == Debug.BOTH) {
            return true;
        }
        if (debug == Debug.FALSE) {
            return false;
        }
        if (debug == Debug.AUTO) {
            //TODO: This is where to add logic for skipping debug sign if there are other keystores configured.
            return true;
        }
        throw new IllegalStateException("Could not determine whether to sign with debug keystore.");
    }

    public boolean shouldCreateBothSignedAndUnsignedApk() {
        return debug == Debug.BOTH;
    }

}
