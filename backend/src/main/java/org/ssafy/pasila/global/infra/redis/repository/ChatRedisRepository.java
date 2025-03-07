package org.ssafy.pasila.global.infra.redis.repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.ssafy.pasila.domain.apihandler.ErrorCode;
import org.ssafy.pasila.domain.apihandler.RestApiException;
import org.ssafy.pasila.global.infra.redis.entity.ChatRedis;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    public List<ChatRedis> findByLiveId(String key) throws JsonProcessingException {
        if(key == null) return null;

        List<String> valueList = redisTemplate.opsForList().range("chat:" + key, 0, -1);

        if(valueList == null || valueList.isEmpty()) return null;

        return valueList.stream()
                .map(this::deserialization)
                .collect(Collectors.toList());
    }

    public String recentChat(String key) {
        if(key == null) return null;

        List<String> valueList = redisTemplate.opsForList().range("chat:" + key, 0, -2);

        if(valueList == null || valueList.isEmpty()) return null;

        String result = valueList.stream().reduce("", (a, b) -> a + b + "\n");
        redisTemplate.delete("chat:" + key);

        return result;
    }

    public void saveChat(String key, ChatRedis chat) throws JsonProcessingException {
        redisTemplate.opsForList().rightPush("chat:" + key, objectMapper.writeValueAsString(chat));
    }

    public ChatRedis deserialization(String value) {
        try {
            return objectMapper.readValue(value, ChatRedis.class);
        } catch (JsonProcessingException e) {
            throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
