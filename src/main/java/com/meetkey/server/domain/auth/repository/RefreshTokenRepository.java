package com.meetkey.server.domain.auth.repository;

import com.meetkey.server.domain.auth.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "refreshToken:";

    public void save(final RefreshToken refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = PREFIX + refreshToken.getRefreshToken();
        valueOperations.set(
                key,
                String.valueOf(refreshToken.getMemberId()),
                7,
                TimeUnit.DAYS
        );
    }

    public Optional<RefreshToken> findById(final String refreshToken){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String memberId = valueOperations.get(PREFIX + refreshToken);

        if (Objects.isNull(memberId)){
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(refreshToken,memberId));
    }

    public void delete(final String refreshToken) {
        String key = PREFIX + refreshToken;

        redisTemplate.delete(key);
    }
}