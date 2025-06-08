package com.bookman.lms.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private static final String TOKEN_BLACKLIST_PREFIX = "blacklisted_token:";

	/**
	 * Blacklists a JWT token by storing it in Redis with a custom expiration time.
	 *
	 * @param token            The JWT token to blacklist.
	 * @param expirationMillis The time in milliseconds after which the token should
	 *                         expire from Redis.
	 */
	public void blacklistToken(String token, long expirationMillis) {
		String key = TOKEN_BLACKLIST_PREFIX + token;
		redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofMillis(expirationMillis));
	}

	/**
	 * Blacklists a JWT token with a default expiration time of 1 hour.
	 *
	 * @param token The JWT token to blacklist.
	 */
	public void blacklistToken(String token) {
		// fallback default: 1 hour TTL if expiration not provided
		blacklistToken(token, 3600_000);
	}

	/**
	 * Checks if a JWT token is blacklisted by looking it up in Redis.
	 *
	 * @param token The JWT token to check.
	 * @return true if the token is blacklisted (exists in Redis), false otherwise.
	 */
	public boolean isTokenBlacklisted(String token) {
		String key = TOKEN_BLACKLIST_PREFIX + token;
		return redisTemplate.hasKey(key);
	}
}

//package com.bookman.lms.service;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class TokenBlacklistService {
//
//	// In-memory storage for blacklisted tokens
//	private final Set<String> blacklistedTokens = new HashSet<>();
//
//	/**
//	 * Add token to blacklist
//	 * 
//	 * @param token JWT token to blacklist
//	 */
//	public void blacklistToken(String token) {
//		blacklistedTokens.add(token);
//	}
//
//	/**
//	 * Check if token is blacklisted
//	 * 
//	 * @param token JWT token
//	 * @return true if token is blacklisted
//	 */
//	public boolean isTokenBlacklisted(String token) {
//		return blacklistedTokens.contains(token);
//	}
//}
