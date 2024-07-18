package kr.co.seoultel.message.mt.mms.core_module.modules.redis;

import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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


    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String get(String key, String hashKey) {
        return (String) redisTemplate.opsForHash().get(key, hashKey);
    }


    public Optional<String> getOptional(String key) {
        return Optional.of(String.valueOf(redisTemplate.opsForValue().get(key)));
    }

    public Optional<String> getOptional(String key, String hashKey) {
        return Optional.of(String.valueOf(redisTemplate.opsForHash().get(key, hashKey)));
    }



    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Long delete(String key, String hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }


    public Boolean hasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

}
