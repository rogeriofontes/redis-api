package com.codeusingjava;

import com.codeusingjava.repo.ShoppingDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@SpringBootApplication
@EnableAutoConfiguration(exclude = RedisAutoConfiguration.class)
public class RedisDatabaseApplication implements CommandLineRunner {

    @Autowired
    private ShoppingDao shoppingDao;

    public static void main(String[] args) {
        SpringApplication.run(RedisDatabaseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Subiu o servidor redis {}", shoppingDao.findAll());
        //redisTemplate.opsForValue().set("spring-r-cluster-1", 123);
        //redisTemplate.opsForValue().set("spring-r-cluster-2", 456);
        //redisTemplate.opsForValue().set("spring-r-cluster-3", 789);
    }
}
