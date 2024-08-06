package com.project.api.metting.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public static long toTomorrow() {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plus(1, ChronoUnit.DAYS);
        return Duration.between(now.atStartOfDay(), tomorrow.atStartOfDay()).getSeconds();
    }

    public <T> Optional<T> getData(final String key, final Class<T> classType) {
        log.info("Attempting to get data from Redis with key: {}", key);
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final String value = valueOperations.get(key);
        if (value == null) {
            log.warn("No data found in Redis for key: {}", key);
            return Optional.empty();
        }
        try {
            T data = objectMapper.readValue(value, classType);
            log.info("Data retrieved from Redis for key: {}: {}", key, data);
            return Optional.ofNullable(data);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON from Redis for key: {}", key, e);
            throw new RuntimeException(e);
        }
    }


    public <T> void setDataExpire(final String key, T value, final long durationMillis) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final Duration expireDuration = Duration.ofMillis(durationMillis);
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            log.info("Setting data in Redis with key: {} and value: {}", key, jsonValue);
            valueOperations.set(key, jsonValue, expireDuration);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON to Redis for key: {}", key, e);
            throw new RuntimeException(e);
        }
    }

    public void flushAll() {
        log.info("Flushing all data from Redis");
        requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }
}