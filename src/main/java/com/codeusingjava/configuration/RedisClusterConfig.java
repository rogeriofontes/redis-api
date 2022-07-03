package com.codeusingjava.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisClusterConfig {

    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate redisClusterTemplate(@Value("${spring.custome.redis.cluster.nodes}") String host,
                                              @Value("${spring.custome.redis.cluster.password}") String password,
                                              @Value("${spring.custome.redis.cluster.timeout}") long timeout,
                                              @Value("${spring.custome.redis.cluster.max-redirects}") int maxRedirect,
                                              @Value("${spring.custome.redis.cluster.max-active}") int maxActive,
                                              @Value("${spring.custome.redis.cluster.max-wait}") int maxWait,
                                              @Value("${spring.custome.redis.cluster.max-idle}") int maxIdle,
                                              @Value("${spring.custome.redis.cluster.min-idle}") int minIdle) {


        JedisConnectionFactory connectionFactory =  jedisClusterConnectionFactory(host, password,
                timeout, maxRedirect, maxActive, maxWait, maxIdle, minIdle);
        return createRedisClusterTemplate(connectionFactory);
    }

    private JedisConnectionFactory jedisClusterConnectionFactory(String host, String password,
                                                                 long timeout, int maxRedirect, int maxActive, int maxWait, int maxIdle, int minIdle) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        List nodeList = new ArrayList();
        String[] cNodes = host.split(",");

        for (String node : cNodes) {
            String[] hp = node.split(":");
            nodeList.add(new RedisNode(hp[0], Integer.parseInt(hp[1])));
        }
        redisClusterConfiguration.setClusterNodes(nodeList);
        //redisClusterConfiguration.setPassword(password);
        redisClusterConfiguration.setMaxRedirects(maxRedirect);

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
        genericObjectPoolConfig.setTestWhileIdle(true);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(300000);

        JedisClientConfiguration.DefaultJedisClientConfigurationBuilder builder = (JedisClientConfiguration.DefaultJedisClientConfigurationBuilder) JedisClientConfiguration
                .builder();
        builder.connectTimeout(Duration.ofSeconds(timeout));
        builder.usePooling();
        builder.poolConfig(genericObjectPoolConfig);
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisClusterConfiguration, builder.build());
        connectionFactory.afterPropertiesSet();

        return connectionFactory;
    }


    private RedisTemplate createRedisClusterTemplate(JedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
