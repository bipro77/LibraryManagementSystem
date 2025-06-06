package com.bookman.lms.controller;

import java.util.Collections;
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

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		String token = jwtUtil.getJwtTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.badRequest().body(Map.of("message", "No token found in request"));
		}

		String username = jwtUtil.extractUsername(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		boolean isValid = jwtUtil.validateToken(username, userDetails, token);
		if (isValid) {
			tokenBlacklistService.blacklistToken(token);
			// return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
			return ResponseEntity.ok(Map.of("message", "Logged out successfully", "token", token));

		} else {
			return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token"));
		}
	}

}
