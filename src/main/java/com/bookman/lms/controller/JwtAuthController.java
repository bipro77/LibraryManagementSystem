package com.bookman.lms.controller;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookman.lms.security.JwtUtil;
import com.bookman.lms.service.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class JwtAuthController {
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JwtUtil jwtUtil;
	@Autowired
	TokenBlacklistService tokenBlacklistService;
	@Autowired
	UserDetailsService userDetailsService;

	@PostMapping("login")
	public ResponseEntity<?> generateToken(@RequestBody Map<String, String> loginData) {
		String username = loginData.get("username");
		String password = loginData.get("password");
		// Check if user is blacklisted
		if (tokenBlacklistService.isTokenBlacklisted(username)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("message", "Token expired and blacklisted. Cannot log in."));
		}
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		String token = jwtUtil.generateToken(username);
		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}

	/**
	 * Logs out a user by blacklisting their JWT token in Redis. The token is stored
	 * with a TTL matching its remaining validity period.
	 *
	 * Steps: 1. Extract the JWT token from the HTTP request. 2. Validate the token
	 * against the username and user details. 3. If valid, calculate the remaining
	 * TTL based on the token's expiration time. 4. Store the token in Redis
	 * blacklist with the calculated TTL. 5. Return a success or error response
	 * accordingly.
	 *
	 * @param request The HTTP request containing the JWT token (usually in the
	 *                Authorization header).
	 * @return A ResponseEntity indicating the logout status.
	 */
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		// 1. Extract token from Authorization header or cookie
		String token = jwtUtil.getJwtTokenFromRequest(request);

		// 2. If token is missing, return 400 Bad Request
		if (token == null) {
			return ResponseEntity.badRequest().body(Map.of("message", "No token found in request"));
		}

		// 3. Extract username from token
		String username = jwtUtil.extractUsername(token);

		// 4. Load user details from the database
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		// 5. Validate the token's signature, expiration, and user identity
		boolean isValid = jwtUtil.validateToken(username, userDetails, token);

		if (isValid) {
			// 6. Get token expiration time
			Date expiry = jwtUtil.extractedClaims(token).getExpiration();

			// 7. Calculate TTL in milliseconds
			long ttlMillis = expiry.getTime() - System.currentTimeMillis();

			// 8. Only blacklist the token if TTL is positive
			if (ttlMillis > 0) {
				tokenBlacklistService.blacklistToken(token, ttlMillis);
			}

			// 9. Return success response
			return ResponseEntity.ok(Map.of("message", "Logged out successfully", "token", token));
		} else {
			// 10. If token is invalid or expired, return error response
			return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token"));
		}
	}

	/*
	 * @PostMapping("/logout") public ResponseEntity<?> logout(HttpServletRequest
	 * request) { String token = jwtUtil.getJwtTokenFromRequest(request); if (token
	 * == null) { return ResponseEntity.badRequest().body(Map.of("message",
	 * "No token found in request")); }
	 * 
	 * String username = jwtUtil.extractUsername(token); UserDetails userDetails =
	 * userDetailsService.loadUserByUsername(username); boolean isValid =
	 * jwtUtil.validateToken(username, userDetails, token); if (isValid) {
	 * tokenBlacklistService.blacklistToken(token); // return
	 * ResponseEntity.ok(Map.of("message", "Logged out successfully")); return
	 * ResponseEntity.ok(Map.of("message", "Logged out successfully", "token",
	 * token));
	 * 
	 * } else { return ResponseEntity.badRequest().body(Map.of("message",
	 * "Invalid or expired token")); } }
	 */

}
