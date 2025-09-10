package com.subtitlescorrector.core.domain;

public enum SecondMillisecondDelimiterRegex {


	DOT("\\.", "."), COMMA(",", ",");

	SecondMillisecondDelimiterRegex(String regex, String formattedDelimiter) {
		this.regex = regex;
		this.formattedDelimiter = formattedDelimiter;
	}
	
	private String regex;
	private String formattedDelimiter;
	
	public String getRegex() {
		return regex;
	}

	public String getFormattedDelimiter() {
		return formattedDelimiter;
	}
}
