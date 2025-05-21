package com.bookman.lms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to be thrown when a Book is not found.
 * Annotated with @ResponseStatus to automatically set the HTTP status code to 404 NOT_FOUND.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Sets HTTP 404 status when this exception is thrown
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}