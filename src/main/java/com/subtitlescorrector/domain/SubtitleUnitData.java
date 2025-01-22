package com.subtitlescorrector.domain;

import java.util.List;

public class SubtitleUnitData {

	String format; 
	Integer number;
	String timestampFrom;
	String timestampTo;
	String timestampFromShifted;
	String timestampToShifted;
	String text;
	String textBeforeCorrection;
	
	List<List<EditOperation>> editOperations;
	List<CompositeEditOperation> compEditOperations;
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getTimestampFrom() {
		return timestampFrom;
	}
	public void setTimestampFrom(String timestampFrom) {
		this.timestampFrom = timestampFrom;
	}
	public String getTimestampTo() {
		return timestampTo;
	}
	public void setTimestampTo(String timestampTo) {
		this.timestampTo = timestampTo;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<List<EditOperation>> getEditOperations() {
		return editOperations;
	}
	public void setEditOperations(List<List<EditOperation>> editOperations) {
		this.editOperations = editOperations;
	}
	
	public String getTextBeforeCorrection() {
		return textBeforeCorrection;
	}
	public void setTextBeforeCorrection(String textBeforeCorrection) {
		this.textBeforeCorrection = textBeforeCorrection;
	}
	
	public List<CompositeEditOperation> getCompEditOperations() {
		return compEditOperations;
	}
	public void setCompEditOperations(List<CompositeEditOperation> compEditOperations) {
		this.compEditOperations = compEditOperations;
	}
	
	public String getTimestampFromShifted() {
		return timestampFromShifted;
	}
	public void setTimestampFromShifted(String timestampFromShifted) {
		this.timestampFromShifted = timestampFromShifted;
	}
	public String getTimestampToShifted() {
		return timestampToShifted;
	}
	public void setTimestampToShifted(String timestampToShifted) {
		this.timestampToShifted = timestampToShifted;
	}
	@Override
	public String toString() {
		return "SubtitleUnitData [format=" + format + ", number=" + number + ", timestampFrom=" + timestampFrom
				+ ", timestampTo=" + timestampTo + ", timestampFromShifted=" + timestampFromShifted
				+ ", timestampToShifted=" + timestampToShifted + ", text=" + text + ", textBeforeCorrection="
				+ textBeforeCorrection + ", editOperations=" + editOperations + ", compEditOperations="
				+ compEditOperations + "]";
	}
	
}
