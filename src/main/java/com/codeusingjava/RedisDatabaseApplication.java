package com.codeusingjava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class RedisDatabaseApplication implements CommandLineRunner {

    @Autowired
    private RedisTemplate redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(RedisDatabaseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        redisTemplate.opsForValue().set("spring-r-cluster-1", 123);
        redisTemplate.opsForValue().set("spring-r-cluster-2", 456);
        redisTemplate.opsForValue().set("spring-r-cluster-3", 789);
    }
}
