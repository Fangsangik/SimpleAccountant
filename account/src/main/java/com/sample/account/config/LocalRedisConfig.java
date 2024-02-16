package com.sample.account.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
public class LocalRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;
    private RedisServer server;

    @PostConstruct
    public void startRedis(){
        server = new RedisServer(redisPort);
        server.start();
    }

    @PreDestroy
    public void endRedis(){
        if (server != null){
            server.stop();
        }
    }
}
