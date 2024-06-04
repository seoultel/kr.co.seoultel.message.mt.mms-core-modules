package kr.co.seoultel.message.mt.mms.core_module.common.config;

import kr.co.seoultel.message.mt.mms.core.interfaces.Checkable;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
public abstract class DefaultDataVaultConfig implements Checkable {

    public static String PERSISTENCE_FILE_PATH;
    public static String REPUBLISH_FILE_PATH;
    public static String MESSAGE_HISTORIES_FILE_PATH;
    public static String REPORT_FILE_PATH;


    @Override
    @PostConstruct
    public void check() {
        Objects.requireNonNull(PERSISTENCE_FILE_PATH);
        Objects.requireNonNull(REPUBLISH_FILE_PATH);
        Objects.requireNonNull(MESSAGE_HISTORIES_FILE_PATH);
        Objects.requireNonNull(REPORT_FILE_PATH);

        log.info("[DATA-VAULT-CONFIG] {}", this);
    }


    @Override
    public String toString() {
        return "DataVaultConfig{" +
                "PERSISTENCE_FILE_PATH='" + PERSISTENCE_FILE_PATH + '\'' +
                ", REPUBLISH_FILE_PATH='" + REPUBLISH_FILE_PATH + '\'' +
                ", MESSAGE_HISTORIES_FILE_PATH='" + MESSAGE_HISTORIES_FILE_PATH + '\'' +
                ", REPORT_FILE_PATH='" + REPORT_FILE_PATH + '\'' +
                '}';
    }
}
