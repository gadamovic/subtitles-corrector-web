package com.subtitlescorrector.core.domain.metrics;

public enum Feature {

	CORRECTION("correction"), CONVERSION("conversion");

	private String name;

	Feature(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}
