package com.photon.phresco.plugins;

public class HeadingBean {
	/**
	 *
	 */
	private Integer headingType;
	private String headingText;
	private String reference;
	private Integer pageIndex;

	/**
	 *
	 */
	public HeadingBean(Integer type, String text, String reference,
			Integer pageIndex) {
		this.headingType = type;
		this.headingText = text;
		this.reference = reference;
		this.pageIndex = pageIndex;
	}

	/**
	 *
	 */
	public Integer getHeadingType() {
		return this.headingType;
	}

	/**
	 *
	 */
	public String getHeadingText() {
		return this.headingText;
	}

	/**
	 *
	 */
	public String getReference() {
		return this.reference;
	}

	/**
	 *
	 */
	public Integer getPageIndex() {
		return this.pageIndex;
	}

}
