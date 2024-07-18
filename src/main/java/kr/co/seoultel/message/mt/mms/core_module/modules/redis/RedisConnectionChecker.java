package kr.co.seoultel.message.mt.mms.core_module.modules.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

@Slf4j
public class RedisConnectionChecker {

    @Getter
    @Setter
    public static boolean isConnected = false;

    protected final RedisConnectionFactory redisConnectionFactory;


    public RedisConnectionChecker(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Scheduled(fixedDelay = 3000L)
    public void scheduler1() {
        redisConnectionIsAlive();
    }

    public void redisConnectionIsAlive() {
        try {
            String ping = redisConnectionFactory.getConnection().ping();
            isConnected = Objects.requireNonNullElse(ping, "").equalsIgnoreCase("PONG");
        } catch (Exception e) {
            isConnected = false;
        }
    }
}
