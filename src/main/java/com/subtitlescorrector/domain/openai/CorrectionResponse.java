package com.subtitlescorrector.domain.openai;

public class CorrectionResponse {
	String number;
	String correction;
	String description;
	public String getCorrection() {
		return correction;
	}
	public void setCorrection(String correction) {
		this.correction = correction;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	
}
