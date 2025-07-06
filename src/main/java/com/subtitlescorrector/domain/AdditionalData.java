package com.subtitlescorrector.domain;

public class AdditionalData {

	private String webSocketSessionId;
	private Boolean stripBTags;
	private Boolean stripITags;
	private Boolean stripUTags;
	private Boolean stripFontTags;
	private Integer correctorIndex = 1;
	/**
	 * Flag to indicate not to remove BOM from files if it is present
	 */
	private Boolean keepBOM;
	private Boolean aeToTj;
	private Boolean AEToTJ;
	private Boolean eToch;
	private Boolean EToCH;
	
	private Boolean aiEnabled;

	private int numberOfCorrectors;
	
	int processedLines = 0;
	int totalNumberOfLines = 0;

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

	public Boolean getKeepBOM() {
		return keepBOM;
	}

	public void setKeepBOM(Boolean keepBOM) {
		this.keepBOM = keepBOM;
	}

	public Boolean getConvertAeToTj() {
		return aeToTj;
	}

	public void setConvertAeToTj(Boolean aeToTj) {
		this.aeToTj = aeToTj;
	}

	public Boolean getConvertAEToTJ() {
		return AEToTJ;
	}

	public void setConvertAEToTJ(Boolean aEToTJ) {
		AEToTJ = aEToTJ;
	}

	public Boolean getConverteToch() {
		return eToch;
	}

	public void setConverteToch(Boolean eToch) {
		this.eToch = eToch;
	}

	public Boolean getConvertEToCH() {
		return EToCH;
	}

	public void setConvertEToCH(Boolean eToCH) {
		EToCH = eToCH;
	}

	public int getProcessedLines() {
		return processedLines;
	}

	public void setProcessedLines(int processedLines) {
		this.processedLines = processedLines;
	}

	public int getTotalNumberOfLines() {
		return totalNumberOfLines;
	}

	public void setTotalNumberOfLines(int totalNumberOfLines) {
		this.totalNumberOfLines = totalNumberOfLines;
	}

	public Boolean getAiEnabled() {
		return aiEnabled;
	}

	public void setAiEnabled(Boolean aiEnabled) {
		this.aiEnabled = aiEnabled;
	}
	
}
