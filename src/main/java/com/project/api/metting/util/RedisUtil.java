package com.project.api.metting.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public static long toTomorrow() {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plus(1, ChronoUnit.DAYS);
        return Duration.between(now.atStartOfDay(), tomorrow.atStartOfDay()).getSeconds();
    }


    public <T> Optional<T> getData(final String key, final Class<T> classType) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final String value = valueOperations.get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(objectMapper.readValue(value, classType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void setDataExpire(final String key, T value, final long durationMillis) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final Duration expireDuration = Duration.ofMillis(durationMillis);
        try {
            valueOperations.set(key, objectMapper.writeValueAsString(value), expireDuration);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void flushAll() {
        requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands();
    }

}
