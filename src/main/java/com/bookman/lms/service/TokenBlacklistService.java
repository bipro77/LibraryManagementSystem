package com.bookman.lms.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

	@Autowired
	private StringRedisTemplate redisTemplate;
//	private RedisTemplate<String, String> redisTemplate;

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
