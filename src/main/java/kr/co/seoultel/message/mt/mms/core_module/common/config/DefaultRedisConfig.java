package kr.co.seoultel.message.mt.mms.core_module.common.config;

import kr.co.seoultel.message.mt.mms.core.interfaces.Checkable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter @Setter
public abstract class DefaultRedisConfig implements Checkable {
    protected List<String> nodes;
    protected String password;

    @Override
    public void check() {
        log.info("[REDIS-CONFIG] {}", this);
    }

    @Override
    public String toString() {
        return "RedisConfig{" +
                "nodes=" + nodes +
                ", password='" + password + '\'' +
                '}';
    }
}

