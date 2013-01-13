package com.photon.phresco.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CsvFileVO implements Serializable {

	private String language;
	private String contentType;
	private String contentTypeName;
	private String phpFunction;
	private String fieldType;
	private String currentFolderPath;
	private String targetFolder;
	
	

	private Map<String, String> titleMap = new HashMap<String, String>();
	private Map<String, String> descriptionMap = new HashMap<String, String>();
	private Map<String, String> extraMap = new HashMap<String, String>();
	private Map<String, String> imageMap = new HashMap<String, String>();
	private Map<String, String> categoryMap = new HashMap<String, String>();
	private Map<String, String> urlMap = new HashMap<String, String>();
	private Map<String, String> metadataMap = new HashMap<String, String>();
	
	public String getTargetFolder() {
		return targetFolder;
	}

	public void setTargetFolder(String targetFolder) {
		this.targetFolder = targetFolder;
	}

	public String getCurrentFolderPath() {
		return currentFolderPath;
	}

	public void setCurrentFolderPath(String currentFolderPath) {
		this.currentFolderPath = currentFolderPath;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentTypeName() {
		return contentTypeName;
	}

	public void setContentTypeName(String contentTypeName) {
		this.contentTypeName = contentTypeName;
	}

	public Map<String, String> getTitleMap() {
		return titleMap;
	}

	public void setTitleMap(Map<String, String> titleMap) {
		this.titleMap = titleMap;
	}

	public Map<String, String> getDescriptionMap() {
		return descriptionMap;
	}

	public void setDescriptionMap(Map<String, String> descriptionMap) {
		this.descriptionMap = descriptionMap;
	}

	public Map<String, String> getExtraMap() {
		return extraMap;
	}

	public void setExtraMap(Map<String, String> extraMap) {
		this.extraMap = extraMap;
	}

	public Map<String, String> getImageMap() {
		return imageMap;
	}

	public void setImageMap(Map<String, String> imageMap) {
		this.imageMap = imageMap;
	}

	public Map<String, String> getCategoryMap() {
		return categoryMap;
	}

	public void setCategoryMap(Map<String, String> categoryMap) {
		this.categoryMap = categoryMap;
	}

	public Map<String, String> getUrlMap() {
		return urlMap;
	}

	public void setUrlMap(Map<String, String> urlMap) {
		this.urlMap = urlMap;
	}

	public Map<String, String> getMetadataMap() {
		return metadataMap;
	}

	public void setMetadataMap(Map<String, String> metadataMap) {
		this.metadataMap = metadataMap;
	}

	public String getPhpFunction() {
		return phpFunction;
	}

	public void setPhpFunction(String phpFunction) {
		this.phpFunction = phpFunction;
	}	
}
