package io.github.catimental.diexample.publisher;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;

@Service
@RequiredArgsConstructor
public class RedisStreamPublisher implements StreamPublisher {
 
    private final StringRedisTemplate redisTemplate;

    @Override
    public void publish(String streamKey, Map<String, String> payload){
        try{
            redisTemplate.opsForStream().add(streamKey, payload);
        }catch(Exception e){
            throw new RuntimeException("Failed to publish event to Redis Stream", e);
        }
    }

}
