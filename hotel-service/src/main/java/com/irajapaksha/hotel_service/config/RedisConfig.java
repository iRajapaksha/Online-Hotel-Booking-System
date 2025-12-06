//package com.irajapaksha.hotel_service.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//
//import java.time.Duration;
//
//@Configuration
//@EnableCaching
//public class RedisConfig {
//
//    @Value("${redis.host}")
//    private String redisHost;
//
//    @Value("${redis.port}")
//    private int redisPort;
//
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration cfg = new RedisStandaloneConfiguration(redisHost, redisPort);
//        return new LettuceConnectionFactory(cfg);
//    }
//
//    @Bean
//    public CacheManager cacheManager(LettuceConnectionFactory lcf) {
//        RedisCacheConfiguration cfg = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(5)); // short TTL for availability
//        return RedisCacheManager.builder(lcf).cacheDefaults(cfg).build();
//    }
//}