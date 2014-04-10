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
package net.awired.jstest.resource;

import static java.util.Arrays.asList;
import java.io.File;
import java.util.List;

public class ResourceDirectory {

    public static final List<String> DEFAULT_INCLUDES = asList("**/**");
    public static final List<String> DEFAULT_EXCLUDES = asList("META-INF/**");

    private File directory;
    private List<String> includes;
    private List<String> excludes;
    private boolean updatable;

    public ResourceDirectory() {
    }

    public ResourceDirectory(File directory, List<String> sourceIncludes, List<String> sourceExcludes) {
        this.directory = directory;
        this.includes = sourceIncludes;
        this.excludes = sourceExcludes;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

}
