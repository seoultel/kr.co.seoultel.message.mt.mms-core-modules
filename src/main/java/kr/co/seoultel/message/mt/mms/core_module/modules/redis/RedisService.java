package kr.co.seoultel.message.mt.mms.core_module.modules.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.RedisException;
import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core_module.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;



@Slf4j
public class RedisService {
    protected transient final Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();


    protected final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /*
     * ================================================
     *     *  SAVE :: Save Message To Redis *
     * ================================================
     */

    /*
     * MessageDelivery 를 final 로 선언하지 않았을 떄, java.util.ConcurrentModificationException 가 발생한다.
     */
    public void saveMessageByUmsMsgId(String umsMsgId, MessageDelivery messageDelivery) throws RedisException, QueryTimeoutException, RedisConnectionFailureException {
        try {
            String serializedMessage = this.gson.toJson(messageDelivery);
            this.redisTemplate.opsForHash().putIfAbsent(RedisUtil.getRedisKeyOfMessage(), umsMsgId, serializedMessage);
            log.info("[SUBMIT] MMS SENDER, GUARANTEE PERSISTENCE OF MESSAGE[umsMsgId : {}]", messageDelivery.getUmsMsgId());
        } catch (RedisConnectionFailureException | RedisCommandTimeoutException | QueryTimeoutException | RedisConnectionException var4) {
            log.error("[REDIS-CONNECTION-EXCEPTION] MESSAGE SAVED ONLY HASHMAP [{}]", umsMsgId);
        } catch (Exception var5) {
            log.error("[REDIS] Failed to save message[{}] in redis", messageDelivery, var5);
        }
    }

    public void removeMessageByUmsMsgId(String umsMsgId) throws RedisException, QueryTimeoutException, RedisConnectionFailureException {
        if (isExistsMessageByUmsMsgId(umsMsgId)) {
            try {
                this.redisTemplate.opsForHash().delete(RedisUtil.getRedisKeyOfMessage(), umsMsgId);
                log.info("[MMS SENDER, GUARANTEE REMOVED] Message[umsMsgId : {}] REMOVED IN \"HASHMAP\" | \"REDIS\"", umsMsgId);
            } catch (RedisConnectionFailureException | RedisCommandTimeoutException | QueryTimeoutException | RedisConnectionException var3) {
                log.error("[REDIS-CONNECTION-EXCEPTION] MESSAGE DELETED ONLY HASHMAP [{}]", umsMsgId);
            }
        } else {
            log.error("[REDIS] Failed to remove message[umsMsgId : {}] to redis", umsMsgId);
        }

    }

    public Optional<MessageDelivery> findMessageByUmsMsgIdToRedis(String umsMsgId) throws RedisException, QueryTimeoutException, RedisConnectionFailureException {
        MessageDelivery messageDelivery = null;
        if (this.isExistsMessageByUmsMsgId(umsMsgId)) {
            try {
                String json = (String)this.redisTemplate.opsForHash().get(RedisUtil.getRedisKeyOfMessage(), umsMsgId);
                messageDelivery = (MessageDelivery) ConvertorUtil.convertJsonToObject(json, MessageDelivery.class);
            } catch (Exception var4) {
                log.info("[EXCEPTION] MESSAGE[umsMsgId: {}] FAIL TO FIND MESSAGE[{}] IN \"HASH-MAP\" & \"REDIS\"", umsMsgId, umsMsgId, var4);
            }
        }

        return messageDelivery == null ? Optional.empty() : Optional.of(messageDelivery);
    }

    public boolean updateMessageByUmsMsgIdToRedis(String umsMsgId, MessageDelivery messageDelivery, Duration duration) throws RedisException, QueryTimeoutException, RedisConnectionFailureException {
        String serializedMessage = this.gson.toJson(messageDelivery);

        try {
            this.redisTemplate.opsForHash().put(RedisUtil.getRedisKeyOfMessage(), umsMsgId, serializedMessage);
            return true;
        } catch (Exception var6) {
            log.info("[EXCEPTION] MESSAGE[umsMsgId: {}] FAIL TO UPDATE MESSAGE IN \"REDIS\"", umsMsgId, var6);
            return false;
        }
    }

    public boolean isExistsMessageByUmsMsgId(String umsMsgId) throws RedisException, QueryTimeoutException, RedisConnectionFailureException {
        try {
            return this.redisTemplate.opsForHash().hasKey(RedisUtil.getRedisKeyOfMessage(), umsMsgId);
        } catch (RedisConnectionFailureException | RedisCommandTimeoutException | QueryTimeoutException | RedisConnectionException var3) {
            log.error("[DISCONNECTED TO REDIS] DIDN'T KNOW MESSAGE[umsMsgId: {}] IN REDIS", umsMsgId);
        } catch (Exception var4) {
            log.info("[EXCEPTION] DIDN'T KNOW MESSAGE[umsMsgId: {}] IN REDIS", umsMsgId);
        }

        return false;
    }



    /* IMAGES */
    public boolean isExistsImage(String key, String hashKey) throws RedisException, QueryTimeoutException, RedisConnectionFailureException {
        try {
            return Boolean.TRUE.equals(this.redisTemplate.opsForHash().hasKey(key, hashKey));
        } catch (RedisConnectionFailureException | RedisCommandTimeoutException | QueryTimeoutException | RedisConnectionException var4) {
            log.error("[REDIS-CONNECTION-EXCEPTION] DIDN'T EXISTS IMAGE[{}] IN REDIS", key);
        } catch (Exception var5) {
            log.error(var5.getMessage());
        }

        return false;
    }

    public boolean isExpiredImage(String groupCode, String imageId) throws RedisException, QueryTimeoutException, RedisConnectionFailureException {
        if (RedisConnectionChecker.isConnected()) {
            return !isExistsImage(groupCode, imageId); // hasImage -> not expried
        }

        return false;
    }
}
