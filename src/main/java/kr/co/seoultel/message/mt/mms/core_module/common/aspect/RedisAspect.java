package kr.co.seoultel.message.mt.mms.core_module.common.aspect;

import io.lettuce.core.RedisException;
import kr.co.seoultel.message.mt.mms.core_module.modules.redis.RedisConnectionChecker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;

@Slf4j
@Aspect
public class RedisAspect {
    @AfterThrowing(pointcut = "execution(* kr.co.seoultel.message.mt.mms.core_module.modules.redis.RedisService.*(..))", throwing = "ex")
    public void handleRedisException(Exception ex) {
        if (ex instanceof RedisException || ex instanceof QueryTimeoutException || ex instanceof RedisConnectionFailureException) {
            RedisConnectionChecker.setConnected(false);
            log.error("[SYSTEM] Disconnected to redis");
        } else {
            log.error(ex.getMessage(), ex);
        }
    }
}
