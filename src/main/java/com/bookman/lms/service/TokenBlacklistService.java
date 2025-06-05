package com.bookman.lms.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

	// In-memory storage for blacklisted tokens
	private final Set<String> blacklistedTokens = new HashSet<>();

	/**
	 * Add token to blacklist
	 * 
	 * @param token JWT token to blacklist
	 */
	public void blacklistToken(String token) {
		blacklistedTokens.add(token);
	}

	/**
	 * Check if token is blacklisted
	 * 
	 * @param token JWT token
	 * @return true if token is blacklisted
	 */
	public boolean isTokenBlacklisted(String token) {
		return blacklistedTokens.contains(token);
	}
}
