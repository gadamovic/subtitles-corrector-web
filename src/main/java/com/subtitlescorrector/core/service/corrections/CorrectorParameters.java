package com.subtitlescorrector.core.service.corrections;

public class CorrectorParameters {

	private Boolean stripBTags;
	private Boolean stripITags;
	private Boolean stripUTags;
	private Boolean stripFontTags;
	private Integer correctorIndex = 1;
	/**
	 * Flag to indicate not to remove BOM from files if it is present
	 */
	private Boolean keepBOM;
	private Boolean convertAeToTj;
	private Boolean convertAEToTJ;
	private Boolean convertEToch;
	private Boolean convertEToCH;
	
	private int numberOfCorrectors;
	int processedLines = 0;
	int totalNumberOfLines = 0;
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
	public Boolean getKeepBOM() {
		return keepBOM;
	}
	public void setKeepBOM(Boolean keepBOM) {
		this.keepBOM = keepBOM;
	}
	public Boolean getConvertAeToTj() {
		return convertAeToTj;
	}
	public void setConvertAeToTj(Boolean convertAeToTj) {
		this.convertAeToTj = convertAeToTj;
	}
	public Boolean getConvertAEToTJ() {
		return convertAEToTJ;
	}
	public void setConvertAEToTJ(Boolean convertAEToTJ) {
		this.convertAEToTJ = convertAEToTJ;
	}
	public Boolean getConvertEToch() {
		return convertEToch;
	}
	public void setConvertEToch(Boolean convertEToch) {
		this.convertEToch = convertEToch;
	}
	public Boolean getConvertEToCH() {
		return convertEToCH;
	}
	public void setConvertEToCH(Boolean convertEToCH) {
		this.convertEToCH = convertEToCH;
	}
	public int getNumberOfCorrectors() {
		return numberOfCorrectors;
	}
	public void setNumberOfCorrectors(int numberOfCorrectors) {
		this.numberOfCorrectors = numberOfCorrectors;
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
	
	
	
}
