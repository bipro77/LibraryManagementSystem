//package com.bookman.lms.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//
//@Configuration
//public class RedisConfig {
//
//	@Bean
//	public LettuceConnectionFactory redisConnectionFactory() {
//		return new LettuceConnectionFactory(); // Defaults to localhost:6379
//	}
//
//	@Bean
//	public RedisTemplate<String, String> redisTemplate() {
//		RedisTemplate<String, String> redis = new RedisTemplate<>();
//		redis.setConnectionFactory(redisConnectionFactory());
//		return redis;
//	}
//}
