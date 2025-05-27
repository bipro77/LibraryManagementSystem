package com.bookman.lms.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that a requested resource (e.g., User, Book) was not found.
 *
 * The @ResponseStatus annotation ensures that when this exception is thrown from
 * a Spring controller method, Spring will automatically return an HTTP 404 Not Found status
 * to the client, along with the exception message.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Maps this exception to an HTTP 404 Not Found status
public class ResourceNotFoundException extends RuntimeException {

    // Constructor to create an exception with a custom message
    public ResourceNotFoundException(String message) {
        super(message); // Calls the constructor of the RuntimeException (super class)
    }

    // Constructor to create an exception with a custom message and a cause (another Throwable)
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause); // Calls the constructor of the RuntimeException (super class)
    }
}