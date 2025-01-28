package com.subtitlescorrector.domain;

public class AdditionalData {

	private String webSocketSessionId;
	private Boolean stripBTags;
	private Boolean stripITags;
	private Boolean stripUTags;
	private Boolean stripFontTags;
	private Integer correctorIndex = 1;

	private int numberOfCorrectors;

	public String getWebSocketSessionId() {
		return webSocketSessionId;
	}

	public void setWebSocketSessionId(String webSocketSessionId) {
		this.webSocketSessionId = webSocketSessionId;
	}

	public Boolean getStripBTags() {
		return stripBTags;
	}

	public void setStripBTags(Boolean stripBTags) {
		this.stripBTags = stripBTags;
	}

	public Boolean getStripITags() {
		return stripITags;
	}

	public void setStripITags(Boolean stripITags) {
		this.stripITags = stripITags;
	}

	public Boolean getStripUTags() {
		return stripUTags;
	}

	public void setStripUTags(Boolean stripUTags) {
		this.stripUTags = stripUTags;
	}

	public Boolean getStripFontTags() {
		return stripFontTags;
	}

	public void setStripFontTags(Boolean stripFontTags) {
		this.stripFontTags = stripFontTags;
	}

	public Integer getCorrectorIndex() {
		return correctorIndex;
	}

	public void setCorrectorIndex(Integer correctorIndex) {
		this.correctorIndex = correctorIndex;
	}

	public int getNumberOfCorrectors() {
		return numberOfCorrectors;
	}

	public void setNumberOfCorrectors(int numberOfCorrectors) {
		this.numberOfCorrectors = numberOfCorrectors;
	}

}
