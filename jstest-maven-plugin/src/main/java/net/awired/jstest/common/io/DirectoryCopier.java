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
package net.awired.jstest.common.io;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class DirectoryCopier {

    private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
    private FileFilterUtilsWrapper fileFilterUtilsWrapper = new FileFilterUtilsWrapper();

    public void copyDirectory(File srcDir, File destDir) throws IOException {
        IOFileFilter filter = FileFileFilter.FILE;
        filter = fileFilterUtilsWrapper.or(DirectoryFileFilter.DIRECTORY, filter);
        filter = fileFilterUtilsWrapper.and(HiddenFileFilter.VISIBLE, filter);
        fileUtilsWrapper.copyDirectory(srcDir, destDir, filter);
    }

}
