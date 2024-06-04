package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.rabbitMq;

import lombok.Getter;

public enum NAckType {
    ACK("ACK"), NACK("NACK");

    @Getter
    private final String type;

    NAckType(String type) {
        this.type = type;
    }
}
