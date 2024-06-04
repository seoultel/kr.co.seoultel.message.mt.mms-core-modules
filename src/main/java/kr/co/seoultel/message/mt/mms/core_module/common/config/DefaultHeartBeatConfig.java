package kr.co.seoultel.message.mt.mms.core_module.common.config;

import kr.co.seoultel.message.mt.mms.core.common.interfaces.Checkable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
@Getter
public abstract class DefaultHeartBeatConfig implements Checkable {

    public static String HOST;
    public static int PORT;
    public static int RECONNECT_TIME;
    public static int EXPIRE_TIME;


    @Override
    @PostConstruct
    public void check() {
        Objects.requireNonNull(HOST);

        log.info("[HEART-BEAT-CONFIG] {}", this);
    }

    @Override
    public String toString() {
        return "HeartBeatConfig{" +
                "host='" + HOST + '\'' +
                ", port=" + PORT +
                ", reconnectTime=" + RECONNECT_TIME +
                ", expireTime=" + EXPIRE_TIME +
                '}';
    }
}
