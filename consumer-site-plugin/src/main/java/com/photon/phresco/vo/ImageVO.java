/**
 * Phresco Plugins
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
package com.photon.phresco.vo;

import java.io.Serializable;

public class ImageVO implements Serializable {

	private String fileName;
	private String key;
	private String destination;
	private int fileNameSize;
	private String datatype;

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public int getFileNameSize() {
		return fileNameSize;
	}

	public void setFileNameSize(int fileNameSize) {
		this.fileNameSize = fileNameSize;
	}

	public String getKey() {
		return key;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String get() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
