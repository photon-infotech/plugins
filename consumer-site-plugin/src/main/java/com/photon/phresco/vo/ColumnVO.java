package com.photon.phresco.vo;

import java.io.Serializable;

public class ColumnVO implements Serializable {

	private String columnName;
	private String fieldName;
	private String fieldType;
	private String destination;
	private boolean isEmpty;
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

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
}
