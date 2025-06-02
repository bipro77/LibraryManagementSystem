package com.bookman.lms.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookman.lms.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class JwtAuthController {
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JwtUtil jwtUtil;

	@PostMapping("login")
	public String generateToken(@RequestBody Map<String, String> loginData) {
		String username = loginData.get("username");
		String password = loginData.get("password");

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			// if the user get authenticated then generate JWT token
			return jwtUtil.generateToken(username);

		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}

	@GetMapping("logout")
	public ResponseEntity<String> logout() {
		SecurityContextHolder.clearContext();
		return ResponseEntity.ok("Logged out successfully. Token invalidated.");
	}

}
