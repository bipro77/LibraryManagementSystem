package com.bookman.lms.security;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final String SECRET = "a-string-secret-at-least-256-bits-long";
	private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
	Date now = new Date();
	private final long JWT_EXPIRATION_MS = TimeUnit.HOURS.toMillis(1);
	Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS); // 1hour from now

	public String generateToken(String username) {

		return Jwts.builder().claims().subject(username).issuedAt(now).expiration(expiryDate).and().signWith(key)
				.compact();

	}

	public String extractUsername(String token) {
		Claims body = extractedClaims(token);
		return body.getSubject();
	}

	private Claims extractedClaims(String token) {
		Claims body = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return body;
	}

	private boolean tokenIsExpired(String token) {
		boolean state = extractedClaims(token).getExpiration().before(now);
		return state;
	}

	public boolean validateToken(String username, UserDetails userDetails, String token) {
		// TRUE if username is same as username in UserDetails and token is not expired
		return username.equals(userDetails.getUsername()) && !tokenIsExpired(token);
	}

}
