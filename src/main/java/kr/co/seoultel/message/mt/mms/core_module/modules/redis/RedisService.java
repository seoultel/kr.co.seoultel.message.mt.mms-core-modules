package kr.co.seoultel.message.mt.mms.core_module.modules.redis;

import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {

    protected final RedisTemplate<String, Object> redisTemplate;


    public void put(String key, Object object) {
        String json = ConvertorUtil.convertObjectToJson(object);
        redisTemplate.opsForValue().set(key, json);
    }

    public void put(String key, String hashKey, Object object) {
        String json = ConvertorUtil.convertObjectToJson(object);
        redisTemplate.opsForHash().put(key, hashKey, json);
    }

    public void putSafely(String key, Object object) {
        if (RedisConnectionChecker.isConnected()) {
            String json = ConvertorUtil.convertObjectToJson(object);
            redisTemplate.opsForValue().set(key, json);
        }
    }

    public void putSafely(String key, String hashKey, Object object) {
        if (RedisConnectionChecker.isConnected()) {
            String json = ConvertorUtil.convertObjectToJson(object);
            redisTemplate.opsForHash().put(key, hashKey, json);
        }
    }


    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String get(String key, String hashKey) {
        return (String) redisTemplate.opsForHash().get(key, hashKey);
    }


    public Optional<String> getSafely(String key) {
        if (RedisConnectionChecker.isConnected()) {
            return Optional.ofNullable((String) redisTemplate.opsForValue().get(key));
        }

        return Optional.empty();
    }

    public Optional<String> getSafely(String key, String hashKey) {
        if (RedisConnectionChecker.isConnected()) {
            return Optional.ofNullable((String) redisTemplate.opsForHash().get(key, hashKey));
        }

        return Optional.empty();
    }


    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Long delete(String key, String hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }


    public Boolean deleteSafely(String key) {
        if (RedisConnectionChecker.isConnected()) {
            return redisTemplate.delete(key);
        }

        return Boolean.FALSE;
    }

    public Long deleteSafely(String key, String hashKey) {
        if (RedisConnectionChecker.isConnected()) {
            return redisTemplate.opsForHash().delete(key, hashKey);
        }

        return 0L;
    }


    public Boolean hasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public Boolean hasKeySafely(String key, String hashKey) {
        if (RedisConnectionChecker.isConnected()) {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        }

        return Boolean.FALSE;
    }
}
