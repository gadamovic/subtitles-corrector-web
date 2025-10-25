package com.subtitlescorrector.core.domain.exception;

public class SubtitleFileParseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public SubtitleFileParseException() {
        super();
    }

    public SubtitleFileParseException(String message) {
        super(message);
    }
}
