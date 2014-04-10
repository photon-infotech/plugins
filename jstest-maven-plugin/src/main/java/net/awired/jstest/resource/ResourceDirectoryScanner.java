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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.codehaus.plexus.util.DirectoryScanner;

public class ResourceDirectoryScanner {

    private DirectoryScanner directoryScanner = new DirectoryScanner();

    public List<String> scan(ResourceDirectory scriptDirectory) {
        Set<String> set = new LinkedHashSet<String>();
        for (String include : scriptDirectory.getIncludes()) {
            set.addAll(performScan(scriptDirectory.getDirectory(), include, scriptDirectory.getExcludes()));
        }
        return new ArrayList<String>(set);
    }

    private List<String> performScan(File directory, String include, List<String> excludes) {
        directoryScanner.setBasedir(directory);
        directoryScanner.setIncludes(new String[] { include });
        directoryScanner.setExcludes(excludes.toArray(new String[] {}));
        directoryScanner.addDefaultExcludes();
        directoryScanner.scan();
        ArrayList<String> result = new ArrayList<String>(asList(directoryScanner.getIncludedFiles()));
        Collections.sort(result);
        return result;
    }

}
