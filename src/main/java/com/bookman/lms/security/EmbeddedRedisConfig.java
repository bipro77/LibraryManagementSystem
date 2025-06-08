package com.bookman.lms.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Component
@Profile("dev")
public class EmbeddedRedisConfig {

	private RedisServer redisServer = new RedisServer(6379);

	@PostConstruct
	public void startRedis() {
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		redisServer.stop();
	}
}