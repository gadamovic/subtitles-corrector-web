package com.subtitlescorrector.core.service.corrections.vtt.domain;

import java.util.List;

import com.subtitlescorrector.core.domain.CompositeEditOperation;
import com.subtitlescorrector.core.domain.EditOperation;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;

public class VttSubtitleUnitData {

	SubtitleFormat format; 
	Integer number;
	SubtitleTimestamp timestampFrom;
	SubtitleTimestamp timestampTo;
	SubtitleTimestamp timestampFromShifted;
	SubtitleTimestamp timestampToShifted;
	String text;
	String textBeforeCorrection;
	String cues;
	
	List<List<EditOperation>> editOperations;
	List<CompositeEditOperation> compEditOperations;
	
	
	public SubtitleFormat getFormat() {
		return format;
	}
	public void setFormat(SubtitleFormat format) {
		this.format = format;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public SubtitleTimestamp getTimestampFrom() {
		return timestampFrom;
	}
	public void setTimestampFrom(SubtitleTimestamp timestampFrom) {
		this.timestampFrom = timestampFrom;
	}
	public SubtitleTimestamp getTimestampTo() {
		return timestampTo;
	}
	public void setTimestampTo(SubtitleTimestamp timestampTo) {
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
	
	public SubtitleTimestamp getTimestampFromShifted() {
		return timestampFromShifted;
	}
	public void setTimestampFromShifted(SubtitleTimestamp timestampFromShifted) {
		this.timestampFromShifted = timestampFromShifted;
	}
	public SubtitleTimestamp getTimestampToShifted() {
		return timestampToShifted;
	}
	public void setTimestampToShifted(SubtitleTimestamp timestampToShifted) {
		this.timestampToShifted = timestampToShifted;
	}
	public String getCues() {
		return cues;
	}
	public void setCues(String cues) {
		this.cues = cues;
	}
	@Override
	public String toString() {
		return "VttSubtitleUnitData [format=" + format + ", number=" + number + ", timestampFrom=" + timestampFrom
				+ ", timestampTo=" + timestampTo + ", timestampFromShifted=" + timestampFromShifted
				+ ", timestampToShifted=" + timestampToShifted + ", text=" + text + ", textBeforeCorrection="
				+ textBeforeCorrection + ", cues=" + cues + ", editOperations=" + editOperations
				+ ", compEditOperations=" + compEditOperations + "]";
	}

	
}
