package com.subtitlescorrector.domain;

import java.util.List;

public class SubtitleUnitData {

	String format; 
	Integer number;
	String timestampFrom;
	String timestampTo;
	String text;
	String textBeforeCorrection;
	
	List<List<EditOperation>> editOperations;
	
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
	
	@Override
	public String toString() {
		return "SubtitleUnitData [format=" + format + ", number=" + number + ", timestampFrom=" + timestampFrom
				+ ", timestampTo=" + timestampTo + ", text=" + text + ", textBeforeCorrection=" + textBeforeCorrection
				+ ", editOperations=" + editOperations + "]";
	}
	
}
