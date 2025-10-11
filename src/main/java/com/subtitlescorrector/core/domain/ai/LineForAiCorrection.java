package com.subtitlescorrector.core.domain.ai;

/**
 * Data holder used for passing subtitle lines to AI for correction
 * @author Gavrilo Adamovic
 *
 */
public class LineForAiCorrection {

	String number;
	String text;
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
