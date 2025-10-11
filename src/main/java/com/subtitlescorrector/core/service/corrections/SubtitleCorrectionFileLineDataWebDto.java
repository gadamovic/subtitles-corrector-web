package com.subtitlescorrector.core.service.corrections;

import java.util.List;

import com.subtitlescorrector.core.domain.CompositeEditOperation;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;

/**
 * Subset of main class used to exchange data with the client app
 *
 */

public class SubtitleCorrectionFileLineDataWebDto {

	Integer number;
	SubtitleTimestamp timestampFrom;
	SubtitleTimestamp timestampTo;
	SubtitleTimestamp timestampFromShifted;
	SubtitleTimestamp timestampToShifted;
	String text;
	String textBeforeCorrection;
	
	List<CompositeEditOperation> compEditOperations;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
	
	
	
}
