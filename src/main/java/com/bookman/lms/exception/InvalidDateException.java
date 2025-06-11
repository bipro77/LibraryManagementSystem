package com.bookman.lms.exception;

/**
 * Custom exception to indicate invalid loan dates in the book loan entity.
 */
public class InvalidDateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidDateException(String message) {
        super(message);
    }

    public InvalidDateException(String message, Throwable cause) {
        super(message, cause);
    }
}