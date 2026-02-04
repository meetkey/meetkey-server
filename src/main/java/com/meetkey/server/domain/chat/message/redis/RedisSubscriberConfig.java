package com.meetkey.server.domain.chat.message.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscriberConfig {

    private final RedisConnectionFactory connectionFactory;
    private final ChatRedisSubscriber chatRedisSubscriber;

    public RedisSubscriberConfig(
            @Qualifier("chatRedisConnectionFactory") RedisConnectionFactory connectionFactory,
            ChatRedisSubscriber chatRedisSubscriber) {
        this.connectionFactory = connectionFactory;
        this.chatRedisSubscriber = chatRedisSubscriber;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                chatRedisSubscriber,
                new PatternTopic("chat.room.*")
        );

        return container;
    }
}

