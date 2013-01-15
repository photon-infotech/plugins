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
