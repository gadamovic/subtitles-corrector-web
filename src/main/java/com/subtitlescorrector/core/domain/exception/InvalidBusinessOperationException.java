package com.subtitlescorrector.core.domain.exception;

public class InvalidBusinessOperationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidBusinessOperationException() {
        super();
    }

    public InvalidBusinessOperationException(String message) {
        super(message);
    }
}
