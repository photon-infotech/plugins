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
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class FileUtilsWrapper {

	public String readFileToString(File file) throws IOException {
		return FileUtils.readFileToString(file);
	}

	public void forceMkdir(File file) throws IOException {
		FileUtils.forceMkdir(file);
	}

	public Collection<File> listFiles(File file, String[] extensions, boolean recursive) {
		return FileUtils.listFiles(file, extensions, recursive);
	}

	public void writeStringToFile(File file, String data, String encoding) throws IOException {
		FileUtils.writeStringToFile(file, data, encoding);
	}

	public void copyDirectory(File srcDir, File destDir, IOFileFilter filter) throws IOException {
		FileUtils.copyDirectory(srcDir, destDir, filter);
	}

}
