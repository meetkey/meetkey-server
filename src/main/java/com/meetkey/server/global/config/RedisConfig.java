package com.meetkey.server.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.data.redis.chat.host}")
    private String chatHost;
    @Value("${spring.data.redis.auth.host}")
    private String authHost;

    @Value("${spring.data.redis.chat.port:6379}")
    private int chatPort;
    @Value("${spring.data.redis.auth.port:6379}")
    private int authPort;

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    // 채팅용 (port: 6379)
    @Bean
    @Primary
    public RedisConnectionFactory chatRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(chatHost);
        config.setPort(chatPort);

        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        return factory;
    }

    // SMS/Refresh토큰용 Redis
    @Bean
    public RedisConnectionFactory authRedisConnectionFactory(){
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(authHost);
        config.setPort(authPort);

        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("chatRedisConnectionFactory") RedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }

    // SMS, RefreshToken용
    @Bean
    public StringRedisTemplate stringRedisTemplate(
            @Qualifier("authRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    // OIDC 공개용 키 저장
    @Bean
    public RedisCacheManager oidcCacheManager(
            @Qualifier("authRedisConnectionFactory")  RedisConnectionFactory connectionFactory
    ){
        ObjectMapper cacheMapper = new ObjectMapper();
        RecordSupportingTypeResolver typeResolver = new RecordSupportingTypeResolver(ObjectMapper.DefaultTyping.NON_FINAL, cacheMapper.getPolymorphicTypeValidator());
        StdTypeResolverBuilder initializedResolver = typeResolver.init(JsonTypeInfo.Id.CLASS, null);
        initializedResolver = initializedResolver.inclusion(JsonTypeInfo.As.PROPERTY);
        cacheMapper.setDefaultTyping(initializedResolver);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(cacheMapper))
                )
                // 키 앞에 "jwk:" 접두사를 붙임
                .computePrefixWith(cacheName -> "jwk:" + cacheName + "::")
                .entryTtl(Duration.ofDays(3));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

}


