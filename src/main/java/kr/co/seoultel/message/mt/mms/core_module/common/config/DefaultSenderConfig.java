package kr.co.seoultel.message.mt.mms.core_module.common.config;


import kr.co.seoultel.message.mt.mms.core.common.interfaces.Checkable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
@Getter
public abstract class DefaultSenderConfig implements Checkable {

    public static String NAME;
    public static String GROUP;
    public static String TELECOM;
    public static boolean IS_DUMMY;


    @Value("${sender.name}")
    public void setName(String name) {
        NAME = name;
    }

    @Value("${sender.group}")
    public void setGroup(String group) {
        GROUP = group;
    }

    @Value("${sender.telecom}")
    public void setTelecom(String telecom) {
        TELECOM = telecom;
    }

    @Value("${sender.is-dummy}")
    public void setDummy(boolean isDummy) {
        IS_DUMMY = isDummy;
    }

    @Override
    @PostConstruct
    public void check() {
        Objects.requireNonNull(NAME);
        Objects.requireNonNull(GROUP);
        Objects.requireNonNull(TELECOM);

        log.info("[SENDER-CONFIG] {}", this);
    }

    @Override
    public String toString() {
        return "SenderConfig{" +
                    "NAME='" + NAME + '\'' +
                    ", GROUP='" + GROUP + '\'' +
                    ", TELECOM='" + TELECOM + '\'' +
                    ", IS_DUMMY=" + IS_DUMMY +
                '}';
    }
}
