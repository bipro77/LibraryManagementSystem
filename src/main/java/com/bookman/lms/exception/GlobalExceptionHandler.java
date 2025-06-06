package com.bookman.lms.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@Value("${app.debug:true}") // Set this in application.properties
	private boolean debug;

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
		return buildErrorResponse(ex, ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
		return buildErrorResponse(ex, ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst()
				.orElse("Validation failed.");
		return buildErrorResponse(ex, message, HttpStatus.BAD_REQUEST);
	}

	// JWT Expired
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException ex) {
		return buildErrorResponse(ex, "JWT token has expired", HttpStatus.UNAUTHORIZED);
	}

	// Invalid signature
	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<Map<String, Object>> handleSignatureException(SignatureException ex) {
		return buildErrorResponse(ex, "JWT signature is invalid", HttpStatus.UNAUTHORIZED);
	}

	// Malformed JWT
	@ExceptionHandler(MalformedJwtException.class)
	public ResponseEntity<Map<String, Object>> handleMalformedJwtException(MalformedJwtException ex) {
		return buildErrorResponse(ex, "Malformed JWT token", HttpStatus.BAD_REQUEST);
	}

	// Catch-all for other JWT issues
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<Map<String, Object>> handleJwtException(JwtException ex) {
		return buildErrorResponse(ex, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleAllOtherExceptions(Exception ex) {
		return buildErrorResponse(ex, "Internal Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Utility method to structure the error response
	private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, String message, HttpStatus status) {
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("timestamp", LocalDateTime.now());
		errorBody.put("status", status.value());
		errorBody.put("error", status.getReasonPhrase());
		errorBody.put("message", message);

		if (debug) {
			errorBody.put("trace", ex.toString());
			String errorTrace = "Exception at: \n trace: " + errorBody.get("trace") + "\n error: "
					+ errorBody.get("error") + "\n message: " + errorBody.get("message") + "\n timestamp: "
					+ errorBody.get("timestamp") + "\n status: " + errorBody.get("status");
			System.err.println(errorTrace);
		}
		return new ResponseEntity<>(errorBody, status);
	}
}
